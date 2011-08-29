package RefScraper.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author al
 */
public class PeriodTest {

    /**
     * 
     */
    @Test
    public void testPeriodYears() {
        Period thePeriod = Period.getRealPeriod("1981-2001");

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(1981, startDate.get(Calendar.YEAR));
        assertEquals(2001, endDate.get(Calendar.YEAR));
    }
    
    /**
     * 
     */
    @Test
    public void testPeriodMonths() {
        Period thePeriod = Period.getRealPeriod("1 July-2 August 2001");
        
        assertEquals(false, thePeriod == null);

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(2001, startDate.get(Calendar.YEAR));
        assertEquals(2001, endDate.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, startDate.get(Calendar.MONTH));
        assertEquals(Calendar.AUGUST, endDate.get(Calendar.MONTH));
        assertEquals(1, startDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(2, endDate.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * 
     */
    @Test
    public void testPeriodFulDates() {
        Period thePeriod = Period.getRealPeriod("1 July 1981-2 August 2001");
        
        assertEquals(false, thePeriod == null);

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("Europe/Edinburgh"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(1981, startDate.get(Calendar.YEAR));
        assertEquals(2001, endDate.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, startDate.get(Calendar.MONTH));
        assertEquals(Calendar.AUGUST, endDate.get(Calendar.MONTH));
        assertEquals(1, startDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(2, endDate.get(Calendar.DAY_OF_MONTH));
    }
}
