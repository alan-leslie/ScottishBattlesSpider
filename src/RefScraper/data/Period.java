/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author al
 */
public class Period {
    private Date theStartDate;
    private Date theEndDate;
    
    public Period(Date startDate, 
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
         
    public static Period getRealPeriod(String dateString) {
        Pattern thePattern = Pattern.compile("\\d\\d\\d\\d-\\d\\d\\d\\d");
        Matcher theMatcher = thePattern.matcher(dateString);
        boolean hasRealPeriod = theMatcher.matches();
        Period realPeriod = null;

        if (hasRealPeriod) {
            Scanner s = new Scanner(dateString).useDelimiter("-");
            Integer theStartYear = new Integer(s.nextInt());
            Integer theEndYear = new Integer(s.nextInt());

            DateFormat theYearFormat = new SimpleDateFormat("yyyy");

            try {
                Date theStartDate = theYearFormat.parse(theStartYear.toString());
                Date theEndDate = theYearFormat.parse(theEndYear.toString());
                realPeriod = new Period(theStartDate, theEndDate);
            } catch (ParseException ex) {
                // parse failure means that the period is not a real period
                // handled by leaving the return value as null
            }
        }

        return realPeriod;
    }
    
        
        // todo - formatters list should be set up once (if needed) only
    // deal with confusion between dd/mm/yyyy and mm/dd/yyyy
    public static Date getDate(String dateString) {
        Date retVal = null;
        List<DateFormat> theFormatters = new ArrayList<DateFormat>();
        theFormatters.add(new SimpleDateFormat("dd MMMM yyyy"));
        theFormatters.add(new SimpleDateFormat("MMMM dd, yyyy"));
        theFormatters.add(new SimpleDateFormat("MMMM, yyyy"));
        theFormatters.add(new SimpleDateFormat("MMMM yyyy"));
        theFormatters.add(new SimpleDateFormat("yyyy"));
        theFormatters.add(new SimpleDateFormat("dd/MM/yyyy"));
        theFormatters.add(new SimpleDateFormat("MM/dd/yyyy"));

        for (int i = 0; i < theFormatters.size() && retVal == null; ++i) {
            try {
                Date theDate = theFormatters.get(i).parse(dateString);
                retVal = theDate;
            } catch (ParseException ex) {
                // not really an exceptional case
            }
        }

        return retVal;
    }
}
