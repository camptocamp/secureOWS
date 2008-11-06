/*
 * Main.java
 *
 * Created on November 1, 2006, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package owsproxyclient;
import java.util.Locale;

/**
 *
 * @author sypasche
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Locale.setDefault(Locale.FRENCH);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                final OWSClientGUI oWSClientGUI = new OWSClientGUI();
                oWSClientGUI.setVisible(true);
            }
        });
        
    }
    
}
