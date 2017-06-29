/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ihm;

import ihm.model.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Nicolas
 */

public class Window extends JFrame implements ActionListener {
    
 
    //Créations des label
    
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
    JList list_players;
    
    DefaultListModel model;
    
    
    
    public Window(){
        
        
        //Instancier classe "intermediaire" test
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
        

        //Instancier Panel qui dessine la carte
        Panel panel = new Panel();

        
        //Boucle qui execute le code toutes les secondes
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                //Récupérer Temps qui contient le temps écoulé depuis le début de la partie ainsi que la météo actuelle et prévue
                Temps temps = tes.comm.getTemps();
                
                //Récupérer de l'Arduino du temps écoulé depuis le début de la partie
                int time_stamp = temps.getTimestamp();
                
                //Afficher le temps écoulé dans label
                temps_ecoule.setText(String.valueOf(time_stamp));

                //Récupérer de l'Arduino le tableau de prévisions météo
                Forecast[] new_forecast = temps.getForecast();

                //Parcour le tableau de prévision 
                for(Forecast f : new_forecast){
                    //Récupérer le jour correspondant à la météo
                    int jour = f.getDfn();
                    
                   
                    if(jour == 0){

                        meteo1.setText(f.getWeather());

                    }else if(jour == 1){

                        meteo2.setText(f.getWeather());

                    }



                }
        
            }
        }, 0, 1000);


        //Configurer la fenêtre       
        this.setContentPane(panel);
        this.setSize(650, 650);
        this.setTitle("Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.setLayout(null);

        //Ajout des labels à la fenêtre

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

        
        
        //Position et dimensions des labels
        label_drink.setBounds(40, 430, 100, 20);
        nb_drinks.setBounds(100, 430, 400, 20);
        nb_money.setBounds(100, 460, 100, 20);
        label_money.setBounds(40, 460, 50, 20);
        nb_sale.setBounds(100,490,100,20);
        label_sale.setBounds(40, 490, 50, 20);
        label_player.setBounds(370, 10, 150, 50);
        list_players.setBounds(370, 50, 150, 200);
        label_j1.setBounds(370,270,100,20);
        label_jp1.setBounds(370, 320, 100, 20);
        meteo1.setBounds(370, 290, 100, 20);
        meteo2.setBounds(370, 350, 100, 20);
        label_temps.setBounds(370, 380, 100, 20);
        temps_ecoule.setBounds(370, 400, 100, 20);
        nb_sale.setBorder(null);
        nb_drinks.setBorder(null);
        nb_money.setBorder(null);
        meteo1.setBorder(null);
        meteo2.setBorder(null);
        temps_ecoule.setBorder(null);


        //ActionListener permet d'"écouter" un évenement
        list_players.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent event){
                //Si un autre joueur est selectionné dans la liste
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
   
    public  void    actionPerformed(ActionEvent e)
    {
        System.out.println("Salut");
    }
    
}
