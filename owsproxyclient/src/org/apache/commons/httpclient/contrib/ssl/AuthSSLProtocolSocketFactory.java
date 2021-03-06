/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.httpclient.contrib.ssl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.camptocamp.owsproxy.ErrorReporter;
import com.camptocamp.owsproxy.OWSProxyServlet;
import com.camptocamp.owsproxy.Translations;
import com.camptocamp.owsproxy.logging.OWSLogger;

/**
 * <p>
 * AuthSSLProtocolSocketFactory can be used to validate the identity of the
 * HTTPS server against a list of trusted certificates and to authenticate to
 * the HTTPS server using a private key.
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable server authentication when supplied
 * with a {@link KeyStore truststore} file containg one or several trusted
 * certificates. The client secure socket will reject the connection during the
 * SSL session handshake if the target HTTPS server attempts to authenticate
 * itself with a non-trusted certificate.
 * </p>
 * 
 * <p>
 * Use JDK keytool utility to import a trusted certificate and generate a
 * truststore file:
 * 
 * <pre>
 *     keytool -import -alias &quot;my server cert&quot; -file server.crt -keystore my.truststore
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable client authentication when supplied
 * with a {@link KeyStore keystore} file containg a private key/public
 * certificate pair. The client secure socket will use the private key to
 * authenticate itself to the target HTTPS server during the SSL session
 * handshake if requested to do so by the server. The target HTTPS server will
 * in its turn verify the certificate presented by the client in order to
 * establish client's authenticity
 * </p>
 * 
 * <p>
 * Use the following sequence of actions to generate a keystore file
 * </p>
 * <ul>
 * <li>
 * <p>
 * Use JDK keytool utility to generate a new key
 * 
 * <pre>
 * keytool -genkey -v -alias &quot;my client key&quot; -validity 365 -keystore my.keystore
 * </pre>
 * 
 * For simplicity use the same password for the key as that of the keystore
 * </p>
 * </li>
 * <li>
 * <p>
 * Issue a certificate signing request (CSR)
 * 
 * <pre>
 * keytool -certreq -alias &quot;my client key&quot; -file mycertreq.csr -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Send the certificate request to the trusted Certificate Authority for
 * signature. One may choose to act as her own CA and sign the certificate
 * request using a PKI tool, such as OpenSSL.
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the trusted CA root certificate
 * 
 * <pre>
 * keytool -import -alias &quot;my trusted ca&quot; -file caroot.crt -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the PKCS#7 file containg the complete certificate chain
 * 
 * <pre>
 * keytool -import -alias &quot;my client key&quot; -file mycert.p7 -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Verify the content the resultant keystore file
 * 
 * <pre>
 * keytool -list -v -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * </ul>
 * 
 * @author <a href="mailto:oleg -at- ural.ru">Oleg Kalnichevski</a>
 * 
 */

public class AuthSSLProtocolSocketFactory implements ProtocolSocketFactory {

    /** Log object for this class. */
    private static final Log                  LOG        = LogFactory.getLog(AuthSSLProtocolSocketFactory.class);

    private SSLContext                        sslcontext = null;
    private final File                        keystoreStore;
    private final String                      keystorePassword;

    private final Collection<X509Certificate> sessionCertificates;

    private final ErrorReporter               errorReporter;

    private boolean                           readonlyKeystore;

    /**
     * Constructor for AuthSSLProtocolSocketFactory. Either a keystore or
     * truststore file must be given. Otherwise SSL context initialization error
     * will result.
     */
    public AuthSSLProtocolSocketFactory(final File keystoreStoreFile, final String keystorePassword,
            final boolean readonlyKeystore, final ErrorReporter errorReporter,
            Collection<X509Certificate> sessionCertificates) {
        super();
        this.keystoreStore = keystoreStoreFile;
        this.keystorePassword = keystorePassword;
        this.sessionCertificates = sessionCertificates;
        this.errorReporter = errorReporter;
        this.readonlyKeystore = readonlyKeystore;
    }

    private static KeyStore createKeyStore(final URL url, final String password) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null"); //$NON-NLS-1$
        }
        LOG.debug("Initializing key store"); //$NON-NLS-1$
        KeyStore keystore = KeyStore.getInstance("jks"); //$NON-NLS-1$
        InputStream is = null;
        try {
            is = url.openStream();
            keystore.load(is, password != null ? password.toCharArray() : null);
        } finally {
            if (is != null)
                is.close();
        }
        return keystore;
    }

    private SSLContext createSSLContext() {
        try {
            KeyManager[] keymanagers = null;
            Collection<TrustManager> trustmanagers = new ArrayList<TrustManager>();
            KeyStore writeableStore = null;
            if (keystoreStore != null) {
                if( readonlyKeystore ) {
                    if (!keystoreStore.exists()) {
                        throw new KeyStoreException("Keystore does not exist"); //$NON-NLS-1$
                    }
                }else if (!keystoreStore.exists()) {
                    if (!keystoreStore.getParentFile().exists() && !keystoreStore.getParentFile().mkdirs()) {
                        throw new AssertionError("Unable to create the certificate keystore: " + keystoreStore); //$NON-NLS-1$
                    }

                    URL defaultKeystore = OWSProxyServlet.class.getResource("default_keystore"); //$NON-NLS-1$
                    KeyStore keystore = createKeyStore(defaultKeystore, "changeit"); //$NON-NLS-1$

                    FileOutputStream stream = new FileOutputStream(keystoreStore);
                    try {
                        keystore.store(stream, keystorePassword.toCharArray());
                    } finally {
                        stream.close();
                    }
                }
                writeableStore = createKeyStore(this.keystoreStore.toURI().toURL(), this.keystorePassword);
                trustmanagers.addAll(loadCertificates(writeableStore));
            }

            SSLContext sslcontext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
            sslcontext.init(keymanagers, trustmanagers.toArray(new TrustManager[0]), null);
            return sslcontext;
        } catch (NoSuchAlgorithmException e) {
            OWSLogger.DEV.log(Level.WARNING, e.getMessage(), e);
            throw new AuthSSLInitializationError("Unsupported algorithm exception: " + e.getMessage()); //$NON-NLS-1$
        } catch (KeyStoreException e) {
            OWSLogger.DEV.log(Level.WARNING, e.getMessage(), e);
            throw new AuthSSLInitializationError("Keystore exception: " + e.getMessage()); //$NON-NLS-1$
        } catch (GeneralSecurityException e) {
            OWSLogger.DEV.log(Level.WARNING, e.getMessage(), e);
            throw new AuthSSLInitializationError("Key management exception: " + e.getMessage()); //$NON-NLS-1$
        } catch (IOException e) {
            if (e.getCause() instanceof UnrecoverableKeyException) {
                throw (AuthSSLInitializationError) new AuthSSLInitializationError(
                        Translations.getString("AuthSSLProtocolSocketFactory.incorrectPassword")).initCause(e.getCause()); //$NON-NLS-1$
            }
            throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    private Collection<TrustManager> loadCertificates(KeyStore keystore) throws KeyStoreException,
            NoSuchAlgorithmException {
        Collection<TrustManager> trustmanagers;
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null"); //$NON-NLS-1$
        }
        log(keystore);
        // add temporary(session) certificates
        for (X509Certificate cert : sessionCertificates) {
            String name = cert.getSubjectX500Principal().getName();
            keystore.setCertificateEntry(name, cert);
        }
        OWSLogger.DEV.fine("Initializing trust manager"); //$NON-NLS-1$
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);
        TrustManager[] trustmanagers1 = tmfactory.getTrustManagers();
        ArrayList<TrustManager> managerCollection = new ArrayList<TrustManager>();
        for (int i = 0; i < trustmanagers1.length; i++) {
            if (trustmanagers1[i] instanceof X509TrustManager) {
                AuthSSLX509TrustManager manager;
                manager = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers1[i], keystore,
                        this.keystoreStore, this.keystorePassword, this.errorReporter, this.sessionCertificates,
                        this.readonlyKeystore);
                managerCollection.add(manager);
            }
        }
        trustmanagers = managerCollection;
        return trustmanagers;
    }

    @SuppressWarnings("unchecked")
    private void log(KeyStore keystore) throws KeyStoreException {
        if (OWSLogger.DEV.isLoggable(Level.FINE)) {
            Enumeration aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                OWSLogger.DEV.fine("Trusted certificate '" + alias + "':"); //$NON-NLS-1$ //$NON-NLS-2$
                Certificate trustedcert = keystore.getCertificate(alias);
                if (trustedcert != null && trustedcert instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) trustedcert;
                    OWSLogger.DEV.fine("  Subject DN: " + cert.getSubjectDN()); //$NON-NLS-1$
                    OWSLogger.DEV.fine("  Signature Algorithm: " + cert.getSigAlgName()); //$NON-NLS-1$
                    OWSLogger.DEV.fine("  Valid from: " + cert.getNotBefore()); //$NON-NLS-1$
                    OWSLogger.DEV.fine("  Valid until: " + cert.getNotAfter()); //$NON-NLS-1$
                    OWSLogger.DEV.fine("  Issuer: " + cert.getIssuerDN()); //$NON-NLS-1$
                }
            }
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }

    /**
     * Attempts to get a new socket connection to the given host within the
     * given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect
     * timeout a controller thread is executed. The controller thread attempts
     * to create a new socket within the given limit of time. If socket
     * constructor does not return until the timeout expires, the controller
     * terminates and throws an {@link ConnectTimeoutException}
     * </p>
     * 
     * @param host
     *            the host name/IP
     * @param port
     *            the port on the host
     * @param clientHost
     *            the local host name/IP to bind the socket to
     * @param clientPort
     *            the port on the local machine
     * @param params
     *            {@link HttpConnectionParams Http connection parameters}
     * 
     * @return Socket a new socket
     * 
     * @throws IOException
     *             if an I/O error occurs while creating the socket
     * @throws UnknownHostException
     *             if the IP address of the host cannot be determined
     */
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort,
            final HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
     */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }
}
