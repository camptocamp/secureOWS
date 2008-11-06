/**
 * 
 */
package com.camptocamp.owsproxy;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import owsproxyclient.CertificateWarningDialog;
import owsproxyclient.ExamineCertPanel.AddCert;

public class UIConnectionManager extends ConnectionManager {

    @Override
    public AddCert certificateValidationFailure(boolean readonlyKeystore, String errorMessage, String certificateInformation) {
        CertificateWarningDialog warningDialog = new CertificateWarningDialog("localhost", errorMessage, certificateInformation, new JFrame(), true);
        if( readonlyKeystore ) warningDialog.disablePermanentOption();
        warningDialog.setVisible(true);
        AddCert howToHandle = warningDialog.addCertificateSelected();
        return howToHandle;
    }
    
    @Override
    public void keystoreMissing(final File keystore) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Object[] options = { "Yes", "No" };
                    int result = JOptionPane.showOptionDialog(new JFrame(),
                            "The defined keystore: "+keystore+" does not exist\nDo you want to create it?",
                            "Missing Keystore", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                    if (result == 1) {
                        throw new RuntimeException("cancel chosen");
                    }
                }
            });
        } catch (Exception e) {
            super.keystoreMissing(keystore);
        }
    }
}
