/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper.ui;

/**
 *
 * @author al
 */
public interface ScrapeProgressDisplay {
    public void setText(String theText);
    public String getText();
    public void setProgress(int i);
}
