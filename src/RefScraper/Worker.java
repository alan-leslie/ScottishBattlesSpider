package RefScraper;

import RefScraper.data.RefThree;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Worker class performs the actual work of
 * looking at sub pages to find locations.  
 *
 * @author al
 */
public class Worker implements Callable<RefThree> {

   /**
     * The controller which drives this worker.
     */
    protected final Controller owner;
    
    /**
     * The data used to find the location.
     */
    protected final RefThree _placemark;
    
    private final Logger theLogger;

    /**
     * Constructs a worker object.
     *
     * @param owner The owner of this object.
     * @param thePlacemark
     * @param logger  
     */
    public Worker(Controller owner,
            RefThree thePlacemark,
            Logger logger) {
        this.owner = owner;
        this._placemark = thePlacemark;
        theLogger = logger;
    }

    /**
     * The call method - copy placemark to result to ensure original is not
     * stuck in other threads.
     * @return - a fully populated placemark if successful otherwise null
     */
    public RefThree call() {
        theLogger.log(Level.FINEST, "LocationWorker call - Completing: {0}", _placemark.getId());
        RefThree theResult = new RefThree(_placemark);
        boolean isError = !(theResult.complete());
       
        if (isError) {
            theLogger.log(Level.INFO, "LocationWorker call unsuccessful");
            return null;
        } else {
            theLogger.log(Level.INFO, "LocationWorker call successful");
            return theResult;
        }
    }
    
}