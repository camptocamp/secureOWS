package org.apache.commons.httpclient.contrib.ssl;

import java.io.File;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class OWSCertificateException extends RuntimeException {

    private static final long serialVersionUID = 3597202693916534939L;

    private X509Certificate[] certificates;

    private KeyStore keystore;

    private File file;

    private String password;

    public OWSCertificateException(String msg, X509Certificate[] certificates, KeyStore keystore, File storeFile, String password) {
        super(msg);
        this.certificates = certificates;
        this.keystore = keystore;
        this.file = storeFile;
        this.password = password;
    }

    public X509Certificate[] getCertificates() {
        return certificates;
    }
    
    public KeyStore getKeystore() {
        return keystore;
    }
    
    public File getFile() {
        return file;
    }
    
    public String getPassword() {
        return password;
    }

}
