package RefScraper.data;

import RefScraper.HTMLPageParser;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to hold information for placing a castle
 * @author al
 */
public class RefThree implements Comparable {

    String theName;
    private String theLatitude;
    private String theLongitude;
    private URL theLocationRef;
    private URL thePlace;
    private String theStartDate;
    private String theEndDate;
    private URL theURL;
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
        try {
            theURL = new URL(theHREF);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RefThree.class.getName()).log(Level.SEVERE, null, ex);
        }
        theName = theTitle;
        theLogger = logger;
    }

    /**
     * a copy constructor so that the object is not shred in threads
     * @param theOther 
     */
    public RefThree(RefThree theOther) {
        theName = theOther.theName;
        theLatitude = theOther.theLatitude;
        theLongitude = theOther.theLongitude;
        theLocationRef = theOther.theLocationRef;
        thePlace = theOther.thePlace;
        theStartDate = theOther.theStartDate;
        theEndDate = theOther.theEndDate;
        theURL = theOther.theURL;
        theLogger = theOther.theLogger;
    }

    /**
     * 
     * @param ps - the stream to where the data is written
     */
    public void outputAsKML(PrintStream ps) {
        ps.print("<event>");
        ps.println();
        ps.print("<name>");
        ps.print(theName);
        ps.print("</name>");
        ps.println();
        ps.print("<description>");
        ps.println();
        ps.print("&lt;p&gt;Condition: ");
        // todo ps.print(theCondition);
        ps.print("&lt;/p&gt;");
        ps.println();
        ps.print("&lt;p&gt;Date: ");
        // todops.print(theDate);
        ps.print("&lt;/p&gt;");
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
        ps.print("<styleUrl>#exampleStyleMap</styleUrl>");
        ps.println();
        ps.print("<Point>");
        ps.println();
        ps.print("<coordinates>");
        ps.print(theLongitude);
        ps.print(",");
        ps.print(theLatitude);
        ps.print("</coordinates>");
        ps.println();
        ps.print("</Point>");
        ps.println();
        ps.print("</Placemark>");
        ps.println();
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
     * 
     * @return - a unique id for the placemark
     */
    public boolean complete() {
        boolean retVal = false;
        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        Document document = theParser.getParsedPage(theURL);

        populateCoordsFromPage(document);
        NodeList summaryFromPage = getSummaryFromPage(document);

        if (summaryFromPage != null) {
            populateDateFromSummaryData(summaryFromPage);
            populateLocationFromSummaryData(summaryFromPage);
            retVal = true;
        }

        return retVal;
    }

    /*
     * Get the position from the coordinates field on the top left of the page
     *
     */
    private void populateCoordsFromPage(Document document) {
    }

    /*
     * Get the summary data from the page
     *
     */
    private NodeList getSummaryFromPage(Document theDocument) {
        NodeList retVal = null;

        try {
            XPath infoboxTableXpath = XPathFactory.newInstance().newXPath();
            NodeList theData = (NodeList) infoboxTableXpath.evaluate("html//table[@class='infobox vevent']/tr", theDocument, XPathConstants.NODESET);
            int theLength = theData.getLength();

            for (int i = 0; i < theLength && retVal == null; ++i) {
                XPath summaryXpath = XPathFactory.newInstance().newXPath();
                NodeList theSummary = (NodeList) summaryXpath.evaluate("./th[@class='summary']", theData.item(i), XPathConstants.NODESET);

                if (theSummary != null
                        && theSummary.getLength() > 0) {
                    String theText = theSummary.item(0).getTextContent();
                    retVal = theData;
                }

            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    /*
     * Get the summary from the top left of the page
     *
     */
    private void populateDateFromSummaryData(NodeList summaryData) {
        try {
            int theLength = summaryData.getLength();
            boolean dateFound = false;

            for (int i = 0; i < theLength && !dateFound; ++i) {
                XPath headerXpath = XPathFactory.newInstance().newXPath();
                Node theHeader = (Node) headerXpath.evaluate("./td/table/tr/th", summaryData.item(i), XPathConstants.NODE);

                if (theHeader != null) {
                    String theText = theHeader.getTextContent();

                    if (theText != null
                            && theText.equalsIgnoreCase("Date")) {
                        XPath detailXpath = XPathFactory.newInstance().newXPath();
                        Node theDetail = (Node) headerXpath.evaluate("./td/table/tr/td", summaryData.item(i), XPathConstants.NODE);

                        if (theDetail != null) {
                            String detailText = theDetail.getTextContent();
                            DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                            try {
                                Date theDate = (Date) formatter.parse(detailText);
                                boolean dateParsed = true;
                            } catch (ParseException ex) {
                                theLogger.log(Level.FINEST, "Cannot get date", ex);
                            }
                        }

                        dateFound = true;
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }
    }

    /*
     * populate the position from the summary data
     * either use the summary ref or the summary place
     *
     */
    private void populateLocationFromSummaryData(NodeList summaryData) {
        int theLength = summaryData.getLength();
        boolean locationFound = false;

        try {
            for (int i = 0; i < theLength && !locationFound; ++i) {
                XPath headerXpath = XPathFactory.newInstance().newXPath();
                Node theHeader = (Node) headerXpath.evaluate("./td/table/tr/th", summaryData.item(i), XPathConstants.NODE);

                if (theHeader != null) {
                    String theText = theHeader.getTextContent();

                    if (theText != null
                            && theText.equalsIgnoreCase("Location")) {
                        XPath detailXpath = XPathFactory.newInstance().newXPath();
                        Node theDetail = (Node) headerXpath.evaluate("./td/table/tr/td", summaryData.item(i), XPathConstants.NODE);

                        if (theDetail != null) {
                        }

                        locationFound = true;
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Get the position from the lt long given in the summary or
     * use the place location to get it
     *
     */
    private void populateLocationFromSummary() {
    }

    /*
     * @precon - theURL is valid
     * @postcon - lat long set 
     * @return
     */
    private boolean populateLatLongFromURL() {
        if (theURL == null) {
            return false;
        }

        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        Document document = theParser.getParsedPage(theURL);

        try {
            XPath lonXpath = XPathFactory.newInstance().newXPath();
            NodeList lonNodeList = (NodeList) lonXpath.evaluate("html//span[@class='geo']", document, XPathConstants.NODESET);
            int lonLength = lonNodeList.getLength();
            if (lonLength > 0) {
                Element element = (Element) lonNodeList.item(0);
                String[] strArr = element.getTextContent().split(";");

                if (strArr.length > 1) {
                    theLatitude = strArr[0];
                    theLongitude = strArr[1];
                }
            }
        } catch (XPathExpressionException theException) {
            theLogger.log(Level.SEVERE, "Placemark populateLatLongFromURL:" + getId(), theException);
        }

        if (theLatitude == null
                || theLongitude == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Try and get the latitude and longitude from either the location or
     * the underlying URL
     * @return
     */
    public boolean populateLatLong() {
        if (theLocationRef == null) {
            return populateLatLongFromURL();
        }

        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        Document document = theParser.getParsedPage(theLocationRef);

        try {
            XPath lonXpath = XPathFactory.newInstance().newXPath();
            NodeList lonNodeList = (NodeList) lonXpath.evaluate("html//span[@class='longitude']", document, XPathConstants.NODESET);
            int lonLength = lonNodeList.getLength();
            if (lonLength > 0) {
                Element element = (Element) lonNodeList.item(0);
                theLongitude = element.getTextContent();
            }

            XPath latXpath = XPathFactory.newInstance().newXPath();
            NodeList latNodeList = (NodeList) latXpath.evaluate("html//span[@class='latitude']", document, XPathConstants.NODESET);
            int latLength = latNodeList.getLength();
            if (latLength > 0) {
                Element element = (Element) latNodeList.item(0);
                theLatitude = element.getTextContent();
            }
        } catch (XPathExpressionException theException) {
            theLogger.log(Level.SEVERE, "Placemark populateLatLongFromURL:" + getId(), theException);
        }

        if (theLatitude == null
                || theLongitude == null) {
            return false;
        } else {
            return true;
        }
    }

    public int compareTo(Object anotherPlacemark) throws ClassCastException {
        if (!(anotherPlacemark instanceof RefThree)) {
            throw new ClassCastException("A Placemark object expected.");
        }
        String anotherPlacemarkName = ((RefThree) anotherPlacemark).getId();
        return this.getId().compareTo(anotherPlacemarkName);
    }
}
