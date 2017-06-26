/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm.model;

import java.util.Arrays;

/**
 *
 * @author Nicolas
 */
public class Player {
    
	private float cash;
	private int sales;
	private float profit;
	private Drink[] drinkOffered;
	private String pseudo;
	
	
	public Player(float cash, int sales, float profit, Drink[] drinkOffered, String pseudo) {
		super();
		this.cash = cash;
		this.sales = sales;
		this.profit = profit;
		this.drinkOffered = drinkOffered;
		this.pseudo = pseudo;
                
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public int getSales() {
		return sales;
	}

	public void setSales(int sales) {
		this.sales = sales;
	}

	public float getProfit() {
		return profit;
	}

	public void setProfit(float profit) {
		this.profit = profit;
	}

	public Drink[] getDrinkOffered() {
		return drinkOffered;
	}

	public void setDrinkOffered(Drink[] drinkOffered) {
		this.drinkOffered = drinkOffered;
	}

	@Override
	public String toString() {
		return "Player [pseudo = "+ pseudo +", cash=" + cash + ", sales=" + sales + ", profit=" + profit + ", drinkOffered="
				+ Arrays.toString(drinkOffered) + "]";
	}    
    
}
