/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JFrame;

/**
 *
 * @author Nicolas
 */
public class Communication {

public static void main (String[] args){
  
    URL heroku_URL = null; //URL du server
    int text_server; //Helloworld du server
      
    try {
        heroku_URL = new URL("http://bushukan-imerir.herokuapp.com/");
        //Ouvrir connexion entre JAVA et le server
        HttpURLConnection connexion = (HttpURLConnection)heroku_URL.openConnection();
        InputStream flux = connexion.getInputStream();
        
        //Vérifier que la connexion est OK
        System.out.println("Status de la connexion : " + connexion.getResponseMessage());
        
        if (connexion.getResponseCode() == HttpURLConnection.HTTP_OK){
          //Tant que le flux n'est pas terminer, faire un print  
          while ((text_server=flux.read())!= -1){
              
            System.out.print((char) text_server);
            
          }
          System.out.print("\n");
        }
        
        //fermer le flux et deconnecter
        flux.close(); 
        connexion.disconnect();
    } 
    catch(Exception e) {
        System.out.println(e);
    }
    
     
     
      //POST
     /* 
      try{
      
        URL url = new URL("http://bushukan-imerir.herokuapp.com/");
        HttpURLConnection connex = (HttpURLConnection) url.openConnection();
        connex.setDoOutput(true);
        connex.setRequestMethod("POST");
        connex.setRequestProperty("Content-type", "application/json");
        connex.setRequestProperty("Accept", "");
      
        OutputStreamWriter writer = new OutputStreamWriter(connex.getOutputStream());
        
        //writer.write("salut", 0, 5);
        writer.write("\"Salut\":12");
     
        writer.flush();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connex.getInputStream()));
        
        while((line = reader.readLine()) != null){
        
            System.out.println(line);
        
        }
        
        writer.close();
        reader.close();
        
      } catch(Exception e){
      
          System.out.println(e);
      
      }
     */
 
      
    }
}


    
    

