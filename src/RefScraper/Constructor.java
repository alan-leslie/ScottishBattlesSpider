package RefScraper;

import RefScraper.data.RefThree;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

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

        if (index < target.size()) {
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
            for (int i = 0; i < targetArr.length; ++i) {
                String theTarget = targetArr[i];
                if (!theTarget.isEmpty()) {
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

        for (int i = 0; i < target.size(); ++i) {
            theLogger.log(Level.INFO, "Constructing fom page {0}", target.get(i));
            Document theDoc = theParser.getParsedPage(target.get(i));
            WikipediaPage thePage = new WikipediaPage(theDoc, theLogger);
            processFile(thePage, linksAdded);
            theLogger.log(Level.INFO, "Constructing fom page {0} - complete", target.get(i));
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
    private boolean processFile(WikipediaPage thePage,
            List<String> linksAdded) {
        List<HTMLLink> theCandidates = thePage.getCandidates();
        int linksLength = theCandidates.size();

        for (int i = 0; i < linksLength; ++i) {
            HTMLLink theRef = theCandidates.get(i);

            if (!linksAdded.contains(theRef.getHREF())) {
                RefThree theWorkloadItem = new RefThree(theRef.getText(), theRef.getHREF(), theLogger);
                theLogger.log(Level.INFO, "Construction worker - processing link : {0}", theRef.getText());

                owner.addWorkload(theWorkloadItem);
                linksAdded.add(theRef.getHREF());
            }

            if (owner.isHalted()) {
                return false;
            }
        }

        return true;
    }
}
