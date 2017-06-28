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
	jour = meteo[0]['met_jour']
	
	for element in range(0,len(data)):
		nom = data["sales"][element]["player"]
		qteVendue = data["sales"][element]["quantity"]
		recette = data["sales"][element]["item"]

		db.execute("INSERT INTO vendre VALUES (@(jour), @(quantite), @(nom), @(recette))",
			   { 'jour' : jour,
			    'quantite' : qteVendue,
			    'nom' : nom,
			    'recette' : recette
			   })
	
	db.close()
	
	reponse = make_response(json.dumps("Vente enregistre"), 200, {'Content-Type': 'application/json'})
	return reponse

#---- Route pour initialiser la base de donnees en admin
@app.route('/admin/resetbase', methods=['GET'])
@besoin_authentification_admin
def route_dbinit():
	db = Db()
	db.executeFile("base.sql")
	db.close()
	return json.dumps("Done."), 200, {'Content-Type': 'application/json'}


#---- Route qui gere les actions joueur
@app.route('/actions/<playerName>', methods=['POST'])
def action_player(playerName):
	data = request.get_json()
	action = data["actions"][0]
	kind = action["kind"]
	simulation=data["simulated"]
	reponse={}
	db=Db()

	if kind == "drinks":
		recetteJoueur = db.select("SELECT * FROM Recette")
		recettes={}
		coutTotal=0
		for recette in range(0,len(recetteJoueur)):
			prepare = action["prepare"][recette]
			print(prepare["quantite"])
			nb = int(prepare["quantite"])
			coutProd = 0.0
			ingredient = {}
			cout=[]
			recettes[recette]=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)", 
				{
					'recette' : recetteJoueur[recette]["rec_nom"],
					 'nom' : playerName
				}))

			ingredientRecette = recettes[recette]
			for ingredient in range(0,len(ingredientRecette)):
				cout += (db.select("SELECT ing_prix_unitaire FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				coutProd = coutProd + (cout[ingredient]['ing_prix_unitaire'] * nb)	
			coutTotal=coutTotal+coutProd
			
			if simulation == False :
				print("Insertion en base")
				db.execute("INSERT INTO produire (jou_nom,pro_jour,pro_prix_vente, pro_quantite, rec_nom) VALUES (@(nom),@(jour),@(prix),@(quantite),@(recette))", 
				{'nom' : playerName,
				'jour' : 1,
				'prix' : action["price"][recette]["prix"],
				'quantite' : action["prepare"][recette]["quantite"],
				'recette' : action["prepare"][recette]["boisson"]
				})

		budget = db.select("SELECT jou_budget FROM Joueur WHERE jou_nom=@(nom)",{
				'nom' : playerName
				})	
			
		if int(budget[0]['jou_budget'])>int(coutTotal):
			if simulation == False :
				db.execute("UPDATE Joueur SET jou_budget = @(newBudget) WHERE jou_nom=@(nom)",{
				'newBudget': budget[0]['jou_budget']-int(coutTotal) ,
				'nom' : playerName
				})	
			reponse = {
				"sufficientFunds" : True,
				"totalCost" : coutTotal
			}
		else:
			reponse = {
				"sufficientFunds" : False,
				"totalCost" : coutTotal
			}
			
		db.close()
		return jsonResponse(reponse)
		#else if(data["kind"]=="ad")
		#else if(data["kind"]=="price")
	return json.dumps("NOK"), 403, {'Content-Type': 'application/json'}
	
	
#---- Route qui permet d'afficher la map de tout les joueurs
# Route en maintenance reste la partie db à faire
@app.route('/map', methods=['GET'])
def get_map():
	# données à générer par la BD pour chaque joueur (pour l'instant généré à la main):
	Map = {
		"region": {
			"center": {
				"latitude": 10.5,
				"longitude": 15.2
			},
			"span": {
				"latitudeSpan": 10.2,
				"longitudeSpan": 15.5
			}
		},
		"ranking":{
			0: "pierre",
			1: "Toto"
		},
		"itemsByPlayer": 
		{
			"pierre":
			[
				{
				 	"kind" : "stand",
				 	"owner" : "pierre",
				 	"location" : {
						"latitude": 100.5,
						"longitude": 150.2
					},
				 	"influence" : 1.0
				},
				{
				 	"kind" : "ad",
				 	"owner" : "pierre",
				 	"location" : {
						"latitude": 20.5,
						"longitude": 25.2
					},
					"influence" : 2.0
				}
			],
			"Toto":
			[
				{
				 	"kind" : "stand",
				 	"owner" : "Toto",
				 	"location" : {
						"latitude": 150.5,
						"longitude": 100.2
					},
				 	"influence" : 1.0
				},
				{
				 	"kind" : "ad",
				 	"owner" : "Toto",
				 	"location" : {
						"latitude": 90.7,
						"longitude": 100.2
					},
					"influence" : 2.0
				}
			]
		},
		"playerInfo": [
			{
				"cash": 3000,
				"sales": 30,
				"profit": 2000.0,
				"pseudo": "pierre",
				"drinkOffered": 
				[
					{
						"name":"limonade",
						"price": 1.8,
						"hasAlcohol" : "false",
						"isCold" : "true"
					}
				]
			},
			{
				"cash": 4000,
				"sales": 40,
				"profit": 3000.0,
				"pseudo": "Toto",
				"drinkOffered": 
				[
					{
						"name":"limonade",
						"price": 2.2,
						"hasAlcohol" : "false",
						"isCold" : "true"
					},
					{
						"name":"coca",
						"price": 2.5,
						"hasAlcohol" : "false",
						"isCold" : "true"
					}
				]
			}
		],
		"drinksByPlayer": {
			"pierre": 
			[
				{
					"name":"limonade",
					"price": 1.8,
					"hasAlcohol" : "false",
					"isCold" : "true"
				}
			],
			"Toto": 
			[
				{
					"name":"limonade",
					"price": 2.2,
					"hasAlcohol" : "false",
					"isCold" : "true"
				},
				{
					"name":"coca",
					"price": 2.5,
					"hasAlcohol" : "false",
					"isCold" : "true"
				}	
			]
		}
	}
	return json.dumps("Suppression joueur OK"), 200, {'Content-Type': 'application/json'}

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
			
			coutProd = (db.select('SELECT count(ing_prix_unitaire) AS price FROM Ingredient, Recette, Composer WHERE ingredient.ing_nom=composer.ing_nom AND '
						'recette.rec_nom=composer.rec_nom AND recette.rec_nom=@(recette)',
						{'recette' : recetteJoueur[recette]["rec_nom"]}))
	
			ingredientRecette = recettes[recette]
			for ingredient in range(0,len(ingredientRecette)):

				ingredientAlcool+=(db.select("SELECT ing_alcool FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				if ingredientAlcool[ingredient]['ing_alcool'] == True & alcool == False :
					alcool = True

				ingredientFroid+=(db.select("SELECT ing_froid FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				if ingredientFroid[ingredient]['ing_froid'] == False & froid == True :
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
	reste = int(meteo[0]['met_heure_ecoule'] / 24.0)%1
		
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

#----------------------------------- LANCE L'APP -----------------------------------#
if __name__ == "__main__":
	app.run()
