/*
 * Created on 07.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.deegree.enterprise.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ResourceBundle;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;



/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences -
 * Java - Code Style - Code Templates
 */
public class ServletRequestWrapper extends HttpServletRequestWrapper {
    
    private static final String BUNDLE_NAME = "org.deegree.enterprise.servlet.ServletRequestWrapper";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

    private HttpServletRequest origReq = null;
    private byte[] bytes = null;

    /**
     * @param request
     */
    public ServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.origReq = request;
    }

    /**
     * creates a new ServletInputStream with a copy of the content of the original one
     * 
     * @return @throws IOException
     */
    private ServletInputStream createInputStream() throws IOException {
        
        if (bytes == null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 10000 );
            InputStream is = origReq.getInputStream();
            int c = 0;
            while ((c = is.read()) > -1) {
                bos.write(c);
            }
            bytes = bos.toByteArray();
        }        
        
        return new ProxyServletInputStream( new ByteArrayInputStream(bytes), bytes.length );
    }
    
    /**
     * sets the content of the inputstream returned by the @see #getReader() and the
     * @see #getInputStream() method as a byte array. Calling this method will 
     * override the content that may has been read from the <code>HttpServletRequest</code>
     * that has been passed to the constructor
     * 
     * @param b
     */
    public void setInputStreamAsByteArray(byte[] b) {        
        this.bytes = b;
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader( new InputStreamReader(createInputStream() ) );
    }
    /**
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return createInputStream();
    }
    

    @Override
    public Principal getUserPrincipal() {
        if ( origReq.getUserPrincipal() != null ) {
            return origReq.getUserPrincipal();
        } 
        return new Principal() {
            public String getName() {
                return RESOURCE_BUNDLE.getString( "defaultuser" );
            }
        };
        
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                 			inner classes //
    /////////////////////////////////////////////////////////////////////////

    /**
     * @author Administrator
     * 
     * TODO To change the template for this generated type comment go to Window -
     * Preferences - Java - Code Style - Code Templates
     */
    private class ProxyServletInputStream extends ServletInputStream {

        private BufferedInputStream buffered;

        public ProxyServletInputStream(InputStream in, int length) {
            if (length > 0)
                buffered = new BufferedInputStream(in, length);
            else
                buffered = new BufferedInputStream(in);
        }

        public synchronized int read() throws IOException {
            return buffered.read();
        }

        public synchronized int read(byte b[], int off, int len) throws IOException {
            return buffered.read(b, off, len);
        }

        public synchronized long skip(long n) throws IOException {
            return buffered.skip(n);
        }

        public synchronized int available() throws IOException {
            return buffered.available();
        }

        public synchronized void mark(int readlimit) {
            buffered.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            buffered.reset();
        }

        public boolean markSupported() {
            return buffered.markSupported();
        }

        public void close() throws IOException {
            buffered.close();
        }
    }

}
/* ******************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: ServletRequestWrapper.java,v $
 * Revision 1.6  2006/06/22 06:52:28  poth
 * bug fix - reading user principal
 *
 * 
 ***************************************************************************** */
