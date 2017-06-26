/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm.model;

/**
 *
 * @author Nicolas
 */
public class Customer {
    
	Coordinates posPerso;
	float sensibilitePub;
	float sensibilitePrix;
	float sensibiliteChaud;
	float sensibiliteFroid;
	float sensibiliteAlcool;
	float sensibiliteSansAlcool;
	
	public Customer(){
		this.sensibilitePub = (float)Math.random();
		this.sensibilitePrix = 1-sensibilitePub;
		this.sensibiliteChaud = (float)Math.random();
		this.sensibiliteFroid = 1-sensibiliteChaud;
		this.sensibiliteAlcool = (float)Math.random();
		this.sensibiliteSansAlcool = 1-sensibiliteAlcool;
		this.posPerso =  new Coordinates((float)Math.random()*100,(float) Math.random()*100);
	}
	
	public String acheter(){
		
		
		return "playerName";
	}    
    
}
