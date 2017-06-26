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
public class Ingredient {
    
	private String name;
	private float cost;
	private boolean hasAlcool;
	private boolean isCold;

	public Ingredient(String name, float cost, boolean hasAlcool, boolean isCold) {
		this.hasAlcool = hasAlcool;
		this.name = name;
		this.cost = cost;
		this.isCold = isCold;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public boolean hasAlcool() {
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
    
}
