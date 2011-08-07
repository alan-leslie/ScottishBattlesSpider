/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author al
 */
public class Position {
    private String theLatitude;
    private String theLongitude;
    
    // inputs should be in DMS format
    public Position(String latitude, 
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
    
    public String getLatitudeDD(){
        return convertDMSToDecimal(theLatitude);
    }
    
    public String getLongitudeDD(){
        return convertDMSToDecimal(theLongitude);
    }   
    
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
