/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author al
 */
public class PeriodMap {
    private Map<String, Period> theMap;

    PeriodMap() {
        theMap = new HashMap<String, Period>();
        theMap.put("BattleofDalnaspidal", new Period(new Date(19, 7, 1654), new Date(19, 7, 1654)));
        theMap.put("BattleofMamGarvia", new Period(new Date(1, 6, 1187), new Date(1, 6, 1187)));
        theMap.put("BattleofRenfrew", new Period(new Date(1, 6, 1164), new Date(1, 6, 1164)));
        theMap.put("BattleofSlioch", new Period(new Date(1, 12, 1307), new Date(1, 12, 1307)));
        theMap.put("BattleofStanhopePark", new Period(new Date(3, 8, 1327), new Date(4, 8, 1327)));
        theMap.put("BattleofTeba", new Period(new Date(15, 8, 1330), new Date(15, 8, 1330)));
        theMap.put("SiegeofStralsund(1628)", new Period(new Date(15, 5, 1628), new Date(4, 8, 1628)));
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
