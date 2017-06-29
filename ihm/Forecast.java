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
public class Forecast {
    
    private int dfn;
    String weather;
    
    public Forecast(int dfn, String weather){
    
        this.dfn = dfn;
        this.weather = weather;

    }
    
    public int getDfn(){
    
        return dfn;
    
    }
    
    public void setDfn(int dfn){
    
        this.dfn = dfn;
    
    }
    
    public String getWeather(){
        
        return weather;

    }
    
    public void setWeather(String weather){
        
        this.weather = weather;
  
    }
    
}
