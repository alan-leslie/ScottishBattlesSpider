package RefScraper;

/*
 * 
 *
 */
import RefScraper.ui.RefScraperUI;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author al
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws IOException 
     */
    // TODO -
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        FileInputStream is = null;

        try {
            is = new FileInputStream("RefScraper.properties");
            properties.load(is);
        } catch (IOException e) {
            // ...
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    /* .... */
                }
            }
        }

        String poolSizeStr = properties.getProperty("PoolSize", "2");
        int poolSize = Integer.parseInt(poolSizeStr);
        String theURL = properties.getProperty("StartURL", "http://en.wikipedia.org/wiki/Category:Battles_involving_Scotland");
        String theOutputDir = properties.getProperty("OutputDir", ".");
        String theAsKML = properties.getProperty("AsKML", "true");
        
        boolean asKML = true;
        
        if(!theAsKML.equalsIgnoreCase("true")){
            asKML = false;
        }

        Logger theLogger = Main.makeLogger();
        RefScraperUI theUI = new RefScraperUI();
        Controller theController = new Controller(theURL,
                poolSize, asKML, theLogger);

        theUI.setOutputDir(theOutputDir);
        theUI.setController(theController);
        theController.setManager(theUI);
        theController.setProgressDisplay(theUI.getProgressDisplay());

        theUI.setVisible(true);
    }

    /**
     *
     * @return - valid logger (single file).
     */
    private static Logger makeLogger() {
        Logger lgr = Logger.getLogger("RefScraper");
        lgr.setUseParentHandlers(false);
        lgr.addHandler(simpleFileHandler());
        return lgr;
    }

    /**
     *
     * @return - valid file handler for logger.
     */
    private static FileHandler simpleFileHandler() {
        try {
            FileHandler hdlr = new FileHandler("RefScraper.log");
            hdlr.setFormatter(new SimpleFormatter());
            return hdlr;
        } catch (Exception e) {
            System.out.println("Failed to create log file");
            return null;
        }
    }
}
