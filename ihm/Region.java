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
public class Region {
    
	private Coordinates center;
	private CoordinatesSpan span;
	
	public Region(Coordinates center, CoordinatesSpan span) {
		this.center = center;
		this.span = span;
	}

	public Coordinates getCenter() {
		return center;
	}

	public void setCenter(Coordinates center) {
		this.center = center;
	}

	public CoordinatesSpan getSpan() {
		return span;
	}

	public void setSpan(CoordinatesSpan span) {
		this.span = span;
	}

	@Override
	public String toString() {
		return "Region [ center= " + center.toString() + " , span= " + span.toString() + " ]";
	}    
    
}
