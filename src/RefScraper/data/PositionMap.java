
package RefScraper.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Map for position data that is known before scrape is run
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

    /*
     * @param - the key of the position searched for
     * @return - position corresponding to the key or null if not found
     */

    synchronized Position getPosition(String key) {
        Position thePos = theMap.get(key);
        return thePos;
    }
}
