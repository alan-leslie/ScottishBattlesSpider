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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private List<URL> target;

    /**
     * 
     * @param index 
     * @return - the URL that this worker uses to get data
     */
    public String getTarget(int index) {
        String retVal = "";
        
        if(index < target.size()){
            target.get(index).toString();
        }
        
        return retVal;
    }
    
    /**
     * The controller which drives this worker.
     */
    private final Controller owner;
    private final Logger theLogger;

    /**
     * Constructs a worker.
     *
     * @param owner The owner of this object
     * @param theTargets 
     * @param logger  
     */
    public Constructor(Controller owner,
            String theTargets,
            Logger logger) {
        this.owner = owner;
        target = new ArrayList<URL>();

        try {
            String targetArr[] = theTargets.split(" ");
             for (int i = 0; i < targetArr.length; ++i){
                 String theTarget = targetArr[i];
                 if(!theTarget.isEmpty()){
                target.add(new URL(theTarget));
                 }
             }
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
        List<String> linksAdded = new ArrayList<String>();
        
        for(int i = 0; i < target.size(); ++i){
            theLogger.log(Level.INFO, "Constructing fom page {0}", target.get(i));
            Document theDoc = theParser.getParsedPage(target.get(i));
            processFile(theDoc, linksAdded);
        }
        
        return "Complete";
    }

    /**
     * Processes the html to produce workers
     * This works for now but it would be more robust if region and table nodes
     * were matched up by looking at relative position of the nodes
     * @param document - valid parsed html document
     * @return  
     */
    private boolean processFile(Document document,
            List<String> linksAdded) {
        List<RefThree> theCandidates = getCandidates(document);   
        int linksLength = theCandidates.size();

        for (int i = 0; i < linksLength; ++i) {
            RefThree theRef = theCandidates.get(i);
  
            if(!linksAdded.contains(theRef.getHREF())){
                theLogger.log(Level.INFO, "Construction worker - processing link : {0}", theRef.getId());

                owner.addWorkload(theRef);
                linksAdded.add(theRef.getHREF());
            }

            if (owner.isHalted()) {
                return false;
            }
        }

        return true;
    }

    private List<RefThree> getCandidates(Document document) {
        List<RefThree> theCandidates = new ArrayList<RefThree>();
        getMainTableCandidates(document, theCandidates);
        getMainListCandidates(document, theCandidates);
        getSubTableCandidates(document, theCandidates);
        
        return theCandidates;
    }
     
    /**
     * Finds the region names by looking up the table class (wikitable_sortable).
     * @param document - valid parsed html document
     * @return - the list of tables as a node list 
     */
    private void getSubTableCandidates(Document document,
            List<RefThree> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//table[@class='wikitable']/tr";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, document, XPathConstants.NODESET);
            
            int listLength = linkNodeList.getLength();   
            int nameIndex = 0;
            
            for (int i = 0; i < listLength; ++i) {               
                if(i == 0){
                    String headerSearchString = "./th";
                    XPath headerXpath = XPathFactory.newInstance().newXPath();
                    NodeList headerNodeList = (NodeList) headerXpath.evaluate(headerSearchString, linkNodeList.item(i), XPathConstants.NODESET);  
                    int headerLength = headerNodeList.getLength();
                            
                    for(int j = 0; j < headerNodeList.getLength(); ++j){
                        String headerText = headerNodeList.item(j).getTextContent();
                        
                        if(headerText.equalsIgnoreCase("Name")){
                            nameIndex = j;
                        }
                    }
                } else {
                    String detailSearchString = "./td";
                    XPath detailXpath = XPathFactory.newInstance().newXPath();
                    NodeList detailNodeList = (NodeList) detailXpath.evaluate(detailSearchString, linkNodeList.item(i), XPathConstants.NODESET);                    
                    int detailLength = detailNodeList.getLength();

                    if(detailNodeList.getLength() > nameIndex){
                        Node childNode = (Node) detailNodeList.item(nameIndex);
                        String anchorSearchString = "./a";
                        XPath anchorXpath = XPathFactory.newInstance().newXPath();
                        Node anchorNode = (Node) anchorXpath.evaluate(anchorSearchString, childNode, XPathConstants.NODE);                    
                        short nodeType = childNode.getNodeType();

                        Element theElement = (Element)anchorNode;
                        String theTitle = theElement.getAttribute("title");
                        String theHREF = theElement.getAttribute("href");
                        String theText = theElement.getTextContent();

                        if(!theTitle.isEmpty() && 
                                !theTitle.contains("Special:") &&
                                !theTitle.contains("Wikipedia:")){
                            RefThree theCandidate = new RefThree(theText, theHREF, theLogger);    
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
    private void getMainTableCandidates(Document document,
            List<RefThree> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//table//ul/li/a";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, document, XPathConstants.NODESET);
            
            int listLength = linkNodeList.getLength();   
            
            for (int i = 0; i < listLength; ++i) {
                Node childNode = (Node) linkNodeList.item(i);
                short nodeType = childNode.getNodeType();

                Element theElement = (Element)childNode;
                String theTitle = theElement.getAttribute("title");
                String theHREF = theElement.getAttribute("href");
                String theText = theElement.getTextContent();
                
                if(!theTitle.isEmpty() && 
                        !theTitle.contains("Special:") &&
                        !theTitle.contains("Wikipedia:")){
                    RefThree theCandidate = new RefThree(theText, theHREF, theLogger);    
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
    private void getMainListCandidates(Document document,
            List<RefThree> theCandidates) {
        NodeList linkNodeList = null;

        try {
            String searchString = "/html//div[@id='bodyContent']/ul/li/a";
            XPath linkXpath = XPathFactory.newInstance().newXPath();
            linkNodeList = (NodeList) linkXpath.evaluate(searchString, document, XPathConstants.NODESET);
            
            int listLength = linkNodeList.getLength();   
            
            for (int i = 0; i < listLength; ++i) {
                Node childNode = (Node) linkNodeList.item(i);
                short nodeType = childNode.getNodeType();

                Element theElement = (Element)childNode;
                String theTitle = theElement.getAttribute("title");
                String theHREF = theElement.getAttribute("href");
                String theText = theElement.getTextContent();
                
                if(!theTitle.isEmpty() && 
                        !theTitle.contains("Special:") &&
                        !theTitle.contains("Wikipedia:")){
                    RefThree theCandidate = new RefThree(theText, theHREF, theLogger);    
                    theCandidates.add(theCandidate);
                }
            }
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Exception on XPath: ", e);
        }
    }
}
