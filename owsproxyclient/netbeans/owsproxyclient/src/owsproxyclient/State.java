/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owsproxyclient;

/**
 * Encapsulates the state of the ProxySettingsDialog class
 * @author jeichar
 */
public class State {
    public final char[] password;
    public final String url;
    public final String port;
    public final boolean useAuthentication;
    public final String username;

    public State(String url, String port, boolean useAuthentication, String username, char[] password) {
        this.url=url;
        this.port=port;
        this.useAuthentication=useAuthentication;
        this.username=username;
        this.password=password;
    }
    
}
