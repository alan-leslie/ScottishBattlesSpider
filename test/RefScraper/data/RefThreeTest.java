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
    public void testRefThree() {
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
    }

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
