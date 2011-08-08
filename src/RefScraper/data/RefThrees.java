package RefScraper.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data class for collection of references.
 * @author al
 */
public class RefThrees {

    private SortedMap<String, RefThree> theRefs = new TreeMap<String, RefThree>();
    private final Logger theLogger;

    /**
     * 
     * @param theLogger
     */
    public RefThrees(Logger theLogger) {
        this.theLogger = theLogger;
    }

    /**
     * 
     * @param refId
     * @param theRef  
     */
    public void addRef(String refId, RefThree theRef) {
        theRefs.put(refId, theRef);
    }

    /**
     * 
     * @param outputDir - the directory where the castles file will be written.
     * @param asKML  - whether output is for google maps (kml) or timeline (xml)
     */
    public void outputAsXML(String outputDir,
            boolean asKML) {
        String strSave = outputDir + "/";
        String targetPath = strSave + "battles.xml";

        if (asKML) {
            targetPath = strSave + "battles.kml";
        }

        FileOutputStream fso = null;

        try {
            fso = new FileOutputStream(new File(targetPath));

            PrintStream ps = new PrintStream(fso);

            if (asKML) {
                ps.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                ps.println();
                ps.print("<kml xmlns=\"http://earth.google.com/kml/2.1\">");
                ps.println();
                ps.print("<Document>");
                ps.println();
            } else {
                ps.print("<data");
                ps.println();
                ps.print("wiki-url=\"http://simile.mit.edu/shelf/\"");
                ps.println();
                ps.print("wiki-section=\"Simile Battles Timeline\"");
                ps.println();
                ps.print(">");
                ps.println();
            }

            Set<Map.Entry<String, RefThree>> refValues = theRefs.entrySet();
            Iterator<Map.Entry<String, RefThree>> refIterator = refValues.iterator();

            String currentFolderName = "";

            while (refIterator.hasNext()) {
                Map.Entry<String, RefThree> anEntry = refIterator.next();
                String name = anEntry.getKey();
                RefThree tmpPlacemark = anEntry.getValue();
                tmpPlacemark.outputAsXML(ps, asKML);
            }

            if (asKML) {
                ps.print("</Document>");
                ps.println();
                ps.print("</kml>");
            } else {
                ps.print("</data>");
            }
            ps.println();
        } catch (Exception e) {
            theLogger.log(Level.SEVERE, "Error: ", e);
        } finally {
            try {
                if (fso != null) {
                    fso.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
