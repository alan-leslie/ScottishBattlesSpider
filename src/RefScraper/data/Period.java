/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.util.Date;

/**
 *
 * @author al
 */
public class Period {
    Date theStartDate;
    Date theEndDate;
    
    Period(Date startDate, 
            Date endDate){
        theStartDate = startDate;
        theEndDate = endDate;
    }

    Date getStartDate() {
        return theStartDate;
    }

    Date getEndDate() {
        return theEndDate;
    } 
}
