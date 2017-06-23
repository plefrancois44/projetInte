/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author Nicolas
 */
public class Window extends JFrame implements ActionListener {
       
   // private JPanel content = new JPanel();  
  
  
    //Récuperer noms des joueurs dans data
    String[] data = {"Joueur 1", "Joueur 2", "Joueur 3"};
    int var_test = 1;
    public int selected_value;
  
    //Créations des composants    
    JList players = new JList(data);  
    JLabel label_drink = new JLabel("drinks :");
    JLabel label_money = new JLabel("money :");
    JLabel nb_drinks = new JLabel();
    JLabel nb_money = new JLabel();




    public Window(){
        
        Panel pan = new Panel();
        
        
        
       // pan.paintComponent(pan);
        
        
        this.setSize(550, 550);
        this.setTitle("Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        

            

        this.setContentPane(new Panel()); 
        

        this.setVisible(true);
        


                // nouvelle fenêtre
                //JFrame window = new JFrame("Interface");

              
                // pas de layout manager pour cette fenêtre : 
                // on positionnera les composants à la min
                this.setLayout(null);
                 

                 
                // ajout des boutons à la fenêtre

                this.add(players);
                this.add(label_drink);
                this.add(label_money);
                this.add(nb_money);
                this.add(nb_drinks);
                
                
                 
                // positionnement et dimensionnement manuel des boutons
                label_drink.setBounds(40, 400, 50, 20);
                nb_drinks.setBounds(100, 400, 20, 20);
                nb_money.setBounds(100, 430, 20, 20);
                label_money.setBounds(40, 430, 50, 20);
                
                
                players.setBounds(370, 50, 150, 200);
                
                players.addListSelectionListener(new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event){
                    
                        if(event.getValueIsAdjusting()){
                            
                            
                            selected_value = players.getSelectedIndex();
                            
                            switch(selected_value){
                            
                                case 0: nb_drinks.setText(String.valueOf(var_test));
                                        nb_money.setText(String.valueOf(var_test));
                                        
                                        
                                        
                                        
                                        break;
                                        
                                case 1: nb_drinks.setText(String.valueOf(2));
                                        nb_money.setText(String.valueOf(2222));
                                        break;
                                 
                                        
                            
                            }
                           
                        
                        
                        }
                  
                    }
                
                
                });

          
                // quitter le programme lorsqu'on ferme la fenêtre
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                // dimensionnement en affichage de la fenêtre
                this.setSize(550, 550);
                this.setResizable(false);
                

            
                this.setVisible(true);
                
                
   }
    public int getSelectedValue(){
    
        return selected_value;
        
    }
     

    public  void    actionPerformed(ActionEvent e)
    {
        //quand on a cliqué sur le bouton ici
        System.out.println("Ici !");
    }    

}
