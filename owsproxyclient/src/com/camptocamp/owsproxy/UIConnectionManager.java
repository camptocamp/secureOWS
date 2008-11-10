/**
 * 
 */
package com.camptocamp.owsproxy;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import owsproxyclient.CertificateWarningDialog;
import owsproxyclient.ExamineCertPanel.AddCert;

public class UIConnectionManager extends ConnectionManager {

    @Override
    public AddCert certificateValidationFailure(boolean readonlyKeystore, String errorMessage, String certificateInformation) {
        CertificateWarningDialog warningDialog = new CertificateWarningDialog("localhost", errorMessage, certificateInformation, new JFrame(), true); //$NON-NLS-1$
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
                    String msg = MessageFormat.format(Translations.getString("UIConnectionManager.warningMsg"), keystore); //$NON-NLS-1$
                    Object[] options = { Translations.getString("UIConnectionManager.yes"), Translations.getString("UIConnectionManager.no") }; //$NON-NLS-1$ //$NON-NLS-2$
                    int result = JOptionPane.showOptionDialog(new JFrame(),
                            msg,
                            Translations.getString("UIConnectionManager.missingKeystore"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, //$NON-NLS-1$
                            null, options, options[0]);
                    if (result == 1) {
                        throw new RuntimeException("cancel chosen"); //$NON-NLS-1$
                    }
                }
            });
        } catch (Exception e) {
            super.keystoreMissing(keystore);
        }
    }
}
