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

db.execute('INSERT INTO vendre VALUES (%(jour)s , %(qteVendue)s),%(nom)s),%(nomRecette)s)',{
	'jour' : jour,
	'qteVendue' : qteVendue,
	'nom' : nom,
	'nomRecette' : nomRecette

})
db.close()
repons = make_response('Vente crée avec succès', 200)
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


if __name__ == "__main__":
    app.run()
