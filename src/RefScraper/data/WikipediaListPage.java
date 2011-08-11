package RefScraper.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Model of wikipedia page that contains lists of items to be processed
 * @author al
 */
public class WikipediaListPage {

    private final URL theURL;
    private final Document theDocument;
    private final Logger theLogger;
    private NodeList theSummary = null;
    private Node theFirstPara = null;
    private static String theBaseURL = "http://en.wikipedia.org";

    /**
     * Constructs model of wikipedia list page.
     * @param newURL 
     * @param logger  
     */
    public WikipediaListPage(URL newURL,
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
}
