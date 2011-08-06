/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author al
 */
public class PeriodMap {
    private static PeriodMap thePeriodMap;
    
    public synchronized static PeriodMap getInstance(){
        if(thePeriodMap == null){
            thePeriodMap = new PeriodMap();
        }
        
        return thePeriodMap;
    }
    
    private Map<String, Period> theMap;

    private PeriodMap() {
        FileReader theReader = null;
        theMap = new HashMap<String, Period>();

        try {
            theReader = new FileReader("KnownDates.txt");
            BufferedReader in = new BufferedReader(theReader);
            
            String theLine = null;
            DateFormat theDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            
            while ((theLine = in.readLine()) != null) {
                String theLineArr[] = theLine.split(",");
                
                if(theLineArr.length > 2){
                    try {
                        Date newStartDate = theDateFormatter.parse(theLineArr[1]);
                        Date newEndDate = theDateFormatter.parse(theLineArr[2]);
                        theMap.put(theLineArr[0], new Period(newStartDate, newEndDate));
                    } catch (ParseException ex) {
                        Logger.getLogger(PeriodMap.class.getName()).log(Level.SEVERE, null, ex);
                    }       
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

    synchronized Date getStartDate(String key) {
        Period thePeriod = theMap.get(key);

        if (thePeriod != null) {
            return thePeriod.getStartDate();
        } else {
            return null;
        }
    }

    synchronized Date getEndDate(String key) {
        Period thePeriod = theMap.get(key);

        if (thePeriod != null) {
            return thePeriod.getEndDate();
        } else {
            return null;
        }
    }    
}