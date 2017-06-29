/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ihm;

import java.awt.Graphics;
import javax.swing.JPanel;
import ihm.model.*;
import java.awt.Color;
import java.awt.Image;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;

/**
 *
 * @author Nicolas
 */


public class Panel extends JPanel {
    
    //Récupérer le plan à afficher en fond
    ImageIcon map = new ImageIcon("C:/Users/Nicolas/Documents/maps.jpg");
    Image map2 = map.getImage();
    
    //Instancier classe Test
    Test test2 = new Test();
    
    //Initialisation variables
    Coordinates coord;
    Map<String, MapItem[]> itemsByPlayer= test2.comm.getMap().getItemsByPlayer();
    
    int stand_x;
    int stand_y;
    int stand_x_influence;
    int stand_y_influence;
    int ad_x;
    int ad_y;
    int influence;
    Region region;
    String spieler;

    
    
    public void paintComponent(Graphics g){

        //Afficher la carte
        g.drawImage(map2, 10, 10, 350, 350, this);

        
        //Récupérer positions des stands ainsi que leur influences
        Set<Map.Entry<String, MapItem[]>> tmp = itemsByPlayer.entrySet();
        Iterator<Map.Entry<String, MapItem[]>> it = tmp.iterator();
        while(it.hasNext()){
            Map.Entry<String, MapItem[]> e = it.next();
            MapItem[] mapItems = e.getValue();
            for(MapItem mi : mapItems){

                influence = (int) mi.getInfluence() * 10;
            
                stand_x = (int) ((mi.getLocation().getLongitude() - 10) * 3);
                stand_y = (int) ((mi.getLocation().getLatitude() - 10) * 3);
                stand_x_influence = stand_x - (influence / 2) + 5;
                stand_y_influence = stand_y - (influence / 2) + 5;

                //Si stand
                if("stand".equals(mi.getKind())){
   
                    spieler = mi.getOwner();
                    
                    //ZONE INFLUENCE
                    
                    g.setColor(Color.PINK);
                    g.drawOval(stand_x_influence, stand_y_influence, influence, influence);
  
                    //STAND
                    
                    g.setColor(Color.BLACK);
                    g.fillOval(stand_x, stand_y, 10, 10);
                    g.drawString(spieler, stand_x + 5, stand_y );
                    
                    //Reinitialiser variables
                    stand_x = 0;
                    stand_y = 0;

                //Si panneau pub    
                } else if("ad".equals(mi.getKind())){
                
                    ad_x = ((int) (mi.getLocation().getLongitude() + 10)) * 3;
                    ad_y = ((int) (mi.getLocation().getLatitude() + 10)) * 3;
                    spieler = mi.getOwner();
                    
                    influence = (int) mi.getInfluence() * 20;
                    
                    //ZONE INFLUENCE
                    g.setColor(Color.PINK);
                    g.fillOval((ad_x - influence / 2) + 5, (ad_y - influence / 2) + 5, influence, influence);
                    
                    //PANNEAU
                    g.setColor(Color.BLUE);
                    g.fillOval(ad_x, ad_y, 10, 10);
                    g.drawString(spieler,ad_x,ad_y-5);
                    
                    //Reinitialiser variables
                    ad_x = 0;
                    ad_y = 0;
                    g.setColor(Color.BLACK);
  
                }
       
            }

        }
   
    }
    
    
}
