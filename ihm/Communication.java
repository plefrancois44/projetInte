/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ihm;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//import model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ihm.model.*;
import java.awt.Color;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/**
 *
 * @author Nicolas, Pierre
 */
public class Communication {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    //!!!!!!!!!!!!
    //Test test3 = new Test();
    
    MapGame map2 = getMap();
    
    
    //MapGame map2 = new MapGame();
    
    
    Player[] joueur_d = map2.getPlayerInfo();
    //!!!!!!!!!!!!!!!
    
    //Map<String, MapGame[]]> list_drinkbyplayer = test3.comm.getMap().getDrinkByPlayer();
    Map<String, Drink[]> list_drink = map2.getDrinkByPlayer();
    
//public static void main (String[] args) throws Exception{
//
//	Communication com = new Communication();
//
//	System.out.println("Send Http GET request to /map");
//	MapGame mapGame = com.getMap();
//	System.out.println("Send Http POST request to /sales");
//	com.sendSales(mapGame);
//
    //  }
    
    public MapGame getMap() {
        try {
            final Gson gson = new GsonBuilder().create();
    
    String jsonMap = this.sendGet("map");
    JSONObject json = new JSONObject(jsonMap);
    
    //---------------récupération de region-----------------------------------
    JSONObject regionJson =  (JSONObject) json.get("region");
    Region region = gson.fromJson(regionJson.toString(), Region.class);
    //------------------------------------------------------------------------
    
    //---------------récupération du rang-------------------------------------
    JSONObject rankingJson =  (JSONObject) json.get("ranking");
    int n = rankingJson.length();
    String[] ranking = new String[n];
    for(int i = 0; i<n; i++)
   	 ranking[i] = (String) rankingJson.get(Integer.toString(i));
    //------------------------------------------------------------------------
    
    //---------------récupération des joueurs---------------------------------
    JSONObject playerInfoJson = new JSONObject(json.get("playerInfo").toString());
    n = playerInfoJson.length();
    Player[] playerTab = new Player[n];
    
    for(int i = 0; i<playerInfoJson.names().length(); i++){
     	JSONObject player = new JSONObject(playerInfoJson.get(playerInfoJson.names().getString(i)).toString());
     	float cash = Float.parseFloat(player.get("cash").toString());
     	float profit = Float.parseFloat(player.get("profit").toString());
     	int sales = Integer.parseInt(player.get("sales").toString());
     	String pseudo = playerInfoJson.names().getString(i);
    	 
     	JSONArray drinks = new JSONArray(player.get("drinkOffered").toString());
     	int nbDrinks = drinks.length();
   	  Drink[] drinkOffered = new Drink[nbDrinks];
   	  for(int j=0;j<nbDrinks;j++) // pour chaque boisson de la JsonArray, on stock dans un tableau de Drink
   		  drinkOffered[j]=gson.fromJson(drinks.get(j).toString(),Drink.class);
   	 
     	Player p = new Player(cash,sales,profit,drinkOffered,pseudo);
     	playerTab[i]=p;
     	System.out.println(p.toString());
    }
    //------------------------------------------------------------------------
    
    //---------------récupération des boissons---------------------------------
   	 JSONObject drinksByPlayerJson =  (JSONObject) json.get("drinksByPlayer");
   	 Map<String, Drink[]> drinkByPlayer = new HashMap<String,Drink[]>() ;
   	 n = drinksByPlayerJson.length();
   	 for(int i=0; i<n; i++){
   		 JSONArray drinkArray = drinksByPlayerJson.getJSONArray(playerTab[i].getPseudo()); //on commence par récupérer la JsonArray à partir du pseudo
   		 int nbDrinks = drinkArray.length();
   		 Drink[] drinks = new Drink[nbDrinks];
   		 for(int j=0;j<nbDrinks;j++) // pour chaque boisson de la JsonArray, on stock dans un tableau de Drink
   			 drinks[j]=gson.fromJson(drinkArray.get(j).toString(),Drink.class);
   		 drinkByPlayer.put(playerTab[i].getPseudo(), drinks); // enfin, on associe la pseudo du joueur au tableau de boissons
   	 }    
    //------------------------------------------------------------------------
    
    //---------------récupération des items---------------------------------
   	 JSONObject itemsByPlayerJson =  (JSONObject) json.get("itemsByPlayer");
   	 Map<String, MapItem[]> itemsByPlayer = new HashMap<String, MapItem[]>() ;
   		 n = itemsByPlayerJson.length();
   		 for(int i=0; i<n; i++){
   			 JSONArray itemArray = itemsByPlayerJson.getJSONArray(playerTab[i].getPseudo()); //on commence par récupérer la JsonArray à partir du pseudo
   			 int nbItems = itemArray.length();
   			 MapItem[] mapItems = new MapItem[nbItems];
   			 for(int j=0;j<nbItems;j++) // pour chaque items de la JsonArray, on stock dans un tableau de MapItem
   				 mapItems[j]=gson.fromJson(itemArray.get(j).toString(),MapItem.class);
   			 itemsByPlayer.put(playerTab[i].getPseudo(), mapItems); // enfin, on associe la pseudo du joueur au tableau d'Item
   		 }    
    //------------------------------------------------------------------------
    
    MapGame map = new MapGame(ranking,region,playerTab,drinkByPlayer,itemsByPlayer);
    System.out.println(map.toString());
    return map;

        } catch (Exception ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    public Temps getTemps() {
        
        try{
            
           // final Gson gson = new GsonBuilder().create();
            
            //String jsonMap = this.sendGet("map");
            //JSONObject json = new JSONObject(jsonMap);
            
            //---------------récupération de region-----------------------------------
            //JSONObject regionJson =  (JSONObject) json.get("region");
            //Region region = gson.fromJson(regionJson.toString(), Region.class);
            //------------------------------------------------------------------------
        
            final Gson gson = new GsonBuilder().create();
            
            String jsonTemps = this.sendGet("metrology");
            JSONObject json = new JSONObject(jsonTemps);
            
            //-------------------Récuperation de Weather------------------------
            JSONArray weatherJson = new JSONArray(json.get("weather").toString());
            int nb = weatherJson.length();
            Forecast[] forecast = new Forecast[nb];
            
            for(int j=0;j<nb;j++){ // pour chaque boisson de la JsonArray, on stock dans un tableau de Drink
                JSONObject js = new JSONObject(weatherJson.get(j).toString());
                forecast[j]=new Forecast(Integer.parseInt(js.get("dfn").toString()),js.get("weather").toString());
            }
            int timestamp = Integer.parseInt(json.get("timestamp").toString());
            
            Temps temps = new Temps(timestamp,forecast);
            
            return temps;
        }
        
        catch(Exception e){
        
        }
        
        return null;

    }
    
    
    
    private String sendGet(String route) throws Exception {
        
        URL heroku_URL = null; //URL du server
        int reponseServeur= 0; // donnée à récupérer
        String response = "";
        
        try {
            heroku_URL = new URL("http://bushukan-imerir.herokuapp.com/"+route);
            //heroku_URL = new URL("http://127.0.0.1:5000/" + route);
            //Ouvrir connexion entre JAVA et le server
            HttpURLConnection connexion = (HttpURLConnection)heroku_URL.openConnection();
            InputStream flux = connexion.getInputStream();
            
            //Vérifier que la connexion est OK
            System.out.println("Status de la connexion : " + connexion.getResponseMessage());
            
            if (connexion.getResponseCode() == HttpURLConnection.HTTP_OK){
                System.out.println("données retournées par le serveur : ");
                //Tant que le flux n'est pas terminer, faire un print
                while ((reponseServeur=flux.read())!= -1){
                    System.out.print((char) reponseServeur);
                    response += (char)reponseServeur;
                }
                System.out.print("\n\n");
            }
            //fermer le flux et deconnecter
            flux.close();
            connexion.disconnect();
        }
        catch(Exception e) {
            System.out.println(e);
        }
        
        return response.toString();
    }
    
// HTTP POST request
    
    //
    
    //rejouter private
     void sendSales(MapGame mapGame) throws Exception {
        
        //String url = "http://bushukan-imerir.herokuapp.com/sales";
        
        String url = "http://127.0.0.1:5000/sales";
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-type", "application/json");
        
        JSONArray jsArray = new JSONArray();
        int i=0;
        Drink[] boisson;
        
        
        for(Player p : joueur_d){
            
            boisson = joueur_d[i].getDrinkOffered();
            
            //mapGame.getDrinkByPlayer()
            
            int nb_drinks = boisson.length;
      
            JSONObject jsPlayer = new JSONObject();
            
            Set<Map.Entry<String, Drink[]>> tmp = list_drink.entrySet();
        Iterator<Map.Entry<String, Drink[]>> it = tmp.iterator();
        while(it.hasNext()){
            Map.Entry<String, Drink[]> e = it.next();
            Drink[] drink = e.getValue();

            for(Drink dr : drink){
                
                jsPlayer.put("player",p.getPseudo());
                jsPlayer.put("item", dr.getName());
                jsPlayer.put("quantity", p.getSales());
                
                System.out.print(dr.getName());
                
                jsArray.put(i,jsPlayer);                
                
                
                
            }
            //mi.getKind()=="stand";
            //mi.getLocation();
            // mi.getOwner();
        }
            
//            for(Drink dr : boisson){
//                
//                jsPlayer.put("player",p.getPseudo());
//                jsPlayer.put("item", dr.getName());
//                jsPlayer.put("quantity", p.getSales());            
//                
//                
//            }

           // jsArray.put(i,jsPlayer);
            i++;            
 
                                
        }
        
System.out.println("\nSending 'POST' request to URL : " + url);
System.out.println("Post parameters : " + jsArray.toString());

String sale = jsArray.toString();
        
            

        }
        
        //!!!!!!!!!!!!!!!!!!!!!!
        
//        Set<Map.Entry<String, MapItem[]>> tmp = list_drinkbyplayer.entrySet();
//
//        Iterator<Map.Entry<String, MapGame>> it = tmp.iterator();
//        while(it.hasNext()){
//            Map.Entry<String, MapItem[]> e = it.next();
//            MapItem[] mapItems = e.getValue();
//            for(MapItem mi : mapItems){
//
//
//
//
//
//
//
//            }
//            //mi.getKind()=="stand";
//            //mi.getLocation();
//            // mi.getOwner();
//        }

//!!!!!!!!!!!!!!!!!!!!!!!!!!

//DECOMMENTER
//con.setDoOutput(true);
//OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
//wr.write(jsArray.toString());
//wr.flush();
//wr.close();
//
//int responseCode = con.getResponseCode();


//System.out.println("Response Code : " + responseCode);

//BufferedReader in = new BufferedReader(
//        new InputStreamReader(con.getInputStream()));
//String inputLine;
//StringBuffer response = new StringBuffer();
//
//while ((inputLine = in.readLine()) != null) {
//    response.append(inputLine);
//}
//in.close();
//
////print result
//System.out.println(response.toString());
//
    }
    

     

