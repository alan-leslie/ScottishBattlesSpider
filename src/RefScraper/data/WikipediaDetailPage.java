package RefScraper.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Model of wikipedia detail page (contains details of period position).
 * @author al
 */
public class WikipediaDetailPage {

    private final URL theURL;
    private final Document theDocument;
    private final Logger theLogger;
    private NodeList theSummary = null;
    private Node theFirstPara = null;
    private static String theBaseURL = "http://en.wikipedia.org";

    /**
     * Constructs model of wikipedia detailpage.
     * @param newURL 
     * @param logger
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException  
     */
    public WikipediaDetailPage(URL newURL,
            Logger logger) throws IOException, ParserConfigurationException, SAXException {
        theURL = newURL;
        theLogger = logger;
        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        theDocument = theParser.getParsedPage(theURL);
    }

    /**
     * Finds the period from the page.
     * @return -valid period or null if unobtainable
     */
    public static String getBaseURL() {
        return theBaseURL;
    }

    /**
     * 
     * @return -valid URL of this page.
     */
    public URL getURL() {
        return theURL;
    }

    /**
     * Finds the position from the page.
     * @return -valid position or null if unobtainable
     */
    public Position getPosition() {
        Position thePosition = getPageCoords();

        if (thePosition == null) {
            if (theSummary == null) {
                theSummary = getSummary();
            }

            if (theSummary != null) {
                thePosition = getLocationFromSummary(theSummary);
            }
        }

        return thePosition;
    }

    /**
     * Finds the period from the page.
     * @return -valid period or null if unobtainable
     */
    public Period getPeriod() {
        Period thePeriod = null;

        if (theSummary == null) {
            theSummary = getSummary();
        }

        if (theSummary != null) {
            thePeriod = getDateFromSummary(theSummary);
        }

        if (thePeriod == null) {
            if (theFirstPara == null) {
                theFirstPara = getFirstPara();

                if (theFirstPara != null) {
                    thePeriod = getDateFromFirstPara(theFirstPara);
                }
            }
        }

        return thePeriod;
    }

    /*
     * Get the summary data from the page
     * @return - node list representing the summary section
     *
     */
    private NodeList getSummary() {
        NodeList retVal = null;

        try {
            XPath infoboxTableXpath = XPathFactory.newInstance().newXPath();
            NodeList theData = (NodeList) infoboxTableXpath.evaluate("html//table[@class='infobox vevent']/tr", theDocument, XPathConstants.NODESET);
            int theLength = theData.getLength();

            for (int i = 0; i < theLength && retVal == null; ++i) {
                XPath summaryXpath = XPathFactory.newInstance().newXPath();
                NodeList theSummaryHeader = (NodeList) summaryXpath.evaluate("./th[@class='summary']", theData.item(i), XPathConstants.NODESET);

                if (theSummaryHeader != null
                        && theSummaryHeader.getLength() > 0) {
                    String theText = theSummaryHeader.item(0).getTextContent();
                    retVal = theData;
                }

            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    /*
     * Get the first paragraph (usually an abstract of the page).
     * @return node representing the first para
     *
     */
    private Node getFirstPara() {
        Node retVal = null;

        try {
            XPath firstParaXpath = XPathFactory.newInstance().newXPath();
            NodeList theData = (NodeList) firstParaXpath.evaluate("html/body//div[@id='bodyContent']/p", theDocument, XPathConstants.NODESET);
            int listLength = theData.getLength();

            for (int i = 0; i < listLength && retVal == null; ++i) {
                XPath coordsXpath = XPathFactory.newInstance().newXPath();
                Node theCoords = (Node) coordsXpath.evaluate(".//span[@id='coordinates']", theData.item(i), XPathConstants.NODE);

                if (theCoords == null) {
                    retVal = theData.item(i);
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    /*
     * Get the position from the coordinates field on the page
     * @return - valid position or null if unobtainable
     *
     */
    private Position getPageCoords() {
        Position thePosition = null;

        try {
            XPath latitudeXpath = XPathFactory.newInstance().newXPath();
            Node theLatitudeNode = (Node) latitudeXpath.evaluate("html/body//span[@class='latitude']", theDocument, XPathConstants.NODE);


            XPath longitudeXpath = XPathFactory.newInstance().newXPath();
            Node theLongitudeNode = (Node) longitudeXpath.evaluate("html/body//span[@class='longitude']", theDocument, XPathConstants.NODE);

            if (theLongitudeNode != null && theLatitudeNode != null) {
                thePosition = new Position(theLatitudeNode.getTextContent(), theLongitudeNode.getTextContent());
            }

        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return thePosition;
    }

    /**
     * Try and get the position from the summary of the page
     * @param summaryData 
     * @return - valid position or null if not obtainable
     */
    private Position getLocationFromSummary(NodeList summaryData) {
        Node theValueNode = getValueNodeFromSummary(theSummary, "Location");

//        int theLength = summaryData.getLength();
//        boolean locationFound = false;
        Position summaryPosition = null;

        try {
            if (theValueNode != null) {
                XPath anchorsXpath = XPathFactory.newInstance().newXPath();
                NodeList theAnchors = (NodeList) anchorsXpath.evaluate("./span/a", theValueNode, XPathConstants.NODESET);

                if (theAnchors != null) {
                    int theAnchorsLength = theAnchors.getLength();

                    if (theAnchorsLength > 0) {
                        Element thePlaceElement = (Element) theAnchors.item(0);
                        String thePlaceHREF = thePlaceElement.getAttribute("href");
                        if (thePlaceHREF.indexOf("http://") != 0) {
                            thePlaceHREF = WikipediaDetailPage.getBaseURL() + thePlaceHREF;
                        }

                        try {
                            URL theLocationRef = new URL(thePlaceHREF);
                            summaryPosition = getLocationFromRef(theLocationRef);
                        } catch (MalformedURLException ex) {
                            theLogger.log(Level.SEVERE, "Unable to format place URL", ex);
                        }
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return summaryPosition;
    }

    /**
     * Try and get the latitude and longitude from the location reference URL
     * @param locationRef 
     * @return - valid position or null if unobtainable
     */
    private Position getLocationFromRef(URL locationRef) {
        Position refPosition = null;

        try {
            WikipediaDetailPage thePage = new WikipediaDetailPage(locationRef, theLogger);
            refPosition = thePage.getPageCoords();
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Cannot get location page", e);
        }

        return refPosition;
    }

    /**
     * Try and get the period from page summary
     * @param summaryData 
     * @return - valid period or null if not obtainable
     */
    private Period getDateFromSummary(NodeList summaryData) {
        Period summaryPeriod = null;
        Node theValueNode = getValueNodeFromSummary(theSummary, "Date");

        if (theValueNode != null) {
            String detailText = theValueNode.getTextContent();
            summaryPeriod = Period.getRealPeriod(detailText);

            if (summaryPeriod == null) {
                Date theDate = Period.getDate(detailText);

                if (theDate != null) {
                    summaryPeriod = new Period(theDate, theDate);
                } else {
                    theLogger.log(Level.WARNING, "Cannot get date");
                }
            }
        }

        return summaryPeriod;
    }

    /**
     * Try and get the period from the first para of the page (usually an 
     * abstract of the full page)
     * @param theFirstPara 
     * @return - valid period or null if not obtainable
     */
    private Period getDateFromFirstPara(Node theFirstPara) {
        String testString = theFirstPara.getTextContent();
        Date testDate = Period.extractDateFromText(testString);

        if (testDate != null) {
            return new Period(testDate, testDate);
        } else {
            return null;
        }
    }

    /**
     * get the result from the summary
     * @return - the result or "?" if not found
     */
    String getResult() {
        String retVal = "?";

        if (theSummary == null) {
            theSummary = getSummary();
        }

        if (theSummary != null) {
            Node theValueNode = getValueNodeFromSummary(theSummary, "Result");
            if (theValueNode != null) {
                retVal = theValueNode.getTextContent();
            }
        }

        return retVal;
    }

    /**
     * Try and get the node that contains the value for the summary data item
     * with title dataName.
     * The summary is a table so looking for a tr node that has a th of dataName
     * @param summaryData 
     * @param dataName 
     * @return - valid Node or null if not found
     */
    private Node getValueNodeFromSummary(NodeList summaryData,
            String dataName) {
        Node dataValueNode = null;

        try {
            int theLength = summaryData.getLength();
            boolean dataFound = false;

            for (int i = 0; i < theLength && !dataFound; ++i) {
                XPath rowXpath = XPathFactory.newInstance().newXPath();
                NodeList theRows = (NodeList) rowXpath.evaluate("./td/table/tr", summaryData.item(i), XPathConstants.NODESET);

                if (theRows != null) {
                    int theRowsLength = theRows.getLength();

                    for (int j = 0; j < theRowsLength; ++j) {
                        Node theRowNode = theRows.item(j);

                        XPath headerXpath = XPathFactory.newInstance().newXPath();
                        Node theHeaderNode = (Node) rowXpath.evaluate("./th", theRowNode, XPathConstants.NODE);

                        String theText = theHeaderNode.getTextContent();

                        if (theText != null
                                && theText.equalsIgnoreCase(dataName)) {
                            XPath detailXpath = XPathFactory.newInstance().newXPath();
                            Node theDetail = (Node) headerXpath.evaluate("./td", theRowNode, XPathConstants.NODE);

                            if (theDetail != null) {
                                dataValueNode = theDetail;
                            }

                            dataFound = true;
                        }
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
        }

        return dataValueNode;
    }
    
        static char asciiFromUTF(int codePoint) {
        char retVal = ' ';
        switch (codePoint) {
            case 8211:
                retVal = '-';
                break;
            case 232:
                retVal = 'e';
                break;
        }

        return retVal;
    }

    public static String getAsciiText(String theText) {
        StringBuilder theBuilder = new StringBuilder();
        int lengthInChars = theText.length();
        int noOfCodePoints = theText.codePointCount(0, lengthInChars - 1);

        try {
            if (lengthInChars > 0
                    && lengthInChars > noOfCodePoints) {
                for (int offset = 0; offset < lengthInChars;) {
                    final int codePoint = theText.codePointAt(offset);
                    char theCharAt = theText.charAt(offset);

                    if (codePoint >= 0 && codePoint < 128) {
                        theBuilder.append(theCharAt);
                    } else {
                        theBuilder.append(WikipediaDetailPage.asciiFromUTF(codePoint));
                    }

                    offset += Character.charCount(codePoint);
                }
            } else {
                theBuilder.append(theText);
            }
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("out of bounds");
        }

        return theBuilder.toString();
    }
}
