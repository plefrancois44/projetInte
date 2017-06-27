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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Nicolas
 */
public class Window extends JFrame implements ActionListener {
    
    
    int var_test = 1;
    private int selected_value = 0;
    
    
    //Créations des composants
    
    JLabel label_drink = new JLabel("drinks :");
    JLabel label_money = new JLabel("money :");
    JLabel label_sale = new JLabel("sales :");
    JLabel label_player = new JLabel("Players :");
    JTextField nb_drinks = new JTextField();
    JTextField nb_money = new JTextField();
    JTextField nb_sale = new JTextField();
    
    String psuedo;
    
    JList list_players;
    
    DefaultListModel model;
    
    public Window(){
        
        Test tes = new Test();
        
        model = new DefaultListModel();
        list_players = new JList(model);
        
        
        
        //Récupération des joueurs:
        Player[] player;
        
        player = tes.map.getPlayerInfo();
        int nb_players = player.length;
        
        String psuedo;
        for(int i = 0; i < nb_players; i++){
            
            psuedo = String.valueOf(player[i].getPseudo());
            
            model.addElement(psuedo);
            
        }
        
        
        Panel panel = new Panel();
        
        panel.setSelectedValue(selected_value);
        
        this.setContentPane(panel);
        // pan.paintComponent(pan);
        //panel.setBackground(Color.yellow);
        
        
        this.setSize(650, 650);
        this.setTitle("Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        
        
        
        this.setVisible(true);
        
        this.setLayout(null);
        
        // ajout des boutons à la fenêtre
        
        this.add(list_players);
        this.add(label_drink);
        this.add(label_money);
        this.add(nb_money);
        this.add(nb_drinks);
        this.add(nb_sale);
        this.add(label_sale);
        this.add(label_player);
        
        
        // positionnement et dimensionnement manuel des boutons
        label_drink.setBounds(40, 400, 100, 20);
        nb_drinks.setBounds(100, 400, 400, 20);
        nb_money.setBounds(100, 430, 100, 20);
        label_money.setBounds(40, 430, 50, 20);
        nb_sale.setBounds(100,460,100,20);
        label_sale.setBounds(40, 460, 50, 20);
        label_player.setBounds(370, 10, 150, 50);
        list_players.setBounds(370, 50, 150, 200);
        nb_sale.setBorder(null);
        nb_drinks.setBorder(null);
        nb_money.setBorder(null);
        
        
        
        list_players.addListSelectionListener(new ListSelectionListener(){
            
            
            
            public void valueChanged(ListSelectionEvent event){
                
                
                
                if(event.getValueIsAdjusting()){
                    
                    selected_value = list_players.getSelectedIndex();
                    
                    
                    String boissons = "";
                    Drink[] drinks;
                    float money;
                    int sales;
                    
                    
                    switch(selected_value){
                        
                        case 0:
                            
                            //DRINKS
                            
                            drinks = player[0].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+": "+d.getPrice();
                                
                            }
                            
                            nb_drinks.setText(boissons);
                            
                            //MONEY
                            
                            money = player[0].getCash();
                            
                            nb_money.setText(String.valueOf(money));
                            
                            //SALES
                            
                            sales = player[0].getSales();
                            
                            nb_sale.setText(String.valueOf(sales));
                            
                            break;
                            
                        case 1:
                            
                            //DRINKS
                            drinks = player[1].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+": "+d.getPrice();
                            }
                            
                            nb_drinks.setText(boissons);
                            
                            
                            //MONEY
                            money = player[1].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            //SALES
                            sales = player[1].getSales();
                            nb_sale.setText(String.valueOf(sales));
                            break;
                            
                        case 2:
                            
                            //DRINKS
                            drinks = player[2].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+": "+d.getPrice();
                            }
                            
                            nb_drinks.setText(boissons);
                            
                            
                            //MONEY
                            money = player[2].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            //SALES
                            sales = player[2].getSales();
                            nb_sale.setText(String.valueOf(sales));
                            
                            
                            break;
                            
                        case 3:
                            
                            //DRINKS
                            drinks = player[3].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+": "+d.getPrice();
                            }
                            
                            nb_drinks.setText(boissons);
                            
                            
                            //MONEY
                            money = player[3].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            //SALES
                            sales = player[3].getSales();
                            nb_sale.setText(String.valueOf(sales));
                            
                            
                            break;
                            
                        case 4:
                            
                            //DRINKS
                            drinks = player[3].getDrinkOffered();
                            
                            for(Drink d : drinks){
                                boissons += d.getName()+": "+d.getPrice();
                            }
                            
                            nb_drinks.setText(boissons);
                            
                            
                            //MONEY
                            money = player[3].getCash();
                            nb_money.setText(String.valueOf(money));
                            
                            //SALES
                            sales = player[3].getSales();
                            nb_sale.setText(String.valueOf(sales));
                            
                            
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
