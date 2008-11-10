package com.camptocamp.owsproxy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JComponent;

import owsproxyclient.settings.ProxyState;
import owsproxyclient.settings.SecurityState;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;

public class OWSClient implements Observer {
    public static final ProxyState      DEFAULT_PROXY_SETTINGS    = new ProxyState("http://", "3218", false, "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                          new char[0]);
    public static final SecurityState   DEFAULT_SECURITY_SETTINGS = new SecurityState(System.getProperty("user.home") //$NON-NLS-1$
                                                                          + "/.secureows/keystore", "changeit".toCharArray(), false); //$NON-NLS-1$ //$NON-NLS-2$

    ConnectionManager                   connManager;
    private owsproxyclient.OWSClientGUI client;
    Color                               textColor;
    private ConnectionStatus status = ConnectionStatus.IDLE;
    private Collection<X509Certificate> sessionCertificates = new HashSet<X509Certificate>();
    public OWSClient() {

        connManager = new UIConnectionManager();
        connManager.addObserver(this);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, new ClipboardOwner() {
            public void lostOwnership(Clipboard clipboard, Transferable contents) {
                // ignored
            }
        });
    }

    public void update(Observable observable, Object arg) {

        if (!(arg instanceof ConnectionEvent))
            return;
        ConnectionEvent connEvent = (ConnectionEvent) arg;

        OWSLogger.DEV.finer("Got event: " + connEvent); //$NON-NLS-1$

        client.proxyURL.setText(""); //$NON-NLS-1$
        // resets state
        if (textColor == null)
            textColor = client.statusLabel.getForeground();
        client.statusLabel.setForeground(textColor);

        client.statusLabel2.setText(" "); //$NON-NLS-1$

        String msg;

        switch (connEvent.status) {
        case IDLE:
            showConnected(false);
            msg = Translations.getString("OWSProxy_not_connected"); //$NON-NLS-1$

            break;
        case CONNECTING:
            showConnected(true);
            msg = Translations.getString("Connecting"); //$NON-NLS-1$
            break;
        case RUNNING:
            showConnected(true);

            client.statusLabel.setForeground(new Color(0, 128, 0));
            msg = Translations.getString("Connected"); //$NON-NLS-1$
            client.proxyURL.setText(connManager.getListeningAddress());
            break;

        case UNAUTHORIZED:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Unauthorized"); //$NON-NLS-1$
            client.statusLabel2.setText(connEvent.message);
            break;

        case ERROR:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Error"); //$NON-NLS-1$
            client.statusLabel2.setText(connEvent.message);
            break;
        case KEYSTORE_PASSWORD:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Error"); //$NON-NLS-1$
            client.statusLabel2.setText(connEvent.message);
            client.openSettings(1, Translations.getString("OWSClient.wrongPassword")); //$NON-NLS-1$
            break;
        case NO_KEYSTORE:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Error"); //$NON-NLS-1$
            client.statusLabel2.setText(connEvent.message);
            client.openSettings(1, Translations.getString("OWSClient.noKeystore")); //$NON-NLS-1$
            break;

        case PROXY_AUTH_REQUIRED:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Proxy_Auth"); //$NON-NLS-1$
            client.statusLabel2.setText(connEvent.message);
            break;

        default:
            throw new RuntimeException("Should not happen: " + connEvent); //$NON-NLS-1$
        }
        status = connEvent.status;

        client.statusLabel.setText(msg);

        OWSLogger.DEV.info("Event " + arg); //$NON-NLS-1$
        if (OWSLogger.DEV.isLoggable(Level.FINER))
            client.errorDetail.setText(arg.toString());
    }

    private void showConnected(boolean connected) {
        client.connectButton.setEnabled(!connected);
        client.disconnectButton.setEnabled(connected);       

        JComponent proxyComponents[] = new JComponent[] { client.proxyUrlLabel, client.proxyUrlLabel,
                client.copyClipboardButton };
        for (Component c : proxyComponents) {
            c.setEnabled(connected);
        }
    }

    private void initGUI() {

        client = new owsproxyclient.OWSClientGUI();
        ArrayList<Object> defaultSettings = new ArrayList<Object>();
        defaultSettings.add(DEFAULT_PROXY_SETTINGS);
        defaultSettings.add(DEFAULT_SECURITY_SETTINGS);
        client.setSettings(defaultSettings);
        String title = client.getTitle() + " - " + "REPLACE_WITH_VERSION"; //$NON-NLS-1$ //$NON-NLS-2$
        client.setTitle(title);
        client.setVisible(true);
        client.errorDetail.setVisible(OWSLogger.DEV.isLoggable(Level.FINER));

        if (OWSLogger.DEV.isLoggable(Level.FINER)) {
            client.serviceURL.setText("http://localhost"); //$NON-NLS-1$
            client.usernameField.setText("tomcat"); //$NON-NLS-1$
            client.passwordField.setText("tomcat"); //$NON-NLS-1$
        }

        client.connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                final ConnectionParameters connectionParams = getSettings();
                if (connectionParams != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            connManager.connect(connectionParams);
                        }

                    }).start();
                }
            }

            private ConnectionParameters getSettings() {
                String host = client.serviceURL.getText();

                String username = client.usernameField.getText();
                String password = new String(client.passwordField.getPassword());

                List<Object> allSettings = client.getSettings();
                ProxyState pSettings = (ProxyState) allSettings.get(0);
                String proxyHost = pSettings.url;
                int proxyPort;
                String proxyUser = ""; //$NON-NLS-1$
                String proxyPass = ""; //$NON-NLS-1$
                if (proxyHost.length() == 0 || proxyHost.equals("http://")) { //$NON-NLS-1$
                    proxyHost = proxyUser = proxyPass = null;
                    proxyPort = -1;
                } else {
                    proxyPort = Integer.parseInt(pSettings.port);
                    if (pSettings.useAuthentication) {
                        proxyUser = pSettings.username;
                        proxyPass = new String(pSettings.password);
                    }
                }

                SecurityState sSettings = (SecurityState) allSettings.get(1);
                String keyStore = sSettings.keystore.trim();
                char[] keyStorePass = sSettings.password;

                if (keyStorePass == null || keyStorePass.length == 0) {
                    client.openSettings(1, Translations.getString("OWSClient.requirePassword")); //$NON-NLS-1$
                    return null;
                }

                ConnectionParameters connectionParams = new ConnectionParameters(host, username, password,
                        proxyHost, proxyPort, proxyUser, proxyPass, keyStore, new String(keyStorePass), sSettings.readonly, sessionCertificates);
                return connectionParams;
            }
        });

        client.validationLabel.setText(" "); //$NON-NLS-1$
        client.serviceURL.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent event) {
                String host = client.serviceURL.getText();
                client.validationLabel.setText(" "); //$NON-NLS-1$
                client.connectButton.setEnabled(true);
                try {
                    new URL(host);
                } catch (MalformedURLException e) {
                    client.connectButton.setEnabled(false);
                    String invalidURLMsg = Translations.getString("Invalid_URL"); //$NON-NLS-1$
                    client.validationLabel.setText(invalidURLMsg);
                }
            }
        });

        client.disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                connManager.disconnect();
            }
        });

        client.copyClipboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                copyToClipboard(client.proxyURL.getText());
            }
        });

        connManager.fireIdleEvent();
    }

    public static void main(String[] args) {
        new OWSClient();
    }
}
