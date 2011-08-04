/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

/**
 *
 * @author al
 */
public class Position {
    private String theLatitude;
    private String theLongitude;
    
    Position(String latitude, 
            String longitude){
        theLatitude = latitude;
        theLongitude = longitude;        
    }
    
    public String getLatitude(){
        return theLatitude;
    }
    
    public String getLongitude(){
        return theLongitude;
    }    
}
