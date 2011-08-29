package RefScraper.data;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to hold information for a HREF, a position and a duration.
 * @author al
 */
public class RefThree implements Comparable {

    String theName;
    private Position thePosition;
    private URL theLocationRef;
    private URL thePlace;
    private Period thePeriod;
    private URL theURL;
    private String theHREF;
    private final Logger theLogger;

    /**
     * 
     * @param theTitle 
     * @param theHREF 
     * @param logger 
     */
    public RefThree(String theTitle,
            String theHREF,
            Logger logger) {
        this.theHREF = theHREF;
        String thePageHREF = theHREF;

        try {
            theURL = new URL(thePageHREF);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RefThree.class.getName()).log(Level.SEVERE, null, ex);
        }
        theName = theTitle;
        theLogger = logger;
    }

    /**
     * a copy constructor to make sure that the object is not shared in threads
     * @param theOther - the original to be copied
     */
    public RefThree(RefThree theOther) {
        theName = theOther.theName;
        if (theOther.thePosition == null) {
            thePosition = null;
        } else {
            thePosition = new Position(theOther.thePosition.getLatitude(), theOther.thePosition.getLongitude());
        }
        theLocationRef = theOther.theLocationRef;
        thePlace = theOther.thePlace;
        if (theOther.thePeriod == null) {
            thePeriod = null;
        } else {
            thePeriod = new Period(theOther.thePeriod.getStartDate(), theOther.thePeriod.getEndDate());
        }
        theURL = theOther.theURL;
        theLogger = theOther.theLogger;
    }

    /**
     * Output the placemark data in different xml formats.
     * @param ps - the stream to where the data is written
     * @param asKML - whether the output format is for google maps (KML) or 
     * timeline (XML)
     */
    public void outputAsXML(PrintStream ps,
            boolean asKML) {

        if (asKML) {
            ps.print("<Placemark>");
            ps.println();
            ps.print("<name>");
            ps.println();
            ps.print(theName);
            ps.println();
            ps.print("</name>");
            ps.println();
            ps.print("<description>");
            ps.println();

            if (theURL != null) {
                ps.print("&lt;p&gt;");
                ps.println();
                ps.print("&lt;a href=\"");
                ps.print(theURL);
                ps.print("\"&gt; more info&gt;&gt;&gt;");
                ps.print("&lt;/a&gt;");
                ps.println();
                ps.print("&lt;/p&gt;");
                ps.println();
            }

            ps.print("</description>");
            ps.println();

            if (thePeriod.hasDuration()) {
                ps.print("<TimeSpan>");
                ps.print("<begin>");
                ps.print(thePeriod.getStartDate().toString());
                ps.print("</begin>");
                ps.print("<end>");
                ps.print(thePeriod.getEndDate().toString());
                ps.print("</end>");
                ps.print("</TimeSpan>");
                ps.println();
            } else {
                ps.print("<TimeStamp>");
                ps.print("<when>");
                ps.print(thePeriod.getStartDate().toString());
                ps.print("</when>");
                ps.print("</TimeStamp>");
                ps.println();
            }

            ps.print("<Point>");
            ps.println();
            ps.print("<coordinates>");
            ps.print(thePosition.getLongitudeDD());
            ps.print(",");
            ps.print(thePosition.getLatitudeDD());
            ps.print("</coordinates>");
            ps.println();
            ps.print("</Point>");
            ps.println();
            ps.print("</Placemark>");
            ps.println();
        } else {
            ps.print("<event ");
            ps.print("start=\"");
            ps.print(thePeriod.getStartDate().toString());
            ps.print("\" ");
            if (thePeriod.hasDuration()) {
                ps.print("end=\"");
                ps.print(thePeriod.getEndDate().toString());
                ps.print("\" ");
            }

            ps.print("title=\"");
            ps.print(theName);
            ps.print("\">");
            ps.println();
            ps.print("</event>");
            ps.println();
        }
    }

    /**
     * 
     * @return - a unique id for the placemark
     */
    public String getId() {
        String strId = theName.replaceAll(" ", "");
        return strId;
    }

    /**
     * @return - the href of this placemark
     */
    public String getHREF() {
        return theHREF;
    }

    /**
     * attempt to fill in all of the placemark data
     * @return - whether all of the required data has been completed
     */
    public boolean complete() {
        WikipediaDetailPage thePage = new WikipediaDetailPage(theURL, theLogger);

        thePosition = thePage.getPosition();
        thePeriod = thePage.getPeriod();

        // try to recover if data is only partially set
        if ((isPeriodSet() || isPositionSet()) && !(isPeriodSet() && isPositionSet())) {
            if (!isPeriodSet()) {
                thePeriod = PeriodMap.getInstance().getPeriod(getId());
            }

            if (!isPositionSet()) {
                thePosition = PositionMap.getInstance().getPosition(getId());
            }
        }

        if (isComplete()) {
            return true;
        } else {
            theLogger.log(Level.WARNING, "Unable to complete {0}", getId());
            return false;
        }
    }

    /**
     * @return - whether all of the position data has been set
     */
    private boolean isPositionSet() {
        return (thePosition != null && thePosition.isComplete());
    }

    /**
     * @return - whether all of the period data has been set
     */
    private boolean isPeriodSet() {
        return (thePeriod != null && thePeriod.isComplete());
    }

    /**
     * @return - whether all of the required data has been set
     */
    private boolean isComplete() {
        boolean retVal = isPositionSet() && isPeriodSet();
        return retVal;
    }

    public int compareTo(Object anotherPlacemark) throws ClassCastException {
        if (!(anotherPlacemark instanceof RefThree)) {
            throw new ClassCastException("A RefThree object expected.");
        }
        String anotherPlacemarkName = ((RefThree) anotherPlacemark).getId();
        return this.getId().compareTo(anotherPlacemarkName);
    }
}
