/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper;

import java.util.ArrayList;
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
}
