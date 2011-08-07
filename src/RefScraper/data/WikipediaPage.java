/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.data;

import RefScraper.data.HTMLPageParser;
import RefScraper.data.Period;
import RefScraper.data.Position;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
 * Model of wikipedia page
 * @author al
 */
public class WikipediaPage {

    private final URL theURL;
    private final Document theDocument;
    private final Logger theLogger;
    private NodeList theSummary = null;
    private Node theFirstPara = null;
    private static String theBaseURL = "http://en.wikipedia.org";

    /**
     * Constructs model of wikipedia page.
     * @param newURL 
     * @param logger  
     */
    public WikipediaPage(URL newURL,
            Logger logger) {
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
     * Finds the period from the page.
     * @return -valid period or null if unobtainable
     */
    public URL getURL() {
        return theURL;
    }

    /**
     * Finds the period from the page.
     * @return -valid period or null if unobtainable
     */
    public Position getPosition() {
        Position thePosition = getPageCoords();

        if (thePosition == null) {
            if (theSummary == null) {
                theSummary = getSummary();
            }

            thePosition = getLocationFromSummary(theSummary);
        }

        return thePosition;
    }

    /**
     * Finds the period from the page.
     * @return -valid period or null if unobtainable
     */
    public Period getPeriod() {
        Period thePeriod = null;

        if (theFirstPara == null) {
            theFirstPara = getFirstPara();

            thePeriod = getDateFromFirstPara(theFirstPara);

            if (thePeriod == null) {
                if (theSummary == null) {
                    theSummary = getSummary();
                }

                thePeriod = getDateFromSummary(theSummary);
            }
        }

        return thePeriod;
    }

    /**
     * Finds candidate links by looking various sections of the page.
     * @return - the candidate links
     */
    public List<HTMLLink> getCandidates() {
        List<HTMLLink> theCandidates = new ArrayList<HTMLLink>();
        getMainTableCandidates(theCandidates);
        getMainListCandidates(theCandidates);
        getSubTableCandidates(theCandidates);

        return theCandidates;
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
     * @return node representation of first para
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

    /**
     * Finds candidate links by looking up links titled with 'Name' in page 
     * sub tables.
     * @param theCandidates - the links to populate
     */
    private void getSubTableCandidates(List<HTMLLink> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//table[@class='wikitable']/tr";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, theDocument, XPathConstants.NODESET);

            int listLength = linkNodeList.getLength();
            int nameIndex = 0;

            for (int i = 0; i < listLength; ++i) {
                if (i == 0) {
                    String headerSearchString = "./th";
                    XPath headerXpath = XPathFactory.newInstance().newXPath();
                    NodeList headerNodeList = (NodeList) headerXpath.evaluate(headerSearchString, linkNodeList.item(i), XPathConstants.NODESET);
                    int headerLength = headerNodeList.getLength();

                    for (int j = 0; j < headerNodeList.getLength(); ++j) {
                        String headerText = headerNodeList.item(j).getTextContent();

                        if (headerText.equalsIgnoreCase("Name")) {
                            nameIndex = j;
                        }
                    }
                } else {
                    String detailSearchString = "./td";
                    XPath detailXpath = XPathFactory.newInstance().newXPath();
                    NodeList detailNodeList = (NodeList) detailXpath.evaluate(detailSearchString, linkNodeList.item(i), XPathConstants.NODESET);
                    int detailLength = detailNodeList.getLength();

                    if (detailNodeList.getLength() > nameIndex) {
                        Node childNode = (Node) detailNodeList.item(nameIndex);
                        String anchorSearchString = "./a";
                        XPath anchorXpath = XPathFactory.newInstance().newXPath();
                        Node anchorNode = (Node) anchorXpath.evaluate(anchorSearchString, childNode, XPathConstants.NODE);
                        short nodeType = childNode.getNodeType();

                        Element theElement = (Element) anchorNode;
                        String theTitle = theElement.getAttribute("title");
                        String theHREF = theElement.getAttribute("href");
                        String theText = theElement.getTextContent();

                        if (!theTitle.isEmpty()
                                && !theTitle.contains("Special:")
                                && !theTitle.contains("Wikipedia:")) {
                            theLogger.log(Level.INFO, "Foudn candidate :{0}", theText);
                            HTMLLink theCandidate = new HTMLLink(theText, theHREF);
                            theCandidates.add(theCandidate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Exception on XPath: ", e);
        }
    }

    /**
     * Finds candidate links by looking up top level table links in the page
     * @param theCandidates - the links to populate
     */
    private void getMainTableCandidates(List<HTMLLink> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//div[@id='mw-pages']/table//ul/li/a";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, theDocument, XPathConstants.NODESET);

            int listLength = linkNodeList.getLength();

            for (int i = 0; i < listLength; ++i) {
                Node childNode = (Node) linkNodeList.item(i);
                short nodeType = childNode.getNodeType();

                Element theElement = (Element) childNode;
                String theTitle = theElement.getAttribute("title");
                String theHREF = theElement.getAttribute("href");
                String theText = theElement.getTextContent();

                if (!theTitle.isEmpty()
                        && !theTitle.contains("Special:")
                        && !theTitle.contains("Wikipedia:")) {
                    theLogger.log(Level.INFO, "Foudn candidate :{0}", theText);
                    HTMLLink theCandidate = new HTMLLink(theText, theHREF);
                    theCandidates.add(theCandidate);
                }
            }
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Exception on XPath: ", e);
        }
    }

    /**
     * Finds candidate links by looking up top level links in the page
     * @param theCandidates - the links to populate
     */
    private void getMainListCandidates(List<HTMLLink> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//div[@id='bodyContent']/ul/li/a";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, theDocument, XPathConstants.NODESET);

            int listLength = linkNodeList.getLength();

            for (int i = 0; i < listLength; ++i) {
                Node childNode = (Node) linkNodeList.item(i);
                short nodeType = childNode.getNodeType();

                Element theElement = (Element) childNode;
                String theTitle = theElement.getAttribute("title");
                String theHREF = theElement.getAttribute("href");
                String theText = theElement.getTextContent();

                if (!theTitle.isEmpty()
                        && !theTitle.contains("Special:")
                        && !theTitle.contains("Wikipedia:")) {
                    theLogger.log(Level.INFO, "Foudn candidate :{0}", theText);
                    HTMLLink theCandidate = new HTMLLink(theText, theHREF);
                    theCandidates.add(theCandidate);
                }
            }
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Exception on XPath: ", e);
        }
    }

    /*
     * Get the position from the coordinates field on the top left of the page
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
     * @param tsummaryData 
     * @return - valid position or null if not obtainable
     */
    private Position getLocationFromSummary(NodeList summaryData) {
        int theLength = summaryData.getLength();
        boolean locationFound = false;
        Position summaryPosition = null;

        try {
            for (int i = 0; i < theLength && !locationFound; ++i) {
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
                                && theText.equalsIgnoreCase("Location")) {
                            XPath detailXpath = XPathFactory.newInstance().newXPath();
                            Node theDetail = (Node) headerXpath.evaluate("./td", theRowNode, XPathConstants.NODE);

                            if (theDetail != null) {
                                XPath anchorsXpath = XPathFactory.newInstance().newXPath();
                                NodeList theAnchors = (NodeList) headerXpath.evaluate("./span/a", theDetail, XPathConstants.NODESET);

                                if (theAnchors != null) {
                                    int theAnchorsLength = theAnchors.getLength();

                                    if (theAnchorsLength > 0) {
                                        Element thePlaceElement = (Element) theAnchors.item(0);
                                        String thePlaceHREF = thePlaceElement.getAttribute("href");
                                        if (thePlaceHREF.indexOf("http://") != 0) {
                                            thePlaceHREF = WikipediaPage.getBaseURL() + thePlaceHREF;
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

                            locationFound = true;
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
        WikipediaPage thePage = new WikipediaPage(locationRef, theLogger);
        Position refPosition = thePage.getPageCoords();
        return refPosition;
    }

    /**
     * Try and get the period from page summary
     * @param summaryData 
     * @return - valid period or null if not obtainable
     */
    private Period getDateFromSummary(NodeList summaryData) {
        Period summaryPeriod = null;

        try {
            int theLength = summaryData.getLength();
            boolean dateFound = false;

            for (int i = 0; i < theLength && !dateFound; ++i) {
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
                                && theText.equalsIgnoreCase("Date")) {
                            XPath detailXpath = XPathFactory.newInstance().newXPath();
                            Node theDetail = (Node) headerXpath.evaluate("./td", theRowNode, XPathConstants.NODE);

                            if (theDetail != null) {
                                String detailText = theDetail.getTextContent();

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

                            dateFound = true;
                        }
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            theLogger.log(Level.SEVERE, null, ex);
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
}
