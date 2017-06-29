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
public class MapItem {
    
	private String kind;
	private String owner;
	private Coordinates location;
	private float influence;
	
	public MapItem(String kind, String owner, Coordinates location, float influence) {
		this.kind = kind;
		this.owner = owner;
		this.location = location;
		this.influence = influence;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Coordinates getLocation() {
		return location;
	}

	public void setLocation(Coordinates location) {
		this.location = location;
	}

	public float getInfluence() {
		return influence;
	}

	public void setInfluence(float influence) {
		this.influence = influence;
	}

	
	public String toString() {
		return " [kind=" + kind + ", owner=" + owner + ", location=" + location + ", influence=" + influence+ "]";
	}    
    
}
