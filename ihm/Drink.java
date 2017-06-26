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
public class Drink {
    
        private String name;
	private float price;
	private boolean hasAlcool;
	private boolean isCold;
	private Ingredient[] listIngredient;
	
	public Drink(String name, float price, boolean hasAlcool, boolean isCold) {
		this.name = name;
		this.price = price;
		this.hasAlcool = hasAlcool;
		this.isCold = isCold;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public boolean isHasAlcool() {
		return hasAlcool;
	}

	public void setHasAlcool(boolean hasAlcool) {
		this.hasAlcool = hasAlcool;
	}

	public boolean isCold() {
		return isCold;
	}

	public void setCold(boolean isCold) {
		this.isCold = isCold;
	}

	@Override
	public String toString() {
		return "Drink [name=" + name + ", price=" + price + ", hasAlcool=" + hasAlcool + ", isCold=" + isCold
				+ ", listIngredient=" + Arrays.toString(listIngredient) + "]";
	}    
    
}
