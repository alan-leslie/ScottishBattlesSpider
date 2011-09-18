package RefScraper.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author al
 */
public class RefThreeTest {

    Logger theLogger = makeLogger();

    /**
     * 
     */
    @Test
    public void testRefThreeNamur() {
        String theTitle = "Siege of Namur";
        String theHREF = "http://en.wikipedia.org/wiki/Siege_of_Namur_(1695)";

        RefThree theTestRef = new RefThree(theTitle, theHREF, theLogger);
        boolean completed = theTestRef.complete();
        
        assertEquals(true, completed);
        
        Period thePeriod = theTestRef.getPeriod();

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(1695, startDate.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, startDate.get(Calendar.MONTH));
        assertEquals(2, startDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1695, endDate.get(Calendar.YEAR));
        assertEquals(Calendar.SEPTEMBER, endDate.get(Calendar.MONTH));
        assertEquals(1, endDate.get(Calendar.DAY_OF_MONTH));
        
        Position thePosition = theTestRef.getPosition();
        String theLat = thePosition.getLatitudeDD();
        String theLong = thePosition.getLongitudeDD();
        float theLatAsFloat = Float.parseFloat(theLat);
        float theLongAsFloat = Float.parseFloat(theLong);
        assert(theLatAsFloat > 50.0 && theLatAsFloat < 51.0);
        assert(theLongAsFloat > 4.0 && theLongAsFloat < 5.0);
        
        String theResult = theTestRef.getResult();
        assert(theResult.equalsIgnoreCase("Allied victory"));
        
        String dateString = theTestRef.getDateString().trim();
        assert(dateString.equalsIgnoreCase("02 July-01 September 1695"));
    }
    
    /**
     * 
     */
    @Test
    public void testRefThreeAncrum() {
        String theTitle = "Battle of Ancrum Moor";
        String theHREF = "http://en.wikipedia.org/wiki/Battle_of_Ancrum_Moor";

        RefThree theTestRef = new RefThree(theTitle, theHREF, theLogger);
        boolean completed = theTestRef.complete();
        
        assertEquals(true, completed);
        
        Period thePeriod = theTestRef.getPeriod();

        assertEquals(false, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        assertEquals(1545, startDate.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, startDate.get(Calendar.MONTH));
        assertEquals(27, startDate.get(Calendar.DAY_OF_MONTH));
       
        Position thePosition = theTestRef.getPosition();
        String theLat = thePosition.getLatitudeDD();
        String theLong = thePosition.getLongitudeDD();
        float theLatAsFloat = Float.parseFloat(theLat);
        float theLongAsFloat = Float.parseFloat(theLong);
        assert(theLatAsFloat > 55.0 && theLatAsFloat < 56.0);
        assert(theLongAsFloat > -3.0 && theLongAsFloat < -2.0);
        
        String theResult = theTestRef.getResult();
        assert(theResult.equalsIgnoreCase("Decisive Scottish Victory"));
        
        String dateString = theTestRef.getDateString().trim();
        assert(dateString.equalsIgnoreCase("27 February 1545"));
    }
 
    /**
     * 
     */
    @Test
    public void testRefThreeStanhope() {
        String theTitle = "Battle of Stanhope Park";
        String theHREF = "http://en.wikipedia.org/wiki/Battle_of_Stanhope_Park";

        RefThree theTestRef = new RefThree(theTitle, theHREF, theLogger);
        boolean completed = theTestRef.complete();
        
        assertEquals(true, completed);
        
        Period thePeriod = theTestRef.getPeriod();

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(1327, startDate.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, startDate.get(Calendar.MONTH));
        assertEquals(3, startDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1327, endDate.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, endDate.get(Calendar.MONTH));
        assertEquals(4, endDate.get(Calendar.DAY_OF_MONTH));      
       
        Position thePosition = theTestRef.getPosition();
        String theLat = thePosition.getLatitudeDD();
        String theLong = thePosition.getLongitudeDD();
        float theLatAsFloat = Float.parseFloat(theLat);
        float theLongAsFloat = Float.parseFloat(theLong);
        assert(theLatAsFloat > 54.0 && theLatAsFloat < 55.0);
        assert(theLongAsFloat > -3.0 && theLongAsFloat < -2.0);
        
        String theResult = theTestRef.getResult();
        assert(theResult.equalsIgnoreCase("Scottish victory"));
        
        String dateString = theTestRef.getDateString().trim();
        assert(dateString.equalsIgnoreCase("03-04 August 1327")); 
    }
    
    /**
     * 
     */
    // unable to complete because cannot parse page
    // todo - maybe convert to nekohtml for parsing
//    @Test
//    public void testRefThreeLanark() {
//        String theTitle = "Action at Lanark";
//        String theHREF = "http://en.wikipedia.org/wiki/Action_at_Lanark";
//
//        RefThree theTestRef = new RefThree(theTitle, theHREF, theLogger);
//        boolean completed = theTestRef.complete();
//        
//        assertEquals(true, completed);
//        
//        Period thePeriod = theTestRef.getPeriod();
//
//        assertEquals(false, thePeriod.hasDuration());
//        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
//        startDate.setTime(thePeriod.getStartDate());
//        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
//        endDate.setTime(thePeriod.getEndDate());
//        assertEquals(1297, startDate.get(Calendar.YEAR));
//        assertEquals(Calendar.MAY, startDate.get(Calendar.MONTH));
//        assertEquals(1, startDate.get(Calendar.DAY_OF_MONTH));
//        assertEquals(1297, endDate.get(Calendar.YEAR));
//        assertEquals(Calendar.MAY, endDate.get(Calendar.MONTH));
//        assertEquals(1, endDate.get(Calendar.DAY_OF_MONTH));      
//       
//        Position thePosition = theTestRef.getPosition();
//        String theLat = thePosition.getLatitudeDD();
//        String theLong = thePosition.getLongitudeDD();
//        float theLatAsFloat = Float.parseFloat(theLat);
//        float theLongAsFloat = Float.parseFloat(theLong);
//        assert(theLatAsFloat > 55.0 && theLatAsFloat < 56.0);
//        assert(theLongAsFloat > -4.0 && theLongAsFloat < -3.0);
//        
//        String theResult = theTestRef.getResult();
//        assert(theResult.equalsIgnoreCase("Scottish victory"));
//        
//        String dateString = theTestRef.getDateString().trim();
//        assert(dateString.equalsIgnoreCase("01 May 1297")); 
//    }
    
    /**
     *
     * @return - valid logger (single file).
     */
    private static Logger makeLogger() {
        Logger lgr = Logger.getLogger("RefThreeTest");
        lgr.setUseParentHandlers(false);
        lgr.addHandler(simpleFileHandler());
        return lgr;
    }

    /**
     *
     * @return - valid file handler for logger.
     */
    private static FileHandler simpleFileHandler() {
        try {
            FileHandler hdlr = new FileHandler("RefThreeTest.log");
            hdlr.setFormatter(new SimpleFormatter());
            return hdlr;
        } catch (Exception e) {
            System.out.println("Failed to create log file");
            return null;
        }
    }
}
