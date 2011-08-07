/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RefScraper;

/**
 *
 * @author al
 */
public class HTMLLink {
    private String theText;
    private String theHREF;
    private static String theBaseURL = "http://en.wikipedia.org";

    
    public HTMLLink(String text, 
            String href)
    {
        theText = text;
        theHREF = href;
        
        if (theHREF.indexOf("http://") != 0) {
            theHREF = theBaseURL + theHREF;
        }
    }
    
    public String getText(){
        return theText;
    }
    
    public String getHREF(){
        return theHREF;
    }   
}
