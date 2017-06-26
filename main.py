# -*- coding: utf-8 -*-
import md5
from flask import Flask, request, make_response, Response
import json, os, psycopg2, urlparse
from db import Db
from functools import wraps 
from datetime import datetime
from math import sqrt
import random
######################################
# OBLIGATOIRE SINON ERREUR
import sys
reload(sys)
sys.setdefaultencoding("latin-1")
######################################

app = Flask(__name__)
app.debug = True
from flask_cors import CORS
app = Flask(__name__)
CORS(app)

Names = ["puppy", "car", "rabbit", "girl", "monkey"]
Verbs = ["runs", "hits", "jumps", "drives", "barfs"]
Compls = ["crazily.", "dutifully.", "foolishly.", "merrily.", "occasionally."]
randPhrase=[Names,Verbs,Compls]



def jsonResponse(data, status=200):
  return json.dumps(data), status, {'Content-Type': 'application/json'}

def verif_authentification_admin(nom, mot_de_passe):
    db = Db()
    result = db.select('SELECT COUNT(com_nom) AS nb FROM Compte '
                       'WHERE com_nom=%(Username)s AND com_mot_de_passe=%(Password)s AND com_est_admin = True;', {
                           'Username': nom,
                           'Password': md5.new(mot_de_passe.encode('utf-8')).hexdigest()
                       })
    db.close()
    return result[0]['nb'] >= 1
	
def besoin_authentification_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or not verif_authentification_admin(auth.username, auth.password):
            return Response('Authentification FAIL', 401, {'WWW-Authenticate': 'Basic realm="Login Required"'})
        return f(*args, **kwargs)

    return decorated


@app.route("/")
def formulaire():
	return "TOTO"

@app.route("/html")
def formulaire2():
	f = open('./static/form.html', 'r')
	html = f.read()
	return html

@app.route("/testhtml")
def product_rand():
	phrase = 'toto'
	return jsonResponse(phrase)

@app.route("/phrases/elements/<name>/<verb>/<compl>")
def product_elem(name,verb,compl):
	global Names, Verbs, Compls
	Names.append(name)
	Verbs.append(verb)
	Compls.append(compl)
	phrase = Names[-1]+' '+Verbs[-1]+' '+Compls[-1]
	return jsonResponse(phrase)

@app.route('/sales', methods=['POST']) 
def sales():
	db=Db()
	elements = request.get_json()
	nom=elements["nom"];
	qteVendue=elements["qteVendue"];
	nomRecette=elements["nomRecette"]
	jour=elements["jour"];

	db.execute('INSERT INTO vendre VALUES (%(jour)s , %(qteVendue)s,%(nom)s,%(nomRecette)s)',{
		'jour' : jour,
		'qteVendue' : qteVendue,
		'nom' : nom,
		'nomRecette' : nomRecette

	})
	db.close()
	reponse = make_response('Vente crée avec succès', 200)
	return reponse

@app.route('/arduino', methods=['POST'])
def arduino():
  elements = request.get_json()
  return jsonResponse(elements)

# Route OK
@app.route('/admin/resetbase', methods=['GET'])
@besoin_authentification_admin
def route_dbinit():
  	"""Cette route sert a initialiser (ou nettoyer) la base de donnees."""
  	db = Db()
  	db.executeFile("base.sql")
  	db.close()
  	return "Done."
		
#-----------------------------------------------------------------

# Route OK
@app.route('/admin', methods=['GET'])
@besoin_authentification_admin
def authentification_admin():
	return Response('Authentification OK', 200, {'WWW-Authenticate': 'Basic realm="Credentials valid"'})
	
#-----------------------------------------------------------------

# Route OK
@app.route('/admin/players', methods=['GET'])
@besoin_authentification_admin
def get_user():
	db=Db()
	
	resultat = db.select('SELECT * FROM Joueur WHERE jou_actif=TRUE')
	
	db.close()
	reponse = make_response(json.dumps(resultat),200)
	return reponse

#-----------------------------------------------------------------

# Route OK
@app.route('/ingredients', methods=['GET'])
def get_ingredient():
	db=Db()
	
	resultat = db.select('SELECT ing_nom AS name, ing_prix_unitaire AS cost, ing_alcool AS hasAlcohol, ing_froid AS isCold FROM Ingredient')
	
	db.close()
	reponse = make_response(json.dumps(resultat),200)
	return reponse

#-----------------------------------------------------------------

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
						"latitude": 10.5,
						"longitude": 15.2
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
						"latitude": 10.5,
						"longitude": 15.2
					},
				 	"influence" : 1.0
				},
				{
				 	"kind" : "ad",
				 	"owner" : "Toto",
				 	"location" : {
						"latitude": 20.5,
						"longitude": 25.2
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


#---------------------------------- R4 - REJOINDRE/QUITTER UNE PARTIE -----------------------------------#
# Route à tester
@app.route('/players',methods=['POST'])
def post_players():
    db = Db()
    data = request.get_json()
    verif = db.select("SELECT * FROM Joueur where name = '%s'"%(data['nom']));
    if(len(verif) != 0) :
        return json.dumps("Le pseudo choisi est déjà utilisé"), 400, {'Content-Type': 'application/json'}
    else :

          #---------------- VARIABLES POUR GENERER UN JOUEUR ------------------------#
          bugdet = 1000.0
          posX =randint(0,100)*1.0
          posY=randint(0,100)*1.0
          rayon = 15
          actif = true
	  #-------------------------------------------------------------------------#

          recette = {}
          db.execute("INSERT INTO Joueur(jou_nom,jou_budget,jou_pos_x, jou_pos_y, jou_rayon, jou_actif) VALUES (%s,%s,%s,%s,%s,%s);", (data['nom'],budget,posX,posY,rayon,actif))
          drinkInfo = db.select("SELECT * FROM Recette WHERE jou_nom = '%s'", (data['nom']))
          for recette in range(0,len(drinkInfo)):
            recette.apprend(db.select("SELECT * FROM Recette WHERE rec_nom='%s' AND jou_nom='%s'", (drinkInfo[recette]["rec_nom"], drinkInfo[recette]["jou_nom"])))
          db.close()

          playerInfo = {}
          playerInfo["cash"] = bugdet
          playerInfo["sales"] = "0"
          playerInfo["profit"] = "0"
          playerInfo["drinksOffered"] = drinkInfo

          coordinates = {}
          coordinates["lattitude"] = posX
          coordinates["longitude"] = posY

          reponse = {}
          reponse["name"] = data['name']
          reponse["location"] = coordinates
          reponse["info"] = playerInfo

          retour = make_response(json.dumps(reponse),200)
	  return retour

if __name__ == "__main__":
    app.run()
