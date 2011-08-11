package RefScraper.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to model position (latitude and longitude)
 * @author al
 */
public class Position {
    private String theLatitude;
    private String theLongitude;
    
    /**
     * 
     * @param latitude - dms format
     * @param longitude - dms format 
     */
    public Position(String latitude, 
            String longitude){
        theLatitude = latitude;
        theLongitude = longitude;        
    }
    
    /**
     * 
     * @return - latitude in dms format
     */
    public String getLatitude(){
        return theLatitude;
    }
    
    /**
     * 
     * @return - longitude in dms format
     */
    public String getLongitude(){
        return theLongitude;
    }   
    
    /**
     * 
     * @return -whether the position data is complete and valid
     */
    public boolean isComplete(){
        boolean latComplete = (theLatitude != null && !theLatitude.isEmpty());
        boolean lonComplete = (theLongitude != null && !theLongitude.isEmpty());
        
        return (latComplete && lonComplete);
    }
    
    /**
     * 
     * @return - latitude in decimal format
     */
    public String getLatitudeDD(){
        return convertDMSToDecimal(theLatitude);
    }
    
    /**
     * 
     * @return - longitude in decimal format
     */
    public String getLongitudeDD(){
        return convertDMSToDecimal(theLongitude);
    }   
    
    /**
     * @param lat or long in dms format
     * @return - decimal format version
     */
    private String convertDMSToDecimal(String dmsString) {
        String retVal = "";
        List<Double> dmsList = new ArrayList<Double>();
        boolean isNegative = dmsString.contains("S") || dmsString.contains("W");
        String tmpString = "";

        for (int i = 0; i < dmsString.length(); ++i) {
            if (Character.isDigit(dmsString.charAt(i))) {
                tmpString = tmpString + dmsString.charAt(i);
            } else {
                if (!tmpString.isEmpty()) {
                    dmsList.add(new Double(Double.parseDouble(tmpString)));
                    tmpString = "";
                }
            }
        }

        Double decimalDeg = 0.0;

        if (dmsList.size() > 0) {
            decimalDeg += dmsList.get(0);
        }

        if (dmsList.size() > 1) {
            decimalDeg += dmsList.get(1) / 60.0;
        }

        if (dmsList.size() > 2) {
            decimalDeg += dmsList.get(2) / 3600.0;
        }

        retVal = decimalDeg.toString();

        if (isNegative) {
            retVal = "-" + retVal;
        }

        return retVal;
    }

}
