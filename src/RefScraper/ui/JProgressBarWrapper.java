/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.ui;

import javax.swing.JProgressBar;

/**
 *
 * @author al
 */
public class JProgressBarWrapper  implements ScrapeProgressDisplay {
    private final JProgressBar theProgressBar;

    JProgressBarWrapper(JProgressBar newProgressBar){
        theProgressBar = newProgressBar;
    }

    public String getText() {
        return "not implemented";
    }
    
    public void setText(String theText) {
        // not implemented
    }

    public void setProgress(int i) {
        Integer thePercentage = new Integer(i);
        theProgressBar.setValue(thePercentage);
    }
    
}
