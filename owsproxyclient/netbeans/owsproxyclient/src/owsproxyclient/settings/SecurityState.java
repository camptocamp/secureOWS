/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owsproxyclient.settings;

import java.util.ResourceBundle;

/**
 * Encapsulates the security settings
 * @author jeichar
 */
public class SecurityState {
    private static final ResourceBundle translations = ResourceBundle.getBundle("owsproxyclient/translations");
    public final String keystore;
    public final char[] password;
    public final boolean readonly;

    public SecurityState(String keystore, char[] password, boolean readonly) {
        this.keystore = keystore;
        this.password = password;
        this.readonly = readonly;
    }
   
}
