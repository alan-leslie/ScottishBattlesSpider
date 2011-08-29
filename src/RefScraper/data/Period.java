package RefScraper.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Models a period (start - end dates)
 * @author al
 * todo - ensure this is immutable
 */
public class Period {

    private Date theStartDate;
    private Date theEndDate;

    /**
     * 
     * @param startDate 
     * @param endDate
     */
    public Period(Date startDate,
            Date endDate) {
        theStartDate = startDate;
        theEndDate = endDate;
    }

    /**
     * 
     * @return - whether the period data is wholly complete
     */
    public boolean isComplete() {
        boolean startComplete = (theStartDate != null && !theStartDate.toString().isEmpty());
        boolean endComplete = (theEndDate != null && !theEndDate.toString().isEmpty());

        return (startComplete && endComplete);
    }

    /**
     * @return - the start date
     */
    Date getStartDate() {
        return (Date) (theStartDate.clone());
    }

    /**
     * @return - the end date
     */
    Date getEndDate() {
        return (Date) (theEndDate.clone());
    }

    /**
     * @return - whether this period has a real duration, that is that the end
     * date is after the start
     */
    boolean hasDuration() {
        if (theEndDate != null
                && theEndDate.after(getStartDate())) {
            return true;
        }

        return false;
    }

    /**
     * @param dateString - the string that denotes the date info
     * @return - a Period object or null if the start and end are the same
     * or unobtainable
     * todo - may need to look for the patterns to and until rather than -
     */
    public static Period getRealPeriod(String dateString) {
        Pattern p = Pattern.compile("[^A-Za-z_0-9 ]+");
        String[] theParts = p.split(dateString);

        if (theParts.length < 2) {
            theParts = dateString.split("-");
        }

        boolean hasRealPeriod = theParts.length > 1;
        Period realPeriod = null;

        if (hasRealPeriod) {
            DateFormat theDayMonthFormat = new SimpleDateFormat("dd MMMM");
            theDayMonthFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            DateFormat theFullDateFormat = new SimpleDateFormat("dd MMMM yyyy");
            theFullDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            DateFormat theYearFormat = new SimpleDateFormat("yyyy");
            theYearFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                Date theStartDate = theFullDateFormat.parse(theParts[0]);
                Date theEndDate = theFullDateFormat.parse(theParts[1]);
                realPeriod = new Period(theStartDate, theEndDate);
            } catch (ParseException ex) {
                // parse failure means that the period is not a real period
                // handled by leaving the return value as null
            }

            if (realPeriod == null) {

                try {
                    Date theStartDate = theDayMonthFormat.parse(theParts[0]);
                    Date theEndDate = theFullDateFormat.parse(theParts[1]);
                    Calendar theStartDateCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                    theStartDateCal.setTime(theStartDate);
                    Calendar theEndDateCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                    theEndDateCal.setTime(theEndDate);
                    theStartDateCal.set(Calendar.YEAR, theEndDateCal.get(Calendar.YEAR));
                    realPeriod = new Period(theStartDateCal.getTime(), theEndDateCal.getTime());
                } catch (ParseException ex) {
                    // parse failure means that the period is not a real period
                    // handled by leaving the return value as null
                }
            }



            if (realPeriod == null) {
                try {
                    Date theStartDate = theYearFormat.parse(theParts[0]);
                    Date theEndDate = theYearFormat.parse(theParts[1]);
                    realPeriod = new Period(theStartDate, theEndDate);
                } catch (ParseException ex) {
                    // parse failure means that the period is not a real period
                    // handled by leaving the return value as null
                }

            }
        }


        return realPeriod;
    }

    /**
     * @param dateString - the string that denotes the date info
     * @return - a date or null if the date format is not found
     */
    // todo - formatters list should be set up once (if needed) only
    // deal with confusion between dd/mm/yyyy and mm/dd/yyyy
    // note that formatters need to be in order of most specific first
    public static Date getDate(String dateString) {
        Date retVal = null;
        List<DateFormat> theFormatters = new ArrayList<DateFormat>();
        SimpleDateFormat theDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);
        theDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);
        theDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);
        theDateFormat = new SimpleDateFormat("MMMM, yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);
        theDateFormat = new SimpleDateFormat("MMMM yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);
        theDateFormat = new SimpleDateFormat("yyyy");
        theDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        theFormatters.add(theDateFormat);

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

    /**
     * @param paragraphText - string that includes a date in the format e.g. 
     * 13 August 1970
     * @return - a date or null if the date format is not found
     */
    // todo cut this into as few patterns as possible
    public static Date extractDateFromText(String paragraphText) {
        Date retVal = null;
        List<Pattern> theDatePatterns = new ArrayList<Pattern>();
        theDatePatterns.add(Pattern.compile("\\d+ January \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ February \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ March \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ April \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ May \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ June \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ July \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ August \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ September \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ October \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ November \\d+"));
        theDatePatterns.add(Pattern.compile("\\d+ December \\d+"));

        for (int i = 0; i < theDatePatterns.size() && retVal == null; ++i) {
            Matcher theMatcher = theDatePatterns.get(i).matcher(paragraphText);
            boolean matchFound = theMatcher.find();

            if (matchFound) {
                String matchingString = theMatcher.group();
                String theParts[] = matchingString.split(" ");
                int theDay = Integer.parseInt(theParts[0]);
                int theYear = Integer.parseInt(theParts[2]);

                if (!(theDay < 1 || theDay > 31 || theYear < 1 || theYear > 5000)) {
                    retVal = Period.getDate(matchingString);
                }
            }
        }

        return retVal;
    }
}
