package RefScraper.data;

import java.util.Calendar;
import java.util.Date;
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
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
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
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
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
    public void testPeriodFullDates() {
        Period thePeriod = Period.getRealPeriod("1 July 1981-2 August 2001");
        
        assertEquals(false, thePeriod == null);

        assertEquals(true, thePeriod.hasDuration());
        Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        startDate.setTime(thePeriod.getStartDate());
        Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        endDate.setTime(thePeriod.getEndDate());
        assertEquals(1981, startDate.get(Calendar.YEAR));
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
    public void testPeriodGetDates() {
        Date theDate = Period.getDate("1 July 1981");
        Date theMonthFirst = Period.getDate("November 12, 1984");
        Date theDMY = Period.getDate("9/1/1984");
//        Date theMDY = Period.getDate("1984");
        Date theMCommaY = Period.getDate("January, 1984");
        Date theMY = Period.getDate("February 1984");
        Date theYear = Period.getDate("1984");
        
        assertEquals(false, theDate == null);
        assertEquals(false, theMonthFirst == null);
        assertEquals(false, theDMY == null);
        assertEquals(false, theMCommaY == null);
        assertEquals(false, theMY == null);
        assertEquals(false, theYear == null);

        Calendar firstDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        firstDate.setTime(theDate);
        Calendar monthFirstDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        monthFirstDate.setTime(theMonthFirst);
        Calendar dMYDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        dMYDate.setTime(theDMY);
        Calendar MCommaYDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        MCommaYDate.setTime(theMCommaY);
        Calendar MYDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        MYDate.setTime(theMY);
        Calendar yearDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        yearDate.setTime(theYear);
 
        assertEquals(1981, firstDate.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, firstDate.get(Calendar.MONTH));
        assertEquals(1, firstDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1984, monthFirstDate.get(Calendar.YEAR));
        assertEquals(Calendar.NOVEMBER, monthFirstDate.get(Calendar.MONTH));
        assertEquals(12, monthFirstDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1984, monthFirstDate.get(Calendar.YEAR));
        assertEquals(Calendar.NOVEMBER, monthFirstDate.get(Calendar.MONTH));
        assertEquals(12, monthFirstDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1984, dMYDate.get(Calendar.YEAR));
        assertEquals(Calendar.SEPTEMBER, dMYDate.get(Calendar.MONTH));
        assertEquals(1, dMYDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(1984, MCommaYDate.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, MCommaYDate.get(Calendar.MONTH));
        assertEquals(1984, MYDate.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, MYDate.get(Calendar.MONTH));
        assertEquals(1984, yearDate.get(Calendar.YEAR));
   }
    
        /**
     * 
     */
    @Test
    public void testPeriodExtractDates() {
        Date theFirstDate = Period.extractDateFromText("1 July 1981");
        Date theSecondDate = Period.extractDateFromText("gubbins to fill 21 July 1001 this out");
        Date theInvalidDate = Period.extractDateFromText("32 July 1001");
             
        assertEquals(false, theFirstDate == null);
        assertEquals(false, theSecondDate == null);
        assertEquals(true, theInvalidDate == null);

        Calendar firstDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        firstDate.setTime(theFirstDate);
        Calendar secondDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        secondDate.setTime(theSecondDate);
        assertEquals(1981, firstDate.get(Calendar.YEAR));
        assertEquals(1001, secondDate.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, firstDate.get(Calendar.MONTH));
        assertEquals(Calendar.JULY, secondDate.get(Calendar.MONTH));
        assertEquals(1, firstDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(21, secondDate.get(Calendar.DAY_OF_MONTH));
    }
}
