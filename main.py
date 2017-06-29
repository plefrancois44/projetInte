<!DOCTYPE html>
    <html >
        <head>
            <script src="/static/jquery-3.2.1.js"></script>
            <link rel="stylesheet" href="static/form.css" />
            <meta charset="utf-8">
            <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
            <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<?------------------------------------------------- GET--------------------------------------------------------------------------------------------->
            <script>   
                  function metrology(){
                    $.ajax("/metrology")
                        .done(function(data){
                            timestamp=data["timestamp"];
                            $("#metrology").text(calcul(timestamp));
                            if(data["weather"][0]["dfn"]==0){
                                $("#meteo").text(data["weather"][1]["weather"]);
                                $("#prevision").text(data["weather"][0]["weather"]);
                            }
                            else{
                                $("#meteo").text(data["weather"][0]["weather"]);
                                $("#prevision").text(data["weather"][1]["weather"]);
                            }
                        })
                        getMapPlayer();
                }

                function calcul(timestamp){
                    return Math.floor(timestamp/24+1);
                }
              

                function getMapPlayer(){
                    $.ajax("/map/"+$_GET('pseudo'))
                        .done(function(data){ 
                            var pseudo = $_GET('pseudo');
                            playerInfo = data["playerInfo"]
                            budget = playerInfo["cash"]
                            $("#budget").text(budget)
                            map = data["map"]
                            itemsByPlayer = map["itemsByPlayer"]
                            influence = itemsByPlayer[pseudo][0]["influence"]
                            $("#influence").text(influence)
                        })
                }
              
                function etendre(){
                    var pseudo = $_GET('pseudo');
                    var location = {}
                    var actions = [];
                    var action = {};

                    $.ajax("/map/"+$_GET('pseudo'))
                        .done(function(data){ 
                            map = data["map"]
                            itemsByPlayer = map["itemsByPlayer"]
                            influence = itemsByPlayer[pseudo][0]["influence"]
                        })

                        if(confirm("Voulez vous etendre votre influence pour "+influence*50+" euros ?"))
                        {
                            action.radius = influence+1
                            action.location = 0
                            action.kind="ad"

                            JSON.stringify(action);
                            actions.push(action)

                            let data = 
                            {
                                "simulated" : false,
                                "actions" : actions
                            };

                            $.ajax("/actions/"+pseudo, {
                              type: "POST",
                              data: JSON.stringify(data),
                              contentType: "application/json",
                            }).done(function(reponse) {
                                    var bool = new Boolean(reponse["sufficentFunds"])
                                    if(bool){
                                        getMapPlayer();
                                    }
                                    else window.alert("fond insuffisant");
                                    
                                })
                        }   
                    }

                function $_GET(param) {
                    var vars = {};
                    window.location.href.replace( location.hash, '' ).replace( 
                        /[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
                        function( m, key, value ) { // callback
                            vars[key] = value !== undefined ? value : '';
                        }
                    );
                    if ( param ) {
                        return vars[param] ? vars[param] : null;    
                    }
                    return vars;
                }

      function envoyerDecision(simulated){
        var pseudo = $_GET('pseudo');
        var toPrepare = [];
        var prices = [];
        var actions = [];
        var action = {};
        toPrepare = [];
        var tmp = {};
        if($("#nbLimonade").val()>0){
            tmp = {}
            tmp.boisson = "Limonade";
            tmp.quantite = $("#nbLimonade").val();
            JSON.stringify(tmp);
            toPrepare.push(tmp);
            tmp = {};
            tmp.boisson = "Limonade";
            tmp.price = $("#prixLimonade").val();
            JSON.stringify(tmp);
            prices.push(tmp);
        }
        if($("#nbChocolat_chaud").val()>0){
            tmp = {}
            tmp.boisson = "Chocolat_chaud";
            tmp.quantite = $("#nbChocolat_chaud").val();
            JSON.stringify(tmp);
            toPrepare.push(tmp);
            tmp = {};
            tmp.boisson = "Chocolat_chaud";
            tmp.price = $("#prixChocolat_chaud").val();
            JSON.stringify(tmp);
            prices.push(tmp);
        }
        if($("#nbMojito").val()>0){
            tmp = {};
            tmp.boisson = "Mojito";
            tmp.quantite = $("#nbMojito").val();
            toPrepare.push(tmp);
            JSON.stringify(tmp);
            tmp = {};
            tmp.boisson = "Mojito";
            tmp.price = $("#prixMojito").val();
            JSON.stringify(tmp);
            prices.push(tmp);
        }
        if($("#nbVin_chaud").val()>0){
            tmp = {};
            tmp.boisson = "Vin_chaud";
            tmp.quantite = $("#nbVin_chaud").val();
            toPrepare.push(tmp);
            JSON.stringify(tmp);
            tmp = {};
            tmp.boisson = "Vin_chaud";
            tmp.price = $("#prixVin_chaud").val();
            JSON.stringify(tmp);
            prices.push(tmp);
        }
        JSON.stringify(toPrepare);
        JSON.stringify(prices);
        action.prepare = toPrepare
        action.price = prices
        action.kind = "drinks"
        JSON.stringify(action);
        actions.push(action)
        let data = 
        {
            "simulated" : simulated,
            "actions" : actions
        };
        $.ajax("/actions/"+pseudo, {
          type: "POST",
          data: JSON.stringify(data),
          contentType: "application/json",
            }).done(function(data) {
                if(data["sufficentFunds"]!=false){
                    $("#valideBoisson").text(data["totalCost"]);
                }
                else $("#valideBoisson").text("fond insuffisant");
                if(simulated!=true){
                    window.alert('Décisions prises et bien envoyés au serveur.')
                } 
                else
                    window.alert('Visualitation seulement')
            })       
        } 

            </script>
        </head>
<?-------------------------------------------------------------------------------------------------------------------------------------------------------->
        
        <body>
            <?-- Titre de la page --------------------------------------------------------------------------------------------------------------------->
            <div id="main_title" class="jumbotron">
                <h1>Lemonade Wars</h1>
                <p>If you're not with me, you're my enemy...</p>                
            </div>
            <?------------------------------------------------------------------------------------------------------------------------------------------->

            <div class="container">
                <div class="row">
                    <div class="col-sm-4">
                        <h2>Situation actuelle:</h2>
                            <label><input type="button" onclick="metrology()" name="metrology" value="Actualiser les informations"></input></label><br>
                            <label>Jour: </label>
                            <label id="metrology"> </label><br>
                            <label>Météo du jour: </label>           
                            <label id="prevision"></label><br>
                            <label>Météo prévisionnelle: </label>                                 
                            <label id="meteo"></label><br>                   
                            <label>Budget disponible: </label>
                            <label id="budget"> </label><br>
                            <label>Influence: </label><br>
                            <label id="influence"> </label><br>
                    </div>        
            <?------------------colum1-------------------------------------------------------------------------------------------------------------------->
                    <div class="col-sm-5">
                        <h2>Décisions:</h2>
                        <form id="boisson">
                            <label id="boisson">Acheter boisson:</label><br>
                            <label value="Limonade">Limonade</label><br>
                            <label>Quantité : <input type='number' id="nbLimonade" placeholder="0"></label>
                            <label>Prix : <input type='number' id="prixLimonade" placeholder="0"></label><br>
                            <label value="Chocolat_chaud">Chocolat Chaud</label><br>
                            <label>Quantité : <input type='number' id="nbChocolat_chaud" placeholder="0"></label>
                            <label>Prix : <input type='number' id="prixChocolat_chaud" placeholder="0"></label><br>
                            <label value="Mojito">Mojito</label><br>
                            <label>Quantité : <input type='number' id="nbMojito" placeholder="0"></label>
                            <label>Prix : <input type='number' id="prixMojito" placeholder="0"></label><br>
                            <label value="Vin_chaud">Vin Chaud</label><br>
                            <label>Quantité : <input type='number' id="nbVin_chaud" placeholder="0"></label>
                            <label>Prix : <input type='number' id="prixVin_chaud" placeholder="0"></label><br>
                            <label><input type="button" onclick="envoyerDecision(true)" name="verifier" value="Visualiser ses choix"></input></label>
                        </form>
                        
                        <form id="position">
                            <label>Acheter un panneau publicitaire:</label><br>
                            <canvas id="myMap" width="516" height="516"></canvas>
                              <script>
                              var canvas = document.getElementById('myMap');
                              var context = canvas.getContext('2d');
                              var imageObj = new Image();
                              imageObj.onload = function() {
                                context.drawImage(imageObj, 69, 50);
                              };
                              imageObj.src = 'static/map.jpg';
                            </script>
                            
                            <label><input type="button" onclick="etendre()" name="add_panneau" value="Etendre la zone d'influence"></input></label><br>
                            <label><input type="button" onclick="level_up()" name="level_up" value="Améliorer le panneau"></input></label>
                        </form>                
                    </div>
                    <div class="col-sm-3">
                        <h2>Facture:</h2>
                            <label>Total à payer: <label id="valideBoisson"></label></label><br>
                            <label id="validePosition"></label><br>
                            <label><input type="button" onclick="envoyerDecision(false)" name="envoyerDecision" value="Valider mes choix"></input></label>
                    </div>
                </div>
            </div>
        </body>
    </html>

