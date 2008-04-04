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
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JComponent;

import owsproxyclient.OWSClientGUI;
import owsproxyclient.ProxySettingsDialog;

import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;

public class OWSClient implements Observer {

	ConnectionManager connManager;
	private owsproxyclient.OWSClientGUI client;
    Color textColor;
	private ProxySettingsDialog proxyDialog;

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
		ConnectionEvent connEvent = (ConnectionEvent)arg;
		
		OWSLogger.DEV.finer("Got event: " + connEvent);
        
		client.proxyURL.setText("");
        client.connectButton.setEnabled(connEvent.status == 
                                        ConnectionEvent.ConnectionStatus.IDLE);
		client.disconnectButton.setEnabled(connEvent.status != 
										ConnectionEvent.ConnectionStatus.IDLE);
        
        // resets state
		if (textColor == null)
                textColor = client.statusLabel.getForeground();
        client.statusLabel.setForeground(textColor);

        JComponent proxyComponents[] = new JComponent[] {client.proxyUrlLabel, client.proxyUrlLabel, client.copyClipboardButton};
        for (Component c : proxyComponents) {
            c.setEnabled(false);
        }
        
        client.statusLabel2.setText(" ");
        
        if (connEvent.status == ConnectionEvent.ConnectionStatus.IDLE) {
			client.statusLabel.setText(
			        java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                    .getString("OWSProxy_not_connected"));
            
		} else if (connEvent.status == ConnectionEvent.ConnectionStatus.CONNECTING) {
            client.statusLabel.setText(
                    java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                    .getString("Connecting..."));
			
		} else if (connEvent.status == ConnectionEvent.ConnectionStatus.RUNNING) {
            for (Component c : proxyComponents) {
                c.setEnabled(true);
            }
            
			client.statusLabel.setForeground(new Color(0, 128, 0));
            client.statusLabel.setText(
                    java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                    .getString("Connected"));
			client.proxyURL.setText(connManager.getListeningAddress());
            
		} else if (connEvent.status == ConnectionEvent.ConnectionStatus.ERROR ||
                   connEvent.status == ConnectionEvent.ConnectionStatus.UNAUTHORIZED) {

            client.statusLabel.setForeground(Color.RED);

            String msg = java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                             .getString("Error");
            if (connEvent.status == ConnectionEvent.ConnectionStatus.UNAUTHORIZED)
                msg = java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                             .getString("Unauthorized");
			
            client.statusLabel2.setText(connEvent.message);
			client.statusLabel.setText(msg);
			
		} else {
			throw new RuntimeException("Should not happen");
		}
		
        OWSLogger.DEV.info("Event " + arg);
        if (OWSLogger.DEV.isLoggable(Level.FINER))
            client.errorDetail.setText(arg.toString());
	}
	
	private void initGUI() {
		
        client = new owsproxyclient.OWSClientGUI();
		client.setVisible(true);
        client.errorDetail.setVisible(OWSLogger.DEV.isLoggable(Level.FINER));
        
        if (OWSLogger.DEV.isLoggable(Level.FINER)) {
            client.serviceURL.setText("http://localhost");
            client.usernameField.setText("tomcat");
            client.passwordField.setText("tomcat");
        }
		
        client.connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new Thread(new Runnable() {
					public void run() {
						// XXX validate these fields
						String host = client.serviceURL.getText();
                        
                        String username = client.usernameField.getText();
						String password = new String(client.passwordField.getPassword());
						
						String proxyHost = proxyDialog.url.getText().trim();
						int proxyPort;
						String proxyUser = "";
						String proxyPass = "";
						if(proxyHost.length()==0 || proxyHost.equals("http://")){
							proxyHost = proxyUser = proxyPass = null;
							proxyPort = -1;
						}else{
							proxyPort=Integer.parseInt(proxyDialog.port.getText());
							if( proxyDialog.useAuthentication.isSelected() ){
								proxyUser=proxyDialog.username.getText();
								proxyPass=new String(proxyDialog.password.getPassword());
							}
						}
						
						connManager.connect(new ConnectionParameters(host, username, password, 
								proxyHost, proxyPort, proxyUser, proxyPass));
					}
                }).start();
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
                    String invalidURLMsg = java.util.ResourceBundle.getBundle("owsproxyclient/translations")
                        .getString("Invalid_URL");
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
        
        initAdvancedDialog(client);

        connManager.fireIdleEvent();
	}
	
	private void initAdvancedDialog(OWSClientGUI client2) {
		this.proxyDialog = new ProxySettingsDialog(client, true);
		proxyDialog.okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				proxyDialog.setVisible(false);
			}
			
		});
		
		client.proxyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				proxyDialog.setVisible(true);
			}
			
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OWSClient();
	}
}
