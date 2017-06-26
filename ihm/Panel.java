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
import java.awt.Image;
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
   
   int player_x;
   int player_y;
    
    
    
    
    
    public void paintComponent(Graphics g){

        coord = test2.map.getRegion().getCenter();
        
        player_x = (int) coord.getLongitude();
        player_y = (int) coord.getLatitude();
        
        player_x = player_x + 50;
        player_y = player_y + 50;
        
        
        //g.drawRect(10, 10, 400, 400);
        g.drawImage(map2, 10, 10, 350, 350, this);

        //Créer une troisieme classe intermediaire

        //System.out.print(selectedValue);
        g.fillOval(player_x, player_y, 5, 5);
        
        



        //Cercle joueur
       // g.fillOval(player_x, player_y, 5, 5);


    
  }

    public void setSelectedValue(int selectedValue)
    {
        this.selectedValue = selectedValue;
    }

}
