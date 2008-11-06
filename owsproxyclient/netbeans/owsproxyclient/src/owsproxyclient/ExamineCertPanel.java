/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExamineCertPanel.java
 *
 * Created on Nov 4, 2008, 1:09:15 PM
 */
package owsproxyclient;

import java.awt.Component;
import java.util.ResourceBundle;

/**
 *
 * @author jeichar
 */
public class ExamineCertPanel extends javax.swing.JPanel {

    public enum AddCert {

        TEMP, PERM, NEVER
    }
    private static final ResourceBundle translations = ResourceBundle.getBundle("owsproxyclient/translations");
    private CertificateWarningDialog owner;
    private AddCert addCert;

    ExamineCertPanel(CertificateWarningDialog owner) {
        initComponents();
        this.owner = owner;
        addCert = AddCert.NEVER;
    }

    AddCert addCertificateSelected() {
        return addCert;
    }

    void setCertificateDetails(String details) {
        certificateDetails.setText(details);
    }

    void setErrorMessage(String errorMessage) {
        errorMsgText.setText(errorMessage);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        errorMsgText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        certificateDetails = new javax.swing.JTextArea();
        addPermButton = new javax.swing.JButton();
        addTempButton = new javax.swing.JButton();

        errorMsgText.setBackground(new java.awt.Color(255, 255, 255));
        errorMsgText.setEditable(false);
        errorMsgText.setDisabledTextColor(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14));
        jLabel1.setText(translations.getString("errorMsgLabel")); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        certificateDetails.setColumns(20);
        certificateDetails.setEditable(false);
        certificateDetails.setRows(5);
        jScrollPane1.setViewportView(certificateDetails);

        addPermButton.setText(translations.getString("addCertificatePermButton"));
        addPermButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPermButtonActionPerformed(evt);
            }
        });

        addTempButton.setText(translations.getString("addCertificateTempButton"));
        addTempButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTempButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);

        layout.linkSize(new Component[] {addPermButton, addTempButton});
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(addTempButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addPermButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap(237, Short.MAX_VALUE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
            .add(errorMsgText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
        );
        
        
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(errorMsgText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 193, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addTempButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)
                    .add(addPermButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addPermButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPermButtonActionPerformed
        addCert = AddCert.PERM;
        owner.dispose();
}//GEN-LAST:event_addPermButtonActionPerformed

    private void addTempButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTempButtonActionPerformed
        addCert = AddCert.TEMP;
        owner.dispose();
}//GEN-LAST:event_addTempButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton addPermButton;
    private javax.swing.JButton addTempButton;
    private javax.swing.JTextArea certificateDetails;
    private javax.swing.JTextField errorMsgText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}