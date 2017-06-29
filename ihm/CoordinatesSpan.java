/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm.model;

/**
 *
 * @author Nicolas, Pierre
 */
public class CoordinatesSpan {
    
	private float latitudeSpan;
	private float longitudeSpan;
		
	public CoordinatesSpan(float latitude, float longitude) {
		this.latitudeSpan = latitude;
		this.longitudeSpan = longitude;
	}
	public float getLatitude() {
		return latitudeSpan;
	}
	public void setLatitude(float latitude) {
		this.latitudeSpan = latitude;
	}
	public float getLongitude() {
		return longitudeSpan;
	}
	public void setLongitude(float longitude) {
		this.longitudeSpan = longitude;
	}

	public String toString() {
		return "(" + latitudeSpan + ", " + longitudeSpan + ")";
	}    
    
    
}
