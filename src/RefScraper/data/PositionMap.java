/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author al
 */
public class PositionMap {
    private static PositionMap thePositionMap;
    
    public synchronized static PositionMap getInstance(){
        if(thePositionMap == null){
            thePositionMap = new PositionMap();
        }
        
        return thePositionMap;
    }

    private Map<String, Position> theMap;

    private PositionMap() {
        FileReader theReader = null;
        theMap = new HashMap<String, Position>();

        try {
            theReader = new FileReader("KnownPositions.txt");
            BufferedReader in = new BufferedReader(theReader);
            
            String theLine = null;
            
            while ((theLine = in.readLine()) != null) {
                String theLineArr[] = theLine.split(",");
                
                if(theLineArr.length > 2){
                    theMap.put(theLineArr[0], new Position(theLineArr[1], theLineArr[2]));
                }
            }

        } catch (IOException e) {
            // ...
        } finally {
            if (null != theReader) {
                try {
                    theReader.close();
                } catch (IOException e) {
                    /* .... */
                }
            }
        }
    }

    synchronized String getLongitude(String key) {
        Position thePos = theMap.get(key);

        if (thePos != null) {
            return thePos.getLongitude();
        } else {
            return null;
        }
    }

    synchronized String getLatitude(String key) {
        Position thePos = theMap.get(key);

        if (thePos != null) {
            return thePos.getLatitude();
        } else {
            return null;
        }
    }
}
