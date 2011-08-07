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
            Date endDate) {
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

    // todo cut this into as few patterns as possible
    public static Date extractDateFromText(String paragraphText) {
        Date retVal = null;
        List<Pattern> theDatePatterns = new ArrayList<Pattern>();
        theDatePatterns.add(Pattern.compile(" \\d\\d January \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d February \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d March \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d April \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d May \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d June \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d July \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d August \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d September \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d October \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d November \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d\\d December \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d January \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d February \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d March \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d April \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d May \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d June \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d July \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d August \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d September \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d October \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d November \\d\\d\\d\\d"));
        theDatePatterns.add(Pattern.compile(" \\d December \\d\\d\\d\\d"));

        for (int i = 0; i < theDatePatterns.size() && retVal == null; ++i) {
            Matcher theMatcher = theDatePatterns.get(i).matcher(paragraphText);
            boolean matchFound = theMatcher.find();

            if (matchFound) {
                String matchingString = theMatcher.group();
                retVal = Period.getDate(matchingString);
            }
        }

        return retVal;
    }
}
