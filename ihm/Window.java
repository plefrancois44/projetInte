/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ihm;

import ihm.model.*;
import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author Nicolas
 */
public class Window extends JFrame implements ActionListener {
    
    // private JPanel content = new JPanel();
    
    
    //Récuperer noms des joueurs dans data
    
    int var_test = 1;
    //Pour récuperer selected_value dans Panel, il faut créer une interface ou dire a Window d'envoyer la variable vers Panel
    private int selected_value = 0;
    
    //Créations des composants
    
    JLabel label_drink = new JLabel("drinks :");
    JLabel label_money = new JLabel("money :");
    JLabel nb_drinks = new JLabel();
    JLabel nb_money = new JLabel();
    JLabel label_sale = new JLabel("sales :");
    JLabel nb_sale = new JLabel();
    
    //Graphics graph;
    
    
    String psuedo;
    
        //String[] data = {"Joueur 1", "Joueur 2", "Joueur 3"};
        JList list_players; 
        
        DefaultListModel model;
    
    
    
    public Window(){
        
        Test tes = new Test();
        
        model = new DefaultListModel();
        list_players = new JList(model);
        

        
        //Récupération des joueurs:
        Player[] player;

        //Player joueur = null;
        
        player = tes.map.getPlayerInfo();
        int nb_players = player.length;
        String psuedo;
        
        
        
        for(int i = 0; i < nb_players; i++){
            
            psuedo = String.valueOf(player[i].getPseudo());
            
            
            
            model.addElement(psuedo);
           
            
            
        }


        //player = joueur.getPseudo();
        
       
        //Test test = new Test();
        Panel panel = new Panel();
       
        panel.setSelectedValue(selected_value);
        // pan.paintComponent(pan);
        //panel.setBackground(Color.yellow);
        
        
        this.setSize(650, 650);
        this.setTitle("Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        this.setContentPane(panel);
        
        
        this.setVisible(true);
        
        
        
        // nouvelle fenêtre
        //JFrame window = new JFrame("Interface");
        
        
        // pas de layout manager pour cette fenêtre :
        // on positionnera les composants à la min
        this.setLayout(null);
        
        
        
        // ajout des boutons à la fenêtre
        
        this.add(list_players);
        this.add(label_drink);
        this.add(label_money);
        this.add(nb_money);
        this.add(nb_drinks);
        this.add(nb_sale);
        this.add(label_sale);
        
        
        
        // positionnement et dimensionnement manuel des boutons
        label_drink.setBounds(40, 400, 100, 20);
        nb_drinks.setBounds(100, 400, 300, 20);
        nb_money.setBounds(100, 430, 200, 20);
        label_money.setBounds(40, 430, 50, 20);
        nb_sale.setBounds(100,460,100,20);
        label_sale.setBounds(40, 460, 50, 20);
        
        
        list_players.setBounds(370, 50, 150, 200);
        
        list_players.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event){
                
                if(event.getValueIsAdjusting()){
                    
                    
                    selected_value = list_players.getSelectedIndex();
                    
                    //Drinks
                    Map mymap;
                    String boissons = "";
                    Drink[] drinks;
                    float money;
                    int sales;
                    
                    Test test = new Test();
                    
                    switch(selected_value){
                        
                        case 0:
                            nb_drinks.setText("");
                            //Test test = new Test();
                            drinks = player[0].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+" ";
                                d.getPrice();
                            }
                            
                            
                            
                            //Drinks
                            mymap = test.map.getDrinkByPlayer();
                            //boissons = test.map.drinksByPlayerToString();
                            nb_drinks.setText(boissons);
                            

                            //Money
                            money = player[0].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            
                            
                            //Sales
                            sales = player[0].getSales();
                            nb_sale.setText(String.valueOf(sales));

                            
                            
                            break;
                            
                        case 1:

                           // Test test = new Test();
                            
                            drinks = player[1].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+" ";
                                d.getPrice();
                            }
                            
                            
                            
                            //Drinks
                            mymap = test.map.getDrinkByPlayer();
                            //boissons = test.map.drinksByPlayerToString();
                            nb_drinks.setText(boissons);
                            

                            //Money
                            money = player[1].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            
                            
                            //Sales
                            sales = player[1].getSales();
                            nb_sale.setText(String.valueOf(sales));

                                                        
                            
                            
                            break;    
//                            
                        
                        
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
