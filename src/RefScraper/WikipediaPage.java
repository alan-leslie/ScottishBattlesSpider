/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper;

import RefScraper.data.Period;
import RefScraper.data.Position;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author al
 */
public class WikipediaPage {

    private final Document theDocument;
    private final Logger theLogger;
    private static String theBaseURL = "http://en.wikipedia.org";
   
    public WikipediaPage(Document document,
            Logger logger){
        theDocument = document;     
        theLogger = logger;
    }
    
    public static String getBaseURL() {
        return theBaseURL;
    }
    
   /*
     * Get the summary data from the page
     * todo go straight to the summary and then go to parent
     *
     */
    public NodeList getSummary() {
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
     * Get the summary data from the page
     * todo go straight to the summary and the go to parent
     *
     */
    public Node getFirstPara() {
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
    
    public List<HTMLLink> getCandidates() {
        List<HTMLLink> theCandidates = new ArrayList<HTMLLink>();
        getMainTableCandidates(theCandidates);
        getMainListCandidates(theCandidates);
        getSubTableCandidates(theCandidates);

        return theCandidates;
    }

    /**
     * Finds the region names by looking up the table class (wikitable_sortable).
     * @param document - valid parsed html document
     * @return - the list of tables as a node list 
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
     * Finds the region names by looking up the table class (wikitable_sortable).
     * @param document - valid parsed html document
     * @return - the list of tables as a node list 
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
     * Finds the region names by looking up the table class (wikitable_sortable).
     * @param document - valid parsed html document
     * @return - the list of tables as a node list 
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
     *
     */
    public Position getPageCoords() {
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
    
    /*
     * populate the position from the summary data
     * either use the summary ref or the summary place
     *
     */
    public Position getLocationFromSummary(NodeList summaryData) {
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
     * Try and get the latitude and longitude from either the place url
     * @return
     */
    private Position getLocationFromRef(URL locationRef) {
        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        Document document = theParser.getParsedPage(locationRef);
        WikipediaPage thePage = new WikipediaPage(document, theLogger);
        Position refPosition = thePage.getPageCoords();

        return refPosition;
    }
    
        /*
     * Get the summary from the top left of the page
     *
     */
    public Period getDateFromSummary(NodeList summaryData) {
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
    
       /*
     * Get the summary from the top left of the page
     *
     */
    public Period getDateFromFirstPara(Node theFirstPara) {
        String testString = theFirstPara.getTextContent();
        Date testDate = getOccurenceDate(testString);

        if (testDate != null) {
            return new Period(testDate, testDate);
        } else {
            return null;
        }
    }
    
        // todo cut this into as few patterns as possible
    private Date getOccurenceDate(String paragraphText) {
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
