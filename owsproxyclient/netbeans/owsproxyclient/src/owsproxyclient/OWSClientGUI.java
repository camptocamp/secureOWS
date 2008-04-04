/*
 * OWSClientGUI.java
 *
 * Created on November 1, 2006, 3:53 PM
 */

package owsproxyclient;

import java.util.Locale;

/**
 *
 * @author  sypasche
 */
public class OWSClientGUI extends javax.swing.JFrame {
    
    /** Creates new form OWSClientGUI */
    public OWSClientGUI() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usernameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        validationLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        urlPanel = new javax.swing.JPanel();
        proxyUrlLabel = new javax.swing.JLabel();
        proxyURL = new javax.swing.JTextField();
        copyClipboardButton = new javax.swing.JButton();
        statusLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorDetail = new javax.swing.JTextArea();
        serviceURL = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("owsproxyclient/translations"); // NOI18N
        setTitle(bundle.getString("Secure_WMS_Client")); // NOI18N

        jLabel3.setText(bundle.getString("Password")); // NOI18N

        connectButton.setText(bundle.getString("Connect")); // NOI18N

        disconnectButton.setText(bundle.getString("Disconnect")); // NOI18N

        jLabel8.setText(bundle.getString("Connection_status:")); // NOI18N

        jLabel4.setText(bundle.getString("Enter_the_information_in_the_fields_below,_and_then_click_Connect.")); // NOI18N

        validationLabel.setForeground(java.awt.Color.red);
        validationLabel.setText("dummy1");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 24));
        jLabel6.setText(bundle.getString("Secure_WMS_Client")); // NOI18N

        jLabel7.setText(bundle.getString("A_connection_URL_will_be_displayed,_which_you_can_use_in_your_WMS_client.")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statusLabel.setFont(new java.awt.Font("Dialog", 1, 18));
        statusLabel.setText("DummyStatusText");

        proxyUrlLabel.setText(bundle.getString("You_can_use_the_following_URL_in_your_WMS_client:")); // NOI18N

        proxyURL.setEditable(false);

        copyClipboardButton.setText(bundle.getString("Copy_to_Clipboard")); // NOI18N

        org.jdesktop.layout.GroupLayout urlPanelLayout = new org.jdesktop.layout.GroupLayout(urlPanel);
        urlPanel.setLayout(urlPanelLayout);
        urlPanelLayout.setHorizontalGroup(
            urlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(urlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(urlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(proxyUrlLabel)
                    .add(proxyURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 413, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(copyClipboardButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 252, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(195, Short.MAX_VALUE))
        );
        urlPanelLayout.setVerticalGroup(
            urlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(urlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyUrlLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyClipboardButton)
                .addContainerGap())
        );

        statusLabel2.setFont(new java.awt.Font("Dialog", 0, 9));
        statusLabel2.setText("status");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statusLabel2)
                            .add(urlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(41, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                        .add(63, 63, 63))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(statusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(urlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        errorDetail.setColumns(20);
        errorDetail.setRows(5);
        jScrollPane1.setViewportView(errorDetail);

        jLabel1.setText(bundle.getString("Service_URL")); // NOI18N

        jLabel9.setText(bundle.getString("User_name")); // NOI18N

        proxyButton.setText("Proxy...");
        proxyButton.setActionCommand("Proxy");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel4)
                                .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel3)
                                        .add(jLabel1)
                                        .add(jLabel9))
                                    .add(39, 39, 39)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(serviceURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                                        .add(validationLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                                        .add(usernameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                                        .add(passwordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)))
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel6))
                            .addContainerGap())
                        .add(jLabel7))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(connectButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(disconnectButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(proxyButton)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(validationLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serviceURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(proxyButton)
                    .add(disconnectButton)
                    .add(connectButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton connectButton;
    public javax.swing.JButton copyClipboardButton;
    public javax.swing.JButton disconnectButton;
    public javax.swing.JTextArea errorDetail;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JPasswordField passwordField;
    public final javax.swing.JButton proxyButton = new javax.swing.JButton();
    public javax.swing.JTextField proxyURL;
    public javax.swing.JLabel proxyUrlLabel;
    public javax.swing.JTextField serviceURL;
    public javax.swing.JLabel statusLabel;
    public javax.swing.JLabel statusLabel2;
    public javax.swing.JPanel urlPanel;
    public javax.swing.JTextField usernameField;
    public javax.swing.JLabel validationLabel;
    // End of variables declaration//GEN-END:variables
    
}
