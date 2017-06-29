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
public class Temps {
    
    private int timestamp;
    private Forecast[] forecast;
    
    public Temps(int timestamp, Forecast[] forecast){
    
        this.timestamp = timestamp;
        this.forecast = forecast;
    
    }
    
    public int getTimestamp(){
        
        return timestamp;
    
    }
    
    public void setTimestamp(int timestamp){
    
        this.timestamp = timestamp;
    
    }
    
    public Forecast[] getForecast(){
    
        return forecast;
    
    }
    
    public void setForecast(Forecast[] forecast){
        this.forecast = forecast;
    }
    
}
