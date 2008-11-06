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
    public static final ProxyState      DEFAULT_PROXY_SETTINGS    = new ProxyState("http://", "3218", false, "",
                                                                          new char[0]);
    public static final SecurityState   DEFAULT_SECURITY_SETTINGS = new SecurityState(System.getProperty("user.home")
                                                                          + "/.secureows/keystore", "changeit".toCharArray(), false);

    ConnectionManager                   connManager;
    private owsproxyclient.OWSClientGUI client;
    Color                               textColor;
    private ConnectionStatus status = ConnectionStatus.IDLE;
    private Collection<X509Certificate> sessionCertificates = new HashSet<X509Certificate>();
    public OWSClient() {

        connManager = new ConnectionManager();
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

        OWSLogger.DEV.finer("Got event: " + connEvent);

        client.proxyURL.setText("");
        // resets state
        if (textColor == null)
            textColor = client.statusLabel.getForeground();
        client.statusLabel.setForeground(textColor);

        client.statusLabel2.setText(" ");

        String msg;

        switch (connEvent.status) {
        case IDLE:
            showConnected(false);
            msg = Translations.getString("OWSProxy_not_connected");

            break;
        case CONNECTING:
            showConnected(true);
            msg = Translations.getString("Connecting...");
            break;
        case RUNNING:
            showConnected(true);

            client.statusLabel.setForeground(new Color(0, 128, 0));
            msg = Translations.getString("Connected");
            client.proxyURL.setText(connManager.getListeningAddress());
            break;

        case UNAUTHORIZED:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Unauthorized");
            client.statusLabel2.setText(connEvent.message);
            break;

        case ERROR:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Error");
            client.statusLabel2.setText(connEvent.message);
            break;

        case PROXY_AUTH_REQUIRED:
            showConnected(status!=ConnectionStatus.CONNECTING);
            client.statusLabel.setForeground(Color.RED);
            msg = Translations.getString("Proxy_Auth");
            client.statusLabel2.setText(connEvent.message);
            break;

        default:
            throw new RuntimeException("Should not happen: " + connEvent);
        }
        status = connEvent.status;

        client.statusLabel.setText(msg);

        OWSLogger.DEV.info("Event " + arg);
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
        String title = client.getTitle() + " - " + Translations.getString("version");
        client.setTitle(title);
        client.setVisible(true);
        client.errorDetail.setVisible(OWSLogger.DEV.isLoggable(Level.FINER));

        if (OWSLogger.DEV.isLoggable(Level.FINER)) {
            client.serviceURL.setText("http://localhost");
            client.usernameField.setText("tomcat");
            client.passwordField.setText("tomcat");
        }

        client.connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                final ConnectionParameters connectionParams = getSettings();
                new Thread(new Runnable() {
                    public void run() {
                        connManager.connect(connectionParams);
                    }

                }).start();
                
            }

            private ConnectionParameters getSettings() {
                String host = client.serviceURL.getText();

                String username = client.usernameField.getText();
                String password = new String(client.passwordField.getPassword());

                List<Object> allSettings = client.getSettings();
                ProxyState pSettings = (ProxyState) allSettings.get(0);
                String proxyHost = pSettings.url;
                int proxyPort;
                String proxyUser = "";
                String proxyPass = "";
                if (proxyHost.length() == 0 || proxyHost.equals("http://")) {
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
                String keyStore = sSettings.keystore;
                char[] keyStorePass = sSettings.password;

                if (keyStorePass == null || keyStorePass.length == 0) {
                    client.openSettings(1, "Keystore password is required");
                    return getSettings();
                }

                ConnectionParameters connectionParams = new ConnectionParameters(host, username, password,
                        proxyHost, proxyPort, proxyUser, proxyPass, keyStore, new String(keyStorePass), sSettings.readonly, false, sessionCertificates);
                return connectionParams;
            }
        });

        client.validationLabel.setText(" ");
        client.serviceURL.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent event) {
                String host = client.serviceURL.getText();
                client.validationLabel.setText(" ");
                client.connectButton.setEnabled(true);
                try {
                    new URL(host);
                } catch (MalformedURLException e) {
                    client.connectButton.setEnabled(false);
                    String invalidURLMsg = Translations.getString("Invalid_URL");
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
