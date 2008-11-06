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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import owsproxyclient.CertificateWarningDialog;

/**
 * <p>
 * AuthSSLX509TrustManager can be used to extend the default {@link X509TrustManager} 
 * with additional trust decisions.
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * for use without additional customization.
 * </p>
 */

public class AuthSSLX509TrustManager implements X509TrustManager
{
    private X509TrustManager _defaultTrustManager = null;

	private KeyStore _keystore;

	private File _keyStoreFile;

	private String _storePassword;

    private Collection<X509Certificate> _sessionCertificates;

    private boolean _noUI;

    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(AuthSSLX509TrustManager.class);

    /**
     * Constructor for AuthSSLX509TrustManager.
     * @param keystore 
     * @param keyStoreFile 
     * @param storePassword 
     * @param noUI 
     * @param sessionCertificates 
     */
    public AuthSSLX509TrustManager(final X509TrustManager defaultTrustManager, KeyStore keystore, File keyStoreFile,
            String storePassword, boolean noUI, Collection<X509Certificate> sessionCertificates) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null");
        }
        _defaultTrustManager = defaultTrustManager;
        _keystore=keystore;
        _keyStoreFile = keyStoreFile;
        _storePassword = storePassword;
        _sessionCertificates = sessionCertificates;
        _noUI = noUI;
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
     */
    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (LOG.isInfoEnabled() && certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                LOG.info(" Client certificate " + (c + 1) + ":");
                LOG.info("  Subject DN: " + cert.getSubjectDN());
                LOG.info("  Signature Algorithm: " + cert.getSigAlgName());
                LOG.info("  Valid from: " + cert.getNotBefore() );
                LOG.info("  Valid until: " + cert.getNotAfter());
                LOG.info("  Issuer: " + cert.getIssuerDN());
            }
        }
    	_defaultTrustManager.checkClientTrusted(certificates,authType);
        
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
     */
    public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (LOG.isInfoEnabled() && certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                LOG.info(" Server certificate " + (c + 1) + ":");
                LOG.info("  Subject DN: " + cert.getSubjectDN());
                LOG.info("  Signature Algorithm: " + cert.getSigAlgName());
                LOG.info("  Valid from: " + cert.getNotBefore() );
                LOG.info("  Valid until: " + cert.getNotAfter());
                LOG.info("  Issuer: " + cert.getIssuerDN());
            }
        }

        try{
        	_defaultTrustManager.checkServerTrusted(certificates,authType);
    	}catch (CertificateException e) {
    	    if( _noUI ) {
    	        throw e;
    	    }
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		PrintStream s = new PrintStream(out);
    		for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                s.println(" Client certificate " + (c + 1) + ":");
                s.println("  Subject DN: " + cert.getSubjectDN());
                s.println("  Signature Algorithm: " + cert.getSigAlgName());
                s.println("  Valid from: " + cert.getNotBefore() );
                s.println("  Valid until: " + cert.getNotAfter());
                s.println("  Issuer: " + cert.getIssuerDN());
    		}
    		CertificateWarningDialog warningDialog = new CertificateWarningDialog("localhost", e.getLocalizedMessage(), out.toString(), new JFrame(), true);
    		warningDialog.setVisible(true);
    		
    		switch (warningDialog.addCertificateSelected()) {
			case PERM:
				addCertificateToKeystore(certificates);
				break;
			case TEMP:
			    addSessionCertificate(certificates);
				break;

			default:
    			throw new CertificateException("Certificate could not be validated");
			}
    	}
    }

    private void addSessionCertificate(X509Certificate[] certificates) {
//        try {
        for (X509Certificate certificate : certificates) {
            _sessionCertificates.add(certificate);
//            addToKeystore(certificates);
        }
//        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmfactory.init(_keystore);
//        _defaultTrustManager = (X509TrustManager) tmfactory.getTrustManagers()[0];
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

	private void addCertificateToKeystore(X509Certificate[] certificates) {
		try {
			addToKeystore(certificates);
			BufferedOutputStream keyStoreOut = new BufferedOutputStream(
					new FileOutputStream(this._keyStoreFile));
			_keystore.store(keyStoreOut, _storePassword.toCharArray());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

    private void addToKeystore(X509Certificate[] certificates) throws KeyStoreException {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                String name = cert.getSubjectX500Principal().getName();
                _keystore.setCertificateEntry(name, cert);
            }
    }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this._defaultTrustManager.getAcceptedIssuers();
    }
}
