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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/**
 *
 * @author Nicolas, Pierre
 */
public class Communication {
	
	private final String USER_AGENT = "Mozilla/5.0";

//public static void main (String[] args) throws Exception{
//
//	Communication com = new Communication();
//	
//	System.out.println("Send Http GET request to /map");
//	MapGame mapGame = com.getMap();
//	System.out.println("Send Http POST request to /sales");
//	com.sendSales(mapGame);
//	
//    }

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
                JSONArray playerInfoJson = new JSONArray(json.get("playerInfo").toString());
                n = playerInfoJson.length();
                Player[] playerTab = new Player[n];
                for(int i=0; i<n; i++){
                    JSONObject p = playerInfoJson.getJSONObject(i);
                    playerTab[i] = gson.fromJson(p.toString(), Player.class);
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
private void sendSales(MapGame mapGame) throws Exception {

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
    for(Player p : mapGame.getPlayerInfo()){
    	JSONObject jsPlayer = new JSONObject();
        
    	jsPlayer.put("nom",p.getPseudo());
    	jsPlayer.put("qteVendue", Math.random()*100);
    	jsPlayer.put("nomRecette", "Limonade");
    	jsPlayer.put("jour", "01/01/2017");
    	jsArray.put(i,jsPlayer);
    	i++;
    }
	
    
	con.setDoOutput(true);
	OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
	wr.write(jsArray.toString());
	wr.flush();
	wr.close();

	int responseCode = con.getResponseCode();
        
	System.out.println("\nSending 'POST' request to URL : " + url);
	System.out.println("Post parameters : " + jsArray.toString());
	System.out.println("Response Code : " + responseCode);

	BufferedReader in = new BufferedReader(
	        new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
	in.close();

	//print result
	System.out.println(response.toString());

}    
    
}
