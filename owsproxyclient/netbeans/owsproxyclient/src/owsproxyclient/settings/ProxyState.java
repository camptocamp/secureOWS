/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owsproxyclient.settings;

import java.util.ResourceBundle;

/**
 * Encapsulates the state of the ProxySettingsPanel class
 *
 * @author jeichar
 */
public class ProxyState {
    private static final ResourceBundle translations = ResourceBundle.getBundle("owsproxyclient/translations");
    public final char[] password;
    public final String url;
    public final String port;
    public final boolean useAuthentication;
    public final String username;

    public ProxyState(String url, String port, boolean useAuthentication, String username, char[] password) {
        this.url=url.trim();
        this.port=port.trim();
        this.useAuthentication=useAuthentication;
        this.username=username.trim();
        this.password=password;
    }
    
}
