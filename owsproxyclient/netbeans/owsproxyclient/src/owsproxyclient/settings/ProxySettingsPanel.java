/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProxySettingsPanel.java
 *
 * Created on Nov 4, 2008, 5:40:36 PM
 */

package owsproxyclient.settings;

/**
 * Panel for configuring proxy settings
 * 
 * @author jeichar
 */
public class ProxySettingsPanel extends javax.swing.JPanel implements SettingsPanel {
    private SettingsDialog _owner;

    /** Creates new form ProxySettingsPanel */
    public ProxySettingsPanel(SettingsDialog owner) {
        _owner = owner;

    }

    public void init(){
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        useAuthentication = new javax.swing.JCheckBox();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("owsproxyclient/translations"); // NOI18N
        jLabel1.setText(bundle.getString("URL")); // NOI18N

        jLabel2.setText(bundle.getString("Port")); // NOI18N

        port.setText("3128");
        port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portActionPerformed(evt);
            }
        });
        port.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                portKeyReleased(evt);
            }
        });

        url.setText("http://");
        url.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                urlActionPerformed(evt);
            }
        });

        useAuthentication.setText(bundle.getString("Use_Authentication")); // NOI18N
        useAuthentication.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useAuthenticationStateChanged(evt);
            }
        });

        usernameLabel.setText(bundle.getString("User_name")); // NOI18N
        usernameLabel.setEnabled(false);

        passwordLabel.setText(bundle.getString("Password")); // NOI18N
        passwordLabel.setEnabled(false);

        password.setEnabled(false);

        username.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel3.setText("Proxy Settings");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(port, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                            .add(url, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)))
                    .add(useAuthentication)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(usernameLabel)
                            .add(passwordLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(password)
                            .add(username, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)))
                    .add(jLabel3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(url, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(useAuthentication)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(usernameLabel)
                    .add(username, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passwordLabel)
                    .add(password, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void portKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_portKeyReleased
        _owner.updateButtons();
}//GEN-LAST:event_portKeyReleased

    private String validatePassword() {
        if(useAuthentication.isSelected() && username.getText().trim().length()==0){
            return "No proxy username";
        }
        return null;
    }

    private String validatePort() {
        try {
            final String text = port.getText().trim();
            if( text.length()>0 ){
                Integer.parseInt(text);
            }
            return null;
        } catch (NumberFormatException exception) {
            return java.util.ResourceBundle.getBundle("owsproxyclient/translations").getString("portError");
        }
    }
        
    private void useAuthenticationStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useAuthenticationStateChanged
        final boolean enabled = useAuthentication.isSelected();
        username.setEnabled(enabled);
        usernameLabel.setEnabled(enabled);
        password.setEnabled(enabled);
        passwordLabel.setEnabled(enabled);
    }//GEN-LAST:event_useAuthenticationStateChanged

    private void urlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlActionPerformed
        _owner.updateButtons();
    }//GEN-LAST:event_urlActionPerformed

    private void portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    public final javax.swing.JPasswordField password = new javax.swing.JPasswordField();
    private javax.swing.JLabel passwordLabel;
    public final javax.swing.JTextField port = new javax.swing.JTextField();
    public final javax.swing.JTextField url = new javax.swing.JTextField();
    public javax.swing.JCheckBox useAuthentication;
    public final javax.swing.JTextField username = new javax.swing.JTextField();
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables

    public Object getState() {
        return new ProxyState( url.getText(), port.getText(), useAuthentication.isSelected(), username.getText(), password.getPassword());
    }

    public void setState(Object state) {
        ProxyState pState = (ProxyState) state;
        url.setText(pState.url);
        port.setText(pState.port);
        useAuthentication.setSelected(pState.useAuthentication);
        if( pState.useAuthentication){
            username.setText(pState.username);
            password.setText(new String(pState.password));
        }
    }


    public String getErrorMessage() {
        String portMsg = validatePort();
        if(portMsg!=null){
            return portMsg;
        }
        String pwdMsg = validatePassword();
        if(pwdMsg!=null){
            return pwdMsg;
        }
        return null;
    }


}
