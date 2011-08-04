package RefScraper;

import RefScraper.data.RefThree;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Constructor class is responsible for constructing the workloads.
 * So it connects to the initial page and looks for links of interest.
 *
 * todo - change so that it tales a list of pages to work with
 * need a list of hrefs processed (added to workload) also
 * @author al
 */
public class Constructor implements Callable<String> {

    /**
     * the URL that this worker uses to get data
     */
    protected URL target;

    /**
     * 
     * @return - the URL that this worker uses to get data
     */
    public String getTarget() {
        return target.toString();
    }
    
    /**
     * The controller which drives this worker.
     */
    protected final Controller owner;
    private final Logger theLogger;

    /**
     * Constructs a worker.
     *
     * @param owner The owner of this object
     * @param theTarget
     * @param logger  
     */
    public Constructor(Controller owner,
            String theTarget,
            Logger logger) {
        this.owner = owner;

        try {
            this.target = new URL(theTarget);
        } catch (MalformedURLException ex) {
            this.target = null;
        }

        theLogger = logger;
    }

    /**
     * @return - "Complete"
     */
    public String call() {
        HTMLPageParser theParser = new HTMLPageParser(theLogger);
        theLogger.log(Level.INFO, "Constructing fom page {0}", target);
        Document theDoc = theParser.getParsedPage(target);
        processFile(theDoc);
        return "Complete";
    }

    /**
     * Processes the html to produce location workers
     * This works for now but it would be more robust if region and table nodes
     * were matched up by looking at relative position of the nodes
     * @param document - valid parsed html document
     * @return  
     */
    private boolean processFile(Document document) {
        NodeList theLinks = getLinks(document);   
        int linksLength = theLinks.getLength();

        for (int i = 0; i < linksLength; ++i) {
            String linkName = "theLink";
            Node childNode = (Node) theLinks.item(i);
            short nodeType = childNode.getNodeType();
            
            Element theElement = (Element)childNode;
            String theTitle = theElement.getAttribute("title");
            String theHREF = theElement.getAttribute("href");
            String theText = theElement.getTextContent();
            
            theLogger.log(Level.INFO, "Construction worker - processinng link : {0}", theTitle);
 
            RefThree theRef = new RefThree(theTitle, theHREF, theLogger);
            
            owner.addWorkload(theRef);

            if (owner.isHalted()) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Finds the region names by looking up the table class (wikitable_sortable).
     * @param document - valid parsed html document
     * @return - the list of tables as a node list 
     */
    private NodeList getLinks(Document document) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//table//ul/li/a";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, document, XPathConstants.NODESET);
            
            int listLength = linkNodeList.getLength();
            
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Exception on XPath: ", e);
        }

        return linkNodeList;
    }


}
