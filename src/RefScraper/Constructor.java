package RefScraper;

import RefScraper.data.HTMLLink;
import RefScraper.data.WikipediaListPage;
import RefScraper.data.RefThree;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Constructor class is responsible for constructing the workloads.
 * So it connects to the initial page and looks for links of interest.
 * @author al
 */
public class Constructor implements Callable<String> {

    /**
     * the URL that this worker uses to get data
     */
    private List<URL> target;
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
     * 
     * @param index 
     * @return - the URL \t the required index
     */
    public String getTarget(int index) {
        String retVal = "";

        if (index < target.size()) {
            target.get(index).toString();
        }

        return retVal;
    }

    /**
     * @return - "Complete"
     */
    public String call() {
        List<String> linksAdded = new ArrayList<String>();

        for (int i = 0; i < target.size(); ++i) {
            try {
                theLogger.log(Level.INFO, "Constructing fom page {0}", target.get(i));
                WikipediaListPage thePage = new WikipediaListPage(target.get(i), theLogger);
                processFile(thePage, linksAdded);
                theLogger.log(Level.INFO, "Constructing fom page {0} - complete", target.get(i));
            } catch (Exception e) {
                theLogger.log(Level.SEVERE, "Constructing fom page {0} - failed", target.get(i));
            }
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
    private boolean processFile(WikipediaListPage thePage,
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
