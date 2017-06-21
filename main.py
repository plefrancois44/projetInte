import json
import random

Names = ["puppy", "car", "rabbit", "girl", "monkey"]
Verbs = ["runs", "hits", "jumps", "drives", "barfs"]
Compls = ["crazily.", "dutifully.", "foolishly.", "merrily.", "occasionally."]
randPhrase=[Names,Verbs,Compls]

from flask import Flask, make_response, request
app = Flask(__name__)


def jsonResponse(data, status=200):
  return json.dumps(data), status, {'Content-Type': 'application/json'}

@app.route("/")
def formulaire():
	return "hello world"

@app.route("/phrases")
def formulaire():
	f = open('./form.html', 'r')
	html = f.read()
	return html

@app.route("/phrases/random")
def product_rand():
	phrase = ' '.join([random.choice(i) for i in randPhrase]) 
	return jsonResponse(phrase)

@app.route("/phrases/elements/<name>/<verb>/<compl>")
def product_elem(name,verb,compl):
	global Names, Verbs, Compls
	Names.append(name)
	Verbs.append(verb)
	Compls.append(compl)
	phrase = Names[-1]+' '+Verbs[-1]+' '+Compls[-1]
	return jsonResponse(phrase)

if __name__ == "__main__":
    app.run()
