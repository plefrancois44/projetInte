/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ihm;

import java.awt.Graphics;
import javax.swing.JPanel;
import ihm.Window;
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
    
    //Window win = new Window();
    // Test test = new Test();
    private int selectedValue ;
    
    
//    public Graphics g2;
//
//
//    public Graphics getGraph(){
//        return this.g2;
//    }
    /**
     *
     */
    
    ImageIcon map = new ImageIcon("C:/Users/Nicolas/Documents/maps.jpg");
    
    Image map2 = map.getImage();
    
    Test test2 = new Test();
    
    Coordinates coord;
    
    Map<String, MapItem[]> itemsByPlayer= test2.comm.getMap().getItemsByPlayer();
    
    int stand_x;
    int stand_y;
    int ad_x;
    int ad_y;
    int influence;
    
    //Player[] spieler;
    Region region;
    String spieler;
    
    
    
    
    
    public void paintComponent(Graphics g){
        
        
        
        //coord = test2.map.getRegion().getCenter();
        
        // player_x = (int) coord.getLongitude();
        //player_y = (int) coord.getLatitude();
        
        //player_x = player_x + 50;
        //player_y = player_y + 50;
        
        
        //g.drawRect(10, 10, 400, 400);
        
        
        g.drawImage(map2, 10, 10, 350, 350, this);
        
        //Créer une troisieme classe intermediaire
        
        //System.out.print(selectedValue);
        
        
        
        
        Set<Map.Entry<String, MapItem[]>> tmp = itemsByPlayer.entrySet();
        Iterator<Map.Entry<String, MapItem[]>> it = tmp.iterator();
        while(it.hasNext()){
            Map.Entry<String, MapItem[]> e = it.next();
            MapItem[] mapItems = e.getValue();
            for(MapItem mi : mapItems){

                if("stand".equals(mi.getKind())){
                    stand_x = ((int) mi.getLocation().getLongitude() + 10) * 3;
                    stand_y = ((int) mi.getLocation().getLatitude() + 10) * 3;
                    spieler = mi.getOwner();
                    
                    influence = (int) mi.getInfluence() * 20;
                    
                    g.setColor(Color.PINK);
                    
                    
                    g.fillOval(stand_x - 5, stand_y - 5, influence, influence);
                    
                    g.setColor(Color.BLACK);
                    g.fillOval(stand_x, stand_y, 10, 10);
                    g.drawString(spieler, stand_x, stand_y - 5);
                    
                    //Reinit
                    stand_x = 0;
                    stand_y = 0;
                    
                    
                    
                    
                    
                } else if("ad".equals(mi.getKind())){
                
                    ad_x = ((int) (mi.getLocation().getLongitude() + 10)) * 3;
                    ad_y = ((int) (mi.getLocation().getLatitude() + 10)) * 3;
                    spieler = mi.getOwner();
                    
                    influence = (int) mi.getInfluence() * 20;
                    
                    
                    g.setColor(Color.PINK);
                    g.fillOval((ad_x - influence / 2) + 5, (ad_y - influence / 2) + 5, influence, influence);
                    
                    g.setColor(Color.BLUE);
                    g.fillOval(ad_x, ad_y, 10, 10);
                    g.drawString(spieler,ad_x,ad_y-5);
                    
                    //Reinit
                    ad_x = 0;
                    ad_y = 0;
                    g.setColor(Color.BLACK);
                    
                    
                    
                
                }
                
                
                
            }
            //mi.getKind()=="stand";
            //mi.getLocation();
            // mi.getOwner();
        }
        //spieler = test2.comm.getMap().getItemsByPlayer()
        
        
        //region.getCenter().getLatitude()
        
        // !! afficher latitude et longitude de chaque joueurs !!
        
        
        
        //recuperer joueur selectionner, en fonction du joueur faire un switch,
        //et recuperer latitude et longitude pour chaque joueurs
        
        
        
        
        
        
        //Cercle joueur
        // g.fillOval(player_x, player_y, 5, 5);
        
        
        
    }
    
    public void setSelectedValue(int selectedValue)
    {
        this.selectedValue = selectedValue;
    }
    
}
