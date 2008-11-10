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
import java.util.logging.Level;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import owsproxyclient.ExamineCertPanel.AddCert;

import com.camptocamp.owsproxy.ErrorReporter;
import com.camptocamp.owsproxy.Translations;
import com.camptocamp.owsproxy.logging.OWSLogger;

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

    private ErrorReporter _errorReporter;

    private boolean _readonlyKeystore;

    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(AuthSSLX509TrustManager.class);

    /**
     * Constructor for AuthSSLX509TrustManager.
     * @param keystore 
     * @param keyStoreFile 
     * @param storePassword 
     * @param errorReporter 
     * @param sessionCertificates 
     * @param readonlyKeystore 
     */
    public AuthSSLX509TrustManager(final X509TrustManager defaultTrustManager, KeyStore keystore, File keyStoreFile,
            String storePassword, ErrorReporter errorReporter, Collection<X509Certificate> sessionCertificates, boolean readonlyKeystore) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null"); //$NON-NLS-1$
        }
        _defaultTrustManager = defaultTrustManager;
        _keystore=keystore;
        _keyStoreFile = keyStoreFile;
        _storePassword = storePassword;
        _sessionCertificates = sessionCertificates;
        _errorReporter = errorReporter;
        _readonlyKeystore = readonlyKeystore;
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
     */
    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (LOG.isInfoEnabled() && certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                LOG.info(" Client certificate " + (c + 1) + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                LOG.info("  Subject DN: " + cert.getSubjectDN()); //$NON-NLS-1$
                LOG.info("  Signature Algorithm: " + cert.getSigAlgName()); //$NON-NLS-1$
                LOG.info("  Valid from: " + cert.getNotBefore() ); //$NON-NLS-1$
                LOG.info("  Valid until: " + cert.getNotAfter()); //$NON-NLS-1$
                LOG.info("  Issuer: " + cert.getIssuerDN()); //$NON-NLS-1$
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
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.certificate") + (c + 1) + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.subject") + cert.getSubjectDN()); //$NON-NLS-1$
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.algorithm") + cert.getSigAlgName()); //$NON-NLS-1$
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.validFrom") + cert.getNotBefore() ); //$NON-NLS-1$
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.validUntil") + cert.getNotAfter()); //$NON-NLS-1$
                OWSLogger.DEV.info(Translations.getString("AuthSSLX509TrustManager.Issuer") + cert.getIssuerDN()); //$NON-NLS-1$
            }
        }

        try{
        	_defaultTrustManager.checkServerTrusted(certificates,authType);
    	}catch (CertificateException e) {
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		PrintStream s = new PrintStream(out);
    		for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                s.println(Translations.getString("AuthSSLX509TrustManager.certificate") + (c + 1) + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                s.println(Translations.getString("AuthSSLX509TrustManager.subject") + cert.getSubjectDN()); //$NON-NLS-1$
                s.println(Translations.getString("AuthSSLX509TrustManager.algorithm") + cert.getSigAlgName()); //$NON-NLS-1$
                s.println(Translations.getString("AuthSSLX509TrustManager.validFrom") + cert.getNotBefore() ); //$NON-NLS-1$
                s.println(Translations.getString("AuthSSLX509TrustManager.validUntil") + cert.getNotAfter()); //$NON-NLS-1$
                s.println(Translations.getString("AuthSSLX509TrustManager.Issuer") + cert.getIssuerDN()); //$NON-NLS-1$
    		}

    		AddCert howToHandle = _errorReporter.certificateValidationFailure(_readonlyKeystore, e.getLocalizedMessage(), out.toString());
    		switch (howToHandle ) {
			case PERM:
				addCertificateToKeystore(certificates);
				break;
			case TEMP:
			    addSessionCertificate(certificates);
				break;

			default:
    			throw new CertificateException(Translations.getString("AuthSSLX509TrustManager.notTrusted")); //$NON-NLS-1$
			}
    	}
    }

    private void addSessionCertificate(X509Certificate[] certificates) {
        for (X509Certificate certificate : certificates) {
            _sessionCertificates.add(certificate);
        }
    }

	private void addCertificateToKeystore(X509Certificate[] certificates) {
		try {
			addToKeystore(certificates);
			BufferedOutputStream keyStoreOut = new BufferedOutputStream(
					new FileOutputStream(this._keyStoreFile));
			_keystore.store(keyStoreOut, _storePassword.toCharArray());
		} catch (Exception e1) {
			OWSLogger.DEV.log(Level.INFO, e1.getLocalizedMessage(), e1);
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
