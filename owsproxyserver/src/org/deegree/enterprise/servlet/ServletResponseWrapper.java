/*
 * Created on 07.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.deegree.enterprise.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences -
 * Java - Code Style - Code Templates
 */
public class ServletResponseWrapper extends HttpServletResponseWrapper {

    protected ServletOutputStream stream = null;
    protected PrintWriter writer = null;
    protected HttpServletResponse origResponse = null;
    private String contentType = null;

    /**
     * 
     * @param response
     */
    public ServletResponseWrapper(HttpServletResponse response) {
        super(response);
        origResponse = response;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private ServletOutputStream createOutputStream() {
        stream = new ProxyServletOutputStream( 10000 );
        return stream;
    }

    /**
     * 
     */
    public ServletOutputStream getOutputStream() throws IOException {       

        if (stream == null) {
            stream = createOutputStream();
        }
        return stream;
    }

    /*
     *  (non-Javadoc)
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        stream = createOutputStream();
        writer = new PrintWriter(stream);
        return writer;
    }
      
    /**
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;        
        if ( contentType != null ) {
            super.setContentType(contentType);
        }
    }
    
    /**
     * 
     */
    public String getContentType() {       
        return this.contentType;
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
    public class ProxyServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bos = null;

        public ProxyServletOutputStream(int length) {
            if (length > 0)
                bos = new ByteArrayOutputStream(length);
            else
                bos = new ByteArrayOutputStream(10000);
        }

        /**
         * @see java.io.OutputStream#close()
         */
        public void close() throws IOException {            
            bos.close();
        }
        
        /**
         * @see java.io.OutputStream#flush()
         */
        public void flush() throws IOException {
            bos.flush();
        }
        
        /**
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len);
        }
        
        /**
         * @see java.io.OutputStream#write(byte[])
         */
        public void write(byte[] b) throws IOException {
            bos.write(b);
        }
        
        /**
         * @see java.io.OutputStream#write(int)
         */
        public void write(int v) throws IOException {
            bos.write(v);
        }
        
        public byte[] toByteArray() {
            return bos.toByteArray();
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServletResponseWrapper.java,v $
Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
