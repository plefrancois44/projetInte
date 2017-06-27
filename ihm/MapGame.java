/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm.model;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Nicolas, Pierre
 */
public class MapGame {
    
	private Region region;
	private String[] ranking;
	private Map<String, MapItem[]> itemsByPlayer;
	private Player[] playerInfo;
	private Map<String, Drink[]> drinksByPlayer;
	
	public MapGame( String[] ranking, Region region,Player[] playerInfo,
			Map<String, Drink[]> drinkByPlayer, Map<String, MapItem[]> itemsByPlayer) {
		this.region = region;
		this.ranking = ranking;
		this.itemsByPlayer = itemsByPlayer;
		this.playerInfo = playerInfo;
		this.drinksByPlayer = drinkByPlayer;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String[] getRanking() {
		return ranking;
	}

	public void setRanking(String[] ranking) {
		this.ranking = ranking;
	}

	public Map<String, MapItem[]> getItemsByPlayer() {
		return itemsByPlayer;
	}

	public void setItemsByPlayer(Map<String, MapItem[]> itemsByPlayer) {
		this.itemsByPlayer = itemsByPlayer;
	}

	public Player[] getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(Player[] playerInfo) {
		this.playerInfo = playerInfo;
	}

	public Map<String, Drink[]> getDrinkByPlayer() {
		return drinksByPlayer;
	}

	public void setDrinkByPlayer(Map<String, Drink[]> drinkByPlayer) {
		this.drinksByPlayer = drinkByPlayer;
	}

	public String itemsByPlayerToString(){
		String str ="";
		Set<Entry<String, MapItem[]>> tmp = itemsByPlayer.entrySet();
		Iterator<Entry<String, MapItem[]>> it = tmp.iterator();
		while(it.hasNext()){
			Entry<String, MapItem[]> e = it.next();
			MapItem[] mapItems = e.getValue();
			str += "\n"+e.getKey()+": ";
			for(MapItem mi : mapItems) str+=mi.toString();
		}
		return str;
	}
	
	public String drinksByPlayerToString(){
		String str ="";
		Set<Entry<String,Drink[]>> tmp = drinksByPlayer.entrySet();
		Iterator<Entry<String,Drink[]>> it = tmp.iterator();
		while(it.hasNext()){
			Entry<String,Drink[]> e = it.next();
			Drink[] drinks = e.getValue();
			str += "\n"+e.getKey()+": ";
			for(Drink drink : drinks) str+=drink.toString();
		}
		return str;
	}
	
	public String toString() {
		return "MapGame [region=" + region.toString() + "\n"+
						", ranking=" + Arrays.toString(ranking) + "\n"+
						", itemsByPlayer="	+ itemsByPlayerToString() + "\n"+
						", playerInfo=" + Arrays.toString(playerInfo) + "\n"+
						", drinkByPlayer=" + drinksByPlayerToString() + "\n"+
						"]";
	}    
    
}
