/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owsproxyclient.settings;

/**
 * A common interface for all panels in the settings dialog
 * @author jeichar
 */
public interface SettingsPanel {

    /**
     * Creates and lays out components of panel
     */
    void init();
    /**
     * Sets the default state for the panel.  This is called after init
     * 
     * @param state
     */
    void setState(Object state);
    /**
     * Return an object that encapsulates the state of the panel
     *
     * @return an object that encapsulates the state of the panel
     */
    Object getState();
    /**
     * Returns null if the panel is in a state where the changes can be accepted
     * (ok can be pressed) or an error message
     *
     * @return null if ok can be pressed or an error message
     */
    String getErrorMessage();
}
