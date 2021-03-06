# -*- coding: utf-8 -*-
import md5
from flask import Flask, request, make_response, Response
import json, os, psycopg2, urlparse
from db import Db
from functools import wraps 
from datetime import datetime
from math import *
import random
from flask_cors import CORS
  
######################################
# OBLIGATOIRE SINON ERREUR
import sys
reload(sys)
sys.setdefaultencoding("latin-1")

app = Flask(__name__)
app.debug = True
CORS(app)
######################################

#-------------------------------- DEFINITION ---------------------------------------#
#---- Retour des reponses
def jsonResponse(data, status=200):
	return json.dumps(data), status, {'Content-Type': 'application/json'}

#---- Verification de l'authentification de l'admin
def verif_authentification_admin(nom, mot_de_passe):
	db = Db()
	result = db.select('SELECT COUNT(com_nom) AS nb FROM Compte '
			'WHERE com_nom=@(Username) AND com_mot_de_passe=@(Password) AND com_est_admin = True;', {
			'Username': nom,
			'Password': md5.new(mot_de_passe.encode('utf-8')).hexdigest()
			})
	db.close()
	return result[0]['nb'] >= 1

#---- Utilise pour le besoin d'authentification de l'admin
def besoin_authentification_admin(f):
	@wraps(f)
	def decorated(*args, **kwargs):
		auth = request.authorization
		if not auth or not verif_authentification_admin(auth.username, auth.password):
			return Response('Authentification FAIL', 401, {'WWW-Authenticate': 'Basic realm="Login Required"'})
		return f(*args, **kwargs)

	return decorated

#---------------------------------------- ROUTES -----------------------------------#
#---- Route pour initialiser la base de donnees en admin
@app.route('/admin/resetbase', methods=['GET'])
@besoin_authentification_admin
def route_dbinit():
	db = Db()
	db.executeFile("base.sql")
	db.close()
	return json.dumps("Done"), 200, {'Content-Type': 'application/json'}
	
#---- Route initiale
@app.route("/")
def formulaire():
	f = open('./static/index.html', 'r')
	html = f.read()
	return html

#------- login
@app.route("/login")
def log():
	return json.dumps("ok"), 200, {'Content-Type': 'application/json'}

#---- Route pour acceder a la page html
@app.route("/html")
def formulaire2():
	f = open('./static/form.html', 'r')
	html = f.read()
	return html

#---- Route pour acceder aux ventes
@app.route('/sales', methods=['POST']) 
def sales():
	db=Db()
	data = request.get_json()
	element = {}
	
	meteoJour = db.select("SELECT met_jour FROM meteo WHERE met_apres_midi IS NOT NULL ORDER BY met_jour DESC LIMIT 1")
	jour = meteoJour[0]['met_jour']
	prix=db.select("SELECT pro_prix_vente FROM produire WHERE jou_nom=@(nom) AND rec_nom=@(recette)",
		 {
			'nom' : data["player"],
			'recette' : data["item"]
		 })
	budget = db.select("SELECT jou_budget FROM Joueur WHERE jou_nom=@(nom)",{
			'nom' : data["player"]
		})	
	verif = db.select("SELECT * FROM vendre WHERE rec_nom=@(recette) AND ven_jour=@(jour) AND jou_nom=@(nom)",
		{ 
			'jour' : jour,
			'nom' : data["player"],
			'recette' : data["item"]
		})
	if len(verif)==0 :
		db.execute("INSERT INTO vendre VALUES (@(jour), @(quantite), @(nom), @(recette))",
				   { 
				    'jour' : jour,
				    'quantite' : data["quantity"],
				    'nom' : data["player"],
				    'recette' : data["item"]
				   })
		newBudget = budget[0]['jou_budget']+prix[0]["pro_prix_vente"]
		db.execute("UPDATE Joueur SET jou_budget=@(budget) WHERE jou_nom=@(nom)",
			  {
				  'nom' : data["player"],
				  'budget': newBudget
			  })
	else :
		qte=int(verif[0]["ven_quantite"])
		db.execute("UPDATE Joueur SET jou_budget=@(budget) WHERE jou_nom=@(nom)",
			  {
				  'nom' : data["player"],
				  'budget': budget[0]['jou_budget']+prix[0]["pro_prix_vente"]
			  })
		db.execute("UPDATE vendre SET ven_quantite=@(quantite)",
				   { 
				    'quantite' : qte + 1
				   })
		
	db.close()
	
	reponse = make_response(json.dumps("Vente enregistre"), 200, {'Content-Type': 'application/json'})
	return reponse

#---- Route qui gere les actions joueur
@app.route('/actions/<playerName>', methods=['POST'])
def action_player(playerName):
	data = request.get_json()
	simulation=data["simulated"]
	db=Db()
	reponse={}
	actions = []
	actions = data["actions"]
	coutTotal = 0.0
		
	meteoJour = db.select("SELECT met_jour FROM meteo WHERE met_apres_midi IS NOT NULL ORDER BY met_jour DESC LIMIT 1")
	jour = meteoJour[0]['met_jour']	
		
	for i in range(0,len(actions)) :
		kind = actions[i]["kind"]
		if kind == "drinks":
			boisson = actions[i]["prepare"].keys()[0]
			valeur = int(actions[i]["prepare"][boisson])
			prix = 0
			if actions[i]["price"][boisson]>0:
				prix = float(actions[i]["price"][boisson])
			
			coutProd = 0.0
			ingredient = {}
			cout=[]
			recettes=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)", 
				{
					'recette' : boisson,
					 'nom' : playerName
				}))

			ingredientRecette = recettes
			for ingredient in range(0,len(ingredientRecette)):
				cout += (db.select("SELECT ing_prix_unitaire FROM Ingredient WHERE ing_nom=@(ing)", 
						   {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				coutProd = coutProd + (cout[ingredient]['ing_prix_unitaire'] * valeur)	
			coutTotal=coutTotal+coutProd

			budget = db.select("SELECT jou_budget FROM Joueur WHERE jou_nom=@(nom)",{
					'nom' : playerName
					})	

			if budget[0]['jou_budget']>coutTotal:
				if simulation == False :
					db.execute("INSERT INTO produire (jou_nom,pro_jour,pro_prix_vente, pro_quantite, rec_nom) VALUES (@(nom),@(jour),@(prix),@(quantite),@(recette))", 
						{'nom' : playerName,
						'jour' : jour,
						'prix' : prix,
						'quantite' : valeur,
						'recette' : boisson
						})

					db.execute("UPDATE Joueur SET jou_budget = @(newBudget) WHERE jou_nom=@(nom)",{
					'newBudget': budget[0]['jou_budget']-int(coutTotal) ,
					'nom' : playerName
					})	
					reponse = {
						"sufficientFunds" : True,
						"totalCost" : coutTotal
					}
				reponse = {
					"sufficientFunds" : True,
					"totalCost" : coutTotal
				}
			else:
				reponse = {
					"sufficientFunds" : False,
					"totalCost" : coutTotal
				}

		elif kind == "ad":
			radius = actions[i]["radius"]
			budget = db.select("SELECT jou_budget FROM Joueur WHERE jou_nom=@(nom)",{
				'nom' : playerName
				})
			cout = (radius - 1) * 50
			newBudget = budget[0]["jou_budget"] - cout
			
			if newBudget > 0:
				db.execute("UPDATE Joueur SET jou_rayon = @(newrayon), jou_budget=@(budget) WHERE jou_nom=@(nom)",{
					'newrayon': radius ,
					'budget': newBudget,
					'nom' : playerName
				})
				reponse = {
					"sufficientFunds" : True,
					"totalCost" : cout
				}
			else:
				reponse = {
					"sufficientFunds" : False,
					"totalCost" : cout
				}
	db.close()
	return make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})

#---- Route qui permet de rejoindre une partie
# Route à tester
@app.route('/players',methods=['POST'])
def post_players():
	db = Db()
	data = request.get_json()
	username = data['name']
	verif = db.select("SELECT * FROM Joueur where jou_nom = @(nom)", {'nom' : username})
	if(len(verif) != 0) :
		return json.dumps("NOK"), 403, {'Content-Type': 'application/json'}
	
	else :
		#----------- VARIABLES POUR GENERER UN JOUEUR ------------------#
		budget = 6000.0
		posX =random.randint(0,100)*1.0
		posY=random.randint(0,100)*1.0
		rayon = 5
		actif = True
		#---------------------------------------------------------------#

		recettes = {}
		recette = {}
		drinksInfos = []
		db.execute("INSERT INTO Joueur(jou_nom,jou_budget,jou_pos_x, jou_pos_y, jou_rayon, jou_actif) VALUES (@(nom),@(budget),@(posX),@(posY),@(rayon),@(actif))", 
			{'nom' : username,
			'budget' : budget,
			'posX' : posX,
			'posY' : posY,
			'rayon' : rayon,
			'actif' : actif
		})
	
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'citron')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'eau gazeuse')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'sucre')",{'nom' : username})
		
		db.execute("INSERT INTO composer VALUES ('Chocolat_chaud', @(nom), 'chocolat')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Chocolat_chaud', @(nom), 'lait')",{'nom' : username})
		
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'rhum')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'eau gazeuse')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'sucre')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'menthe')",{'nom' : username})
		
		db.execute("INSERT INTO composer VALUES ('Vin_chaud', @(nom), 'raisin')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Vin_chaud', @(nom), 'eau')",{'nom' : username})
		db.execute("INSERT INTO composer VALUES ('Vin_chaud', @(nom), 'sucre')",{'nom' : username})
		
		recetteJoueur = db.select("SELECT * FROM Recette")
		for recette in range(0,len(recetteJoueur)):
			ingredient = {}
			cout=[]
			ingredientAlcool =[]
			ingredientFroid=[]
			coutProd = 0.0
			alcool = False
			froid = True
			recettes[recette]=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)", 
				{'recette' : recetteJoueur[recette]["rec_nom"], 'nom' : username}))
			
			coutProd = (db.select('SELECT sum(ing_prix_unitaire) AS price FROM Ingredient, Recette, Composer WHERE ingredient.ing_nom=composer.ing_nom AND '
						'recette.rec_nom=composer.rec_nom AND recette.rec_nom=@(recette)',
						{'recette' : recetteJoueur[recette]["rec_nom"]}))
	
			ingredientRecette = recettes[recette]
			for ing in range(0,len(ingredientRecette)):
				ingredientAF=(db.select("SELECT ing_alcool, ing_froid FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ing]["ing_nom"]}))
				if ingredientAF[0]['ing_alcool'] == True :
					alcool = True
				if ingredientAF[0]['ing_froid'] == False :
					froid = False
			
			drinkInfo = {}
			drinkInfo["name"] = recetteJoueur[recette]["rec_nom"]
			drinkInfo["price"] = coutProd[0]["price"]
			drinkInfo["hasAlcohol"] = alcool
			drinkInfo["isCold"] = froid
			drinksInfos += drinkInfo

		db.close()

		playerInfo = {}
		playerInfo["cash"] = budget
		playerInfo["sales"] = "0"
		playerInfo["profit"] = "0"
		playerInfo["drinksOffered"] = drinksInfos

		coordinates = {}
		coordinates["lattitude"] = posX
		coordinates["longitude"] = posY

		reponse = {}
		reponse["name"] = username
		reponse["location"] = coordinates
		reponse["info"] = playerInfo

		retour = make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})
		return retour
	
#---- Route metrology, enregistrment de la meteo dans la BDD
@app.route('/metrology',methods=['POST'])
def post_metrology():
	db = Db()
	arduino = request.get_json()

	weather = arduino['weather']
	timestamp = arduino ['timestamp']

	if timestamp == 1:
		matin = weather[0]['weather']
		aprem = weather[1]['weather']
			
		db.execute("INSERT INTO Meteo VALUES (@(jour), @(heure), @(matin), @(aprem))",
			{'jour' : 1,
			'heure' : timestamp,
			'matin' : matin,
			'aprem' : aprem
		})
		
	else:
		temps = timestamp / 24.0
		jour = int(temps) + 1
		reste = temps % 1
		
		if reste <=0.5:
			matin = weather[0]['weather']
			aprem = weather[1]['weather']			
			
			db.execute("UPDATE Meteo SET met_heure_ecoule=@(heure), met_matin=@(matin), met_apres_midi=@(aprem) WHERE met_jour=@(jour)",
				{'heure' : timestamp,
				'matin' : matin,
				'aprem' : aprem,
				'jour' : jour
			})

		else:
			aprem = weather[0]['weather']
			matin = weather[1]['weather']
			jourSup = jour + 1
			existe = db.select("SELECT * FROM Meteo WHERE met_jour=@(jour)",
				  {'jour' : jourSup})
			if len(existe) != 0 :
				db.execute("UPDATE Meteo SET met_heure_ecoule=@(heure), met_apres_midi=@(aprem) WHERE met_jour=@(jour)",
					{'heure' : timestamp,
					'aprem' : aprem,
					'jour' : jour
				})
				
				db.execute("UPDATE Meteo SET met_heure_ecoule=@(heure), met_matin=@(matin) WHERE met_jour=@(jour)",
					{'heure' : timestamp,
					'matin' : matin,
					'jour' : jourSup
				})

			else :
				db.execute("UPDATE Meteo SET met_heure_ecoule=@(heure), met_apres_midi=@(aprem) WHERE met_jour=@(jour)",
					{'heure' : timestamp,
					'aprem' : aprem,
					'jour' : jour
				})
				
				db.execute("INSERT INTO Meteo (met_jour, met_heure_ecoule, met_matin) VALUES (@(jour), @(heure), @(matin))",
					{'jour' : jourSup,
					'heure' : timestamp,
					'matin' : matin
				})
			
	db.close()		
	retour = make_response(json.dumps(arduino), 200, {'Content-Type': 'application/json'})
	return retour

#---- Route metrology, renvoie la meteo du jour
@app.route('/metrology',methods=['GET'])
def get_metrology():
	db = Db()
	meteo = db.select("SELECT met_heure_ecoule, met_matin, met_apres_midi FROM meteo ORDER BY met_jour DESC LIMIT 2")
	forecast = []
	reste = (meteo[0]['met_heure_ecoule'] / 24.0)%1
		
	if reste <=0.5:
		forecasts = {}
		forecasts["dfn"] = 1
		forecasts["weather"] = meteo[0]['met_apres_midi']
		forecast.append(forecasts)
		forecasts = {}
		forecasts["dfn"] = 0
		forecasts["weather"] = meteo[0]['met_matin']
		forecast.append(forecasts)
	else :
		forecasts = {}
		forecasts["dfn"] = 1
		forecasts["weather"] = meteo[0]['met_matin']
		forecast.append(forecasts)
		forecasts = {}
		forecasts["dfn"] = 0
		forecasts["weather"] = meteo[1]['met_apres_midi']
		forecast.append(forecasts)
		
	db.close()
	reponse = {}
	reponse["timestamp"] = meteo[0]['met_heure_ecoule']
	reponse["weather"] = forecast
	
	retour = make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})
	return retour

#---- Route ingredients, renvoie les informations de tout les ingredients
@app.route('/ingredients',methods=['GET'])
def get_ingredients():
	db = Db()
	ingredientInfo = db.select("SELECT * FROM ingredient")
	ingredient = []
	
	for ing in range(0,len(ingredientInfo)):
	
		ingredients={}
		ingredients["name"] = ingredientInfo[ing]['ing_nom']
		ingredients["cost"] = ingredientInfo[ing]['ing_prix_unitaire']
		ingredients["hasAlcohol"] = ingredientInfo[ing]['ing_alcool']
		ingredients["isCold"] = ingredientInfo[ing]['ing_froid']
		ingredient.append(ingredients)
	
	db.close()
	reponse = {}
	reponse["ingredients"] = ingredient
	
	retour = make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})
	return retour

#---- Permet de mettre tout les joueurs au même niveau (remise du budget initial et remise a zero des tables vendre, produire et pub)
@app.route('/reset',methods=['GET'])
def get_reset():
	db = Db()
	db.execute("TRUNCATE TABLE pub, produire, vendre")
	joueurs = db.select("SELECT * FROM joueur")
	
	for joueur in range(0,len(joueurs)):
		db.execute("UPDATE Joueur SET jou_budget=@(budget) WHERE jou_nom=@(nom)",
				{'budget' : 6000.0,
				'nom' : joueurs[joueur]['jou_nom']
			})

	db.close()
	return json.dumps("Reset OK"), 200, {'Content-Type': 'application/json'}

#---- Route qui supprime un joueur de la base de données
@app.route('/players/<playerName>', methods=['DELETE'])
def delete_player(playerName):
	db = Db()
	db.execute("DELETE FROM composer WHERE jou_nom=@(nom)", {'nom' : playerName})
	db.execute("DELETE FROM vendre WHERE jou_nom=@(nom)", {'nom' : playerName})
	db.execute("DELETE FROM pub WHERE jou_nom=@(nom)", {'nom' : playerName})
	db.execute("DELETE FROM produire WHERE jou_nom=@(nom)", {'nom' : playerName})
	db.execute("DELETE FROM joueur WHERE jou_nom=@(nom)", {'nom' : playerName})
	
	db.close()
	return json.dumps("Suppression joueur OK"), 200, {'Content-Type': 'application/json'}

#---- Route qui permet de récupérer la map d'un joueur
@app.route('/map/<playerName>',methods=['GET'])
def get_map_player(playerName):
	db = Db()
	ingredient = []
	drinksInfos = []
	recettes = []
	profit = 0.0
	
	meteoJour = db.select("SELECT met_jour FROM meteo WHERE met_apres_midi IS NOT NULL ORDER BY met_jour DESC LIMIT 1")
	jour = meteoJour[0]['met_jour']
	
	ingredientInfo = db.select("SELECT * FROM ingredient")
	for ing in range(0,len(ingredientInfo)):
		ingredients={}
		ingredients["name"] = ingredientInfo[ing]['ing_nom']
		ingredients["cost"] = ingredientInfo[ing]['ing_prix_unitaire']
		ingredients["hasAlcohol"] = ingredientInfo[ing]['ing_alcool']
		ingredients["isCold"] = ingredientInfo[ing]['ing_froid']
		ingredient.append(ingredients)
	
	budget = db.select("SELECT jou_budget FROM joueur WHERE jou_nom=@(nom)", {'nom':playerName})
	
	vente = (db.select("SELECT sum(ven_quantite) AS quantite FROM vendre WHERE jou_nom=@(nom) AND ven_jour=@(jour)",
					{'nom' : playerName, 'jour' : jour}))
	
	recetteJoueur = db.select("SELECT * FROM Recette")
	for recette in range(0,len(recetteJoueur)):
		ing = {}
		ingredientAlcool =[]
		ingredientFroid=[]
		coutProd = 0.0
		alcool = False
		froid = True
		prix = 0.0
		qte = 0
		nomRecette =  recetteJoueur[recette]["rec_nom"]
		
		recettes=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)",
				{'recette' : nomRecette, 'nom' : playerName}))
		coutVente = (db.select("SELECT pro_prix_vente AS price FROM produire WHERE jou_nom=@(nom) AND rec_nom=@(recette)",
				{'nom' : playerName, 'recette' : nomRecette}))
		venteR = (db.select("SELECT sum(ven_quantite) AS quantite FROM vendre WHERE jou_nom=@(nom) AND ven_jour=@(jour) AND rec_nom=@(recette)",
				{'nom' : playerName, 'jour' : jour, 'recette' : nomRecette}))
		
		if len(coutVente) != 0:
			prix=coutVente[0]["price"]
			if len(venteR) != 0 :
				qte=venteR[0]["quantite"]
				if qte is not None :
					profit = profit + prix * qte
		
		coutProd = (db.select('SELECT sum(ing_prix_unitaire) AS price FROM Ingredient, Recette, Composer WHERE ingredient.ing_nom=composer.ing_nom AND '
						'recette.rec_nom=composer.rec_nom AND recette.rec_nom=@(recette) AND composer.jou_nom=@(nom)',
						{'recette' : nomRecette, 'nom' : playerName}))

		ingredientRecette = recettes
		for ing in range(0,len(ingredientRecette)):		
			ingredientAF=(db.select("SELECT ing_alcool, ing_froid FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ing]["ing_nom"]}))
			if ingredientAF[0]['ing_alcool'] == True :
				alcool = True
			if ingredientAF[0]['ing_froid'] == False :
				froid = False
			
		drinkInfo = {}
		drinkInfo["name"] = nomRecette
		drinkInfo["price"] = coutProd[0]["price"]
		drinkInfo["hasAlcohol"] = alcool
		drinkInfo["isCold"] = froid
		drinksInfos.append(drinkInfo)
		
	riche=[]
	ventes = []
	numero = db.select("SELECT * FROM joueur ORDER BY jou_budget DESC")
	for chaque in range(0,len(numero)):
		riche.append(numero[chaque]["jou_nom"])
	
	mapItem = []
	
	stand = db.select("SELECT jou_nom, jou_pos_x, jou_pos_y, jou_rayon FROM joueur WHERE jou_nom=@(nom)", {'nom':playerName})
	coordinatesS={}
	coordinatesS["latitude"] = stand[0]['jou_pos_x']
	coordinatesS["longitude"] = stand[0]['jou_pos_y']
	
	mapItems={}
	mapItems["kind"] = "stand"
	mapItems["owner"] = playerName
	mapItems["location"] = coordinatesS
	mapItems["influence"] = stand[0]['jou_rayon']
	mapItem.append(mapItems)
	
	ads = db.select("SELECT pub_pos_x, pub_pos_y, pub_rayon, jou_nom FROM pub WHERE pub_jour=@(jour) AND jou_nom=@(nom)", {'jour':jour, 'nom':playerName})
	for a in range(0,len(ads)):
		coordinatesA={}
		coordinatesA["latitude"] = ads[a]['pub_pos_x']
		coordinatesA["longitude"] = ads[a]['pub_pos_y']
		
		mapItems={}
		mapItems["kind"] = "ad"
		mapItems["owner"] = playerName
		mapItems["location"] = coordinatesA
		mapItems["influence"] = ads[a]['pub_rayon']
		mapItem.append(mapItems)
	
	db.close()
	
	coordinates = {}
	coordinates["lattitude"] = 50.0
	coordinates["longitude"] = 50.0
	
	coordinatesSpan = {}
	coordinatesSpan["lattitudeSpan"] = 50.0
	coordinatesSpan["longitudeSpan"] = 50.0
	
	region = {}
	region["center"] = coordinates
	region["span"] = coordinatesSpan
	
	map = {}
	map["region"] = region
	map["ranking"] = riche
	map["itemsByPlayer"] = {playerName : mapItem}

	playerInfo = {}
	playerInfo["cash"] = budget[0]["jou_budget"]
	playerInfo["sales"] = vente[0]["quantite"]
	playerInfo["profit"] = profit
	playerInfo["drinksOffered"] = drinksInfos
	
	reponse = {}
	reponse["availableIngredients"] = ingredient
	reponse["map"] = map
	reponse["playerInfo"] = playerInfo
	
	retour = make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})
	return retour

#---- Route qui permet de récupérer la map d'un joueur
@app.route('/map',methods=['GET'])
def get_map():
	db = Db()
	ingredient = []
	drinksByPlayers = {}
	recettes = []
	playerInfos = {}
	itemsByPlayers = {}
	profit = 0.0
	
	meteoJour = db.select("SELECT met_jour FROM meteo WHERE met_apres_midi IS NOT NULL ORDER BY met_jour DESC LIMIT 1")
	jour = meteoJour[0]["met_jour"]
	
	joueurs = db.select("SELECT * FROM joueur")
	for joueur in range(0,len(joueurs)):
		drinksInfos = []
		playerName = joueurs[joueur]["jou_nom"]
	
		ingredientInfo = db.select("SELECT * FROM ingredient")
		for ing in range(0,len(ingredientInfo)):
			ingredients={}
			ingredients["name"] = ingredientInfo[ing]['ing_nom']
			ingredients["cost"] = ingredientInfo[ing]['ing_prix_unitaire']
			ingredients["hasAlcohol"] = ingredientInfo[ing]['ing_alcool']
			ingredients["isCold"] = ingredientInfo[ing]['ing_froid']
			ingredient.append(ingredients)
		
		budget = db.select("SELECT jou_budget FROM joueur WHERE jou_nom=@(nom)", {'nom':playerName})
		
		vente = (db.select('SELECT count(ven_quantite) AS quantite FROM vendre WHERE jou_nom=@(nom) AND ven_jour=@(jour)',
						{'nom' : playerName, 'jour' : jour}))
		
		recetteJoueur = db.select("SELECT * FROM Recette")
		for recette in range(0,len(recetteJoueur)):
			ing = {}
			ingredientAF =[]
			prix = 0.0
			qte = 0
			alcool = False
			froid = True
			nomRecette =  recetteJoueur[recette]["rec_nom"]
		
			
			recettes=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)", 
				{'recette' : nomRecette, 'nom' : playerName}))
			coutVente = (db.select('SELECT pro_prix_vente AS price FROM produire WHERE pro_jour=@(jour) AND jou_nom=@(nom) AND rec_nom=@(recette)',
						{'jour' : jour, 'nom' : playerName, 'recette' : nomRecette}))
			venteR = (db.select('SELECT count(ven_quantite) AS quantite FROM vendre WHERE jou_nom=@(nom) AND ven_jour=@(jour) AND rec_nom=@(recette)',
						{'nom' : playerName, 'jour' : jour, 'recette' : nomRecette}))
		
			if len(coutVente) != 0:
				prix=coutVente[0]["price"]
				if len(venteR) != 0 :
					qte=venteR[0]["quantite"]
					if qte is not None :
						profit = profit + prix * qte
		
			ingredientRecette = recettes
			for ing in range(0,len(ingredientRecette)):
				ingredientAF=(db.select("SELECT ing_alcool, ing_froid FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ing]["ing_nom"]}))
				if ingredientAF[0]['ing_alcool'] == True :
					alcool = True
				if ingredientAF[0]['ing_froid'] == False :
					froid = False
			drinkInfo = {}
			drinkInfo["name"] = nomRecette
			drinkInfo["price"] = prix
			drinkInfo["hasAlcohol"] = alcool
			drinkInfo["isCold"] = froid			
			drinksInfos.append(drinkInfo)
			
		drinksByPlayers[playerName] = drinksInfos
		
		print(drinksByPlayers[playerName])
		
		riche=[]
		ventes = []
		numero = db.select("SELECT * FROM joueur ORDER BY jou_budget DESC")
		for chaque in range(0,len(numero)):
			riche.append(numero[chaque]["jou_nom"])

		mapItem = []
		
		stand = db.select("SELECT jou_nom, jou_pos_x, jou_pos_y, jou_rayon FROM joueur WHERE jou_nom=@(nom)", {'nom':playerName})
		coordinatesS={}
		coordinatesS["latitude"] = stand[0]['jou_pos_x']
		coordinatesS["longitude"] = stand[0]['jou_pos_y']
		
		mapItems={}
		mapItems["kind"] = "stand"
		mapItems["owner"] = playerName
		mapItems["location"] = coordinatesS
		mapItems["influence"] = stand[0]['jou_rayon']
		mapItem.append(mapItems)
		
		ads = db.select("SELECT pub_pos_x, pub_pos_y, pub_rayon, jou_nom FROM pub WHERE pub_jour=@(jour) AND jou_nom=@(nom)", {'jour':jour, 'nom':playerName})
		for a in range(0,len(ads)):
			coordinatesA={}
			coordinatesA["latitude"] = ads[a]['pub_pos_x']
			coordinatesA["longitude"] = ads[a]['pub_pos_y']
			
			mapItems={}
			mapItems["kind"] = "ad"
			mapItems["owner"] = playerName
			mapItems["location"] = coordinatesA
			mapItems["influence"] = ads[a]['pub_rayon']
			mapItem.append(mapItems)
			
		itemsByPlayers[playerName] = mapItem
			
		playerInfo = {}
		playerInfo["cash"] = budget[0]["jou_budget"]
		playerInfo["sales"] = vente[0]["quantite"]
		playerInfo["profit"] = profit
		playerInfo["drinksOffered"] = drinksInfos

		playerInfos[playerName] = playerInfo
		
	db.close()
	
	coordinates = {}
	coordinates["lattitude"] = 50.0
	coordinates["longitude"] = 50.0
	
	coordinatesSpan = {}
	coordinatesSpan["lattitudeSpan"] = 50.0
	coordinatesSpan["longitudeSpan"] = 50.0
	
	region = {}
	region["center"] = coordinates
	region["span"] = coordinatesSpan
	
	map = {}
	map["region"] = region
	map["ranking"] = riche
	map["itemsByPlayer"] = itemsByPlayers
	map["playerInfo"] = playerInfos
	map["drinksByPlayer"] = drinksByPlayers

	reponse = {}
	reponse["map"] = map
	
	retour = make_response(json.dumps(reponse), 200, {'Content-Type': 'application/json'})
	return retour

#----------------------------------- LANCE L'APP -----------------------------------#
if __name__ == "__main__":
	app.run()
