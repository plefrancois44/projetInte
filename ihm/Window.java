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
import java.util.TimerTask;
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
    JLabel label_j1 = new JLabel("Meteo actuelle :");
    JLabel label_jp1 = new JLabel("Meteo à venir:");
    JTextField meteo1 = new JTextField();
    JTextField meteo2 = new JTextField();
    JLabel label_temps = new JLabel("Temps écoulé:");
    JTextField temps_ecoule = new JTextField();
    
    String psuedo;
    
    JList list_players;
    
    DefaultListModel model;
    
    public Window(){
        
        Test tes = new Test();
        
        model = new DefaultListModel();
        list_players = new JList(model);
        JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(list_players);
        JScrollPane scrollPane = new JScrollPane(list_players);
        JScrollBar jsb = null;
        
        
        
        
        
        //Récupération des joueurs:
        Player[] player;
        
        player = tes.map.getPlayerInfo();
        
        
        int nb_players = player.length;
        
        String psuedo;
        
        for(int i = 0; i < nb_players; i++){
            
            psuedo = String.valueOf(player[i].getPseudo());
            
            model.addElement(psuedo);
            
        }
        
        //Meteo
        //comm.sendSales(mg);
        //Communication comm = new Communication();
        Temps temps = tes.comm.getTemps();
        
        Panel panel = new Panel();
        //int zeit = temps.getTimestamp();
        
        //*Forecast[] forecast = temps.getForecast();
        
//*        for(Forecast f : forecast){
//            int jour = f.getDfn();
//
//            if(jour == 0){
//
//
//                meteo1.setText(f.getWeather().toString());
//
//            }else if(jour == 1){
//
//                meteo2.setText(f.getWeather().toString());
//
//            }
//
//
//*        }





java.util.Timer t = new java.util.Timer();
t.schedule(new TimerTask() {
    @Override
    public void run() {
        
        Temps temps2 = tes.comm.getTemps();
        
        
        int zeit = temps2.getTimestamp();
        System.out.print(zeit);
        temps_ecoule.setText(String.valueOf(zeit));
        
        Forecast[] new_forecast = temps2.getForecast();
        
        for(Forecast f : new_forecast){
        
            int jour = f.getDfn();
            
            if(jour == 0){
         
                meteo1.setText(f.getWeather());
                
            }else if(jour == 1){
               
                meteo2.setText(f.getWeather());
                
            }
            
            
        
        }
        
//        System.out.print(new_forecast[0].getWeather());
//        System.out.print(new_forecast[1].getWeather());
//        
//        meteo1.setText(psuedo);
        
//        for(Forecast f : new_forecast){
//            //System.out.print("saert");
//            //int jour = f.getDfn();
//            
//            System.out.print(f.getWeather());
//            
//            
//            
////            if(jour == 0){
////                
////                System.out.print("jour1");
////                
////                
////                
////                meteo1.setText(f.getWeather());
////                
////            }else if(jour == 1){
////                
////                System.out.print("jour2");
////                
////                meteo2.setText(f.getWeather());
////                
////            }
//            
//            
//        }
        
        
        
//                if(zeit%12 == 0){
//
//
//                    Forecast[] new_forecast = temps.getForecast();
//                    updateForecast(new_forecast);
//
//
//                    //temps_ecoule.setText(String.valueOf(zeit));
//
//
//                }
    }
}, 0, 1000);


//temps_ecoule.setText(String.valueOf(zeit));




//
//        for(Forecast f : forecast){
//            int jour = f.getDfn();
//
//            if(jour == 0){
//
//
//                meteo1.setText(f.getWeather().toString());
//
//            }else if(jour == 1){
//
//                meteo2.setText(f.getWeather().toString());
//
//            }
//
//
//        }


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
this.add(label_j1);
this.add(label_jp1);
this.add(meteo1);
this.add(meteo2);
this.add(label_temps);
this.add(temps_ecoule);





// positionnement et dimensionnement manuel des boutons
label_drink.setBounds(40, 430, 100, 20);
nb_drinks.setBounds(100, 430, 400, 20);
nb_money.setBounds(100, 460, 100, 20);
label_money.setBounds(40, 460, 50, 20);
nb_sale.setBounds(100,490,100,20);
label_sale.setBounds(40, 490, 50, 20);
label_player.setBounds(370, 10, 150, 50);
list_players.setBounds(370, 50, 150, 200);
nb_sale.setBorder(null);
nb_drinks.setBorder(null);
nb_money.setBorder(null);
meteo1.setBorder(null);
meteo2.setBorder(null);
temps_ecoule.setBorder(null);
label_j1.setBounds(370,270,100,20);
label_jp1.setBounds(370, 320, 100, 20);
meteo1.setBounds(370, 290, 100, 20);
meteo2.setBounds(370, 350, 100, 20);
label_temps.setBounds(370, 380, 100, 20);
temps_ecoule.setBounds(370, 400, 100, 20);



list_players.addListSelectionListener(new ListSelectionListener(){
    
    
    
    public void valueChanged(ListSelectionEvent event){
        
        
        
        if(event.getValueIsAdjusting()){
            
            int i = list_players.getSelectedIndex();
            String boissons = "";
            Drink[] drinks;
            float money;
            int sales;
            
            //DRINKS
            
            drinks = player[i].getDrinkOffered();
            
            for(Drink d : drinks){
                boissons += d.getName()+": "+d.getPrice();
                
            }
            
            nb_drinks.setText(boissons);
            
            //MONEY
            
            money = player[i].getCash();
            
            nb_money.setText(String.valueOf(money));
            
            //SALES
            
            sales = player[i].getSales();
            
            nb_sale.setText(String.valueOf(sales));
            
            
            
            
            
            
            
            
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
    
    public void updateForecast(Forecast[] forecast){
        
        
        System.out.print("Update appelée");
        for(Forecast f : forecast){
            int jour = f.getDfn();
            
            if(jour == 0){
                
                
                meteo1.setText(f.getWeather().toString());
                
            }else if(jour == 1){
                
                meteo2.setText(f.getWeather().toString());
                
            }
            
            
        }
        
        
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
