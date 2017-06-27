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
# OBLIGATOIRE SINON ER REUR
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
			'WHERE com_nom=%(Username)s AND com_mot_de_passe=%(Password)s AND com_est_admin = True;', {
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
	return jsonResponse("ok")

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
	
	for element in range(0,len(data)):
		nom = data[element]["nom"];
		qteVendue = data[element]["qteVendue"];
		nomRecette = data[element]["nomRecette"]
		jour = data[element]["jour"];

		db.execute("INSERT INTO vendre VALUES ('%s', '%s', '%s', '%s')",(jour, qteVendue, nom, nomRecette))
	
	db.close()
	reponse = make_response('Vente crée avec succès', 200)
	return reponse

#---- Route pour la recupoeration des donnes de l'arduino
@app.route('/arduino', methods=['POST'])
def arduino():
	elements = request.get_json()
	return jsonResponse(elements)

#---- Route pour initialiser la base de donnees en admin
@app.route('/admin/resetbase', methods=['GET'])
@besoin_authentification_admin
def route_dbinit():
	db = Db()
	db.executeFile("base.sql")
	db.close()
	return "Done."
		
#---- Route pour s'authentifier en admin
@app.route('/admin', methods=['GET'])
@besoin_authentification_admin
def authentification_admin():
	return Response('Authentification OK', 200, {'WWW-Authenticate': 'Basic realm="Credentials valid"'})
	
#---- Route qui permet d'afficher tous les joueurs actifs en admin
@app.route('/admin/players', methods=['GET'])
@besoin_authentification_admin
def get_user():
	db=Db()
	
	resultat = db.select('SELECT * FROM Joueur WHERE jou_actif=TRUE')
	
	db.close()
	reponse = make_response(json.dumps(resultat),200)
	return reponse

#---- Route qui retourne toute la liste d'ingredient present dans la base
@app.route('/ingredients', methods=['GET'])
def get_ingredient():
	db=Db()
	
	resultat = db.select('SELECT ing_nom AS name, ing_prix_unitaire AS cost, ing_alcool AS hasAlcohol, ing_froid AS isCold FROM Ingredient')
	
	db.close()
	reponse = make_response(json.dumps(resultat),200)
	return reponse

#---- Route qui gere les actions joueur
@app.route('/action/<player>', methods=['POST'])
def action_player(player):
	data = request.get_json()
	kind = data["kind"]
	db=Db()
	coutLimonade = 0.5 #recupéré par la bd
	if kind == "drinks":
		recetteJoueur = db.select("SELECT * FROM Recette")
		recettes={}
		i = 0
		for recette in range(0,len(recetteJoueur)):
			prepare = data["prepare"][i++]
			nb = int(prepare["quantite"])
			ingredient = {}
			cout=[]
			coutProd = 0.0
			print(player)
			recettes[recette]=(db.select("SELECT * FROM composer WHERE rec_nom=@(recette) AND jou_nom=@(nom)", 
				{'recette' : recetteJoueur[recette]["rec_nom"], 'nom' : player}))
			ingredientRecette = recettes[recette]
			for ingredient in range(0,len(ingredientRecette)):
				cout += (db.select("SELECT ing_prix_unitaire FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				coutProd = coutProd + (cout[ingredient]['ing_prix_unitaire'] * nb)
				print(cout[ingredient]['ing_prix_unitaire'])		
				
		#à insérer dans la bd avec le pseudo
		reponse = {
			"sufficientFunds" : True,
			"totalCost" : coutProd
		}

		db.close()
		return jsonResponse(reponse)
	return jsonResponse("ok")
	#else if(data["kind"]=="ad")
	#else if(data["kind"]=="price")

	
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
			0: "playerName1",
			1: "playerName2"
		},
		"itemsByPlayer": 
		{
			"playerName1":
			[
				{
				 	"kind" : "stand",
				 	"owner" : "playerName1",
				 	"location" : {
						"latitude": 100.5,
						"longitude": 150.2
					},
				 	"influence" : 1.0
				},
				{
				 	"kind" : "ad",
				 	"owner" : "playerName1",
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
				"pseudo": "playerName1",
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
			"playerName1": 
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
	return json.dumps(Map)


#---- Route qui permet de rejoindre une partie
# Route à tester
@app.route('/players',methods=['POST'])
def post_players():
	db = Db()
	data = request.get_json()
	verif = db.select("SELECT * FROM Joueur where jou_nom = @(nom)", {'nom' : data['user']});
	if(len(verif) != 0) :
		return jsonResponse("NOK")
	
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
			{'nom' : data['user'],
			'budget' : budget,
			'posX' : posX,
			'posY' : posY,
			'rayon' : rayon,
			'actif' : actif
		})
	
		db.execute("INSERT INTO Compte VALUES (@(nom),@(mdp), false)",{'nom' : data['user'],'mdp' : md5.new(data['password'].encode('utf-8')).hexdigest()})
		
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'citron')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'eau gazeuse')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Limonade', @(nom), 'sucre')",{'nom' : data['user']})
		
		db.execute("INSERT INTO composer VALUES ('Chocolat chaud', @(nom), 'chocolat')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Chocolat chaud', @(nom), 'lait')",{'nom' : data['user']})
		
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'rhum')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'eau gazeuse')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'sucre')",{'nom' : data['user']})
		db.execute("INSERT INTO composer VALUES ('Mojito', @(nom), 'menthe')",{'nom' : data['user']})
		
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
				{'recette' : recetteJoueur[recette]["rec_nom"], 'nom' : data['user']}))
			ingredientRecette = recettes[recette]
			for ingredient in range(0,len(ingredientRecette)):
				cout += (db.select("SELECT ing_prix_unitaire FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				coutProd = coutProd + cout[ingredient]['ing_prix_unitaire']

				ingredientAlcool+=(db.select("SELECT ing_alcool FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				if ingredientAlcool[ingredient]['ing_alcool'] == True & alcool == False :
					alcool = True

				ingredientFroid+=(db.select("SELECT ing_froid FROM Ingredient WHERE ing_nom=@(ing)", {'ing' : ingredientRecette[ingredient]["ing_nom"]}))
				if ingredientFroid[ingredient]['ing_froid'] == False & froid == True :
					froid = False
			
			drinkInfo = {}
			drinkInfo["name"] = recetteJoueur[recette]["rec_nom"]
			drinkInfo["price"] = coutProd
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
		reponse["name"] = data['user']
		reponse["location"] = coordinates
		reponse["info"] = playerInfo

		retour = make_response(json.dumps(reponse),200)
		return retour
	
#---- Route metrology, enregistrment de la meteo dans la BDD
@app.route('/metrology',methods=['POST'])
def post_metrology():
	db = Db()
	arduino = request.get_json()
	
	#--- EXEMPLE :  arduino = {"timestamp" : 1,"weather":[{"dfn" : 0,"weather" : "cloudy"},{"dfn" : 1,"weather" : "sunny"}]}
	
	weather = arduino['weather']
	timestamp = arduino ['timestamp']
	
	if timestamp == 1:
		matin = weather[0]['weather']
		aprem = weather[1]['weather']
			
		db.execute("INSERT INTO Meteo VALUES (@(jour), @(matin), @(aprem))",
			{'jour' : 1,
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
			
			db.execute("UPDATE Meteo SET met_matin=@(matin), met_apres_midi=@(aprem) WHERE met_jour=@(jour)",
				{'matin' : matin,
				'aprem' : aprem,
				'jour' : jour
			})

		else:
			aprem = weather[0]['weather']
			matin = weather[1]['weather']

			db.execute("UPDATE Meteo SET met_apres_midi=@(aprem) WHERE met_jour=@(jour)",
				{'aprem' : aprem,
				'jour' : jour
			})
			
			db.execute("INSERT INTO Meteo (met_jour, met_matin) VALUES (@(jour), @(matin))",
				{'jour' : jour + 1,
				'matin' : matin
			})
			
			
	retour = make_response(json.dumps(weather[0]['weather']),200)
	return retour


#----------------------------------- LANCE L'APP -----------------------------------#
if __name__ == "__main__":
	app.run()
