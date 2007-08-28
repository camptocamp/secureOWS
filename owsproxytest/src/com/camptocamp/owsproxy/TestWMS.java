package com.camptocamp.owsproxy;

import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.apache.commons.httpclient.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for testing WMS requests on the owsproxyserver 
 *
 */
public class TestWMS extends TestCase {

    final static String BASE_WMS_URL = "http://localhost:8080/owsproxyserver/"; 
    
    static final Credentials TOMCAT_CRED = new UsernamePasswordCredentials("tomcat", "tomcat");
    static final Credentials ALICE_CRED = new UsernamePasswordCredentials("alice", "alice");
    static final Credentials INVALID_CRED = new UsernamePasswordCredentials("invalid_user", "nopass");

    // NOTE: This has to be kept in sync with the value from accessDenied.html 
    static final String DENIED_MESSAGE = "Access denied";

    // Layers for layer restriction tests
    final static Set<String> AUTHORIZED_LAYERS_SET1 = new HashSet<String>(Arrays.asList(new String[] 
                             {"tiger:giant_polygon", "topp:tasmania_cities"}));

    // TODO: tests several versions
    private static final String VERSION = "1.1.1";

    static String AUTHORIZED_LAYERS_SET1_AS_STRING = "";
    static {
        AUTHORIZED_LAYERS_SET1_AS_STRING = "";
        for (String layer : AUTHORIZED_LAYERS_SET1) {
            AUTHORIZED_LAYERS_SET1_AS_STRING += "," + layer;
        }
        AUTHORIZED_LAYERS_SET1_AS_STRING = AUTHORIZED_LAYERS_SET1_AS_STRING.substring(1);
    }

    // Number of layers in map
    final int NUM_LAYERS = 14;

    // Utility variable to quickly enable/disable tests
    //private boolean TESTS_ENABLED = false;
    private boolean TESTS_ENABLED = true;

    private Map<String, String> overrideMap(Map<String, String> original,
                                            Map<String, String> override) {

        Map<String, String> overridden = (Map<String, String>)((HashMap<String, String>)original).clone();
        
        if (override != null)
            overridden.putAll(override);

        return overridden;
    }
    
    private String buildQueryString(Map<String, String> params,
                                    Map<String, String> override) {

        Map<String, String> updated = overrideMap(params, override);
        
        String queryString = "";
        for (String p : updated.keySet()) {
            queryString += p + "=" + updated.get(p) + "&";
        }
        
        return queryString;
    }
    
    public void setUp() {

    }
    
    private Map<String, String> getGetMapDefaultParams() {
        Map<String, String> m = new HashMap<String, String>();
        
        m.put("REQUEST", "GetMap");
        m.put("SERVICE", "WMS");
        m.put("VERSION", VERSION);
        m.put("WIDTH", "684");
        m.put("HEIGHT", "497");
        m.put("LAYERS", "nurc%3AImg_Sample");
        m.put("TRANSPARENT", "TRUE");
        m.put("FORMAT", "image/png");
        m.put("BBOX", "-124.03,11.36,-68.14,55.86");
        m.put("SRS", "EPSG:4326");
        m.put("STYLES", "");
        
        return m;
    }
    
    private Map<String, String> getGetCapabilitiesDefaultParams() {
        Map<String, String> m = new HashMap<String, String>();

        m.put("REQUEST", "GetCapabilities");
        m.put("SERVICE", "WMS");
        m.put("VERSION", VERSION);

        return m;
    }

    private Map<String, String> getGetFeatureInfoDefaultParams() {
        Map<String, String> m = new HashMap<String, String>();

        m.put("REQUEST", "GetFeatureInfo");
        m.put("SERVICE", "WMS");
        m.put("VERSION", VERSION);

        m.put("EXCEPTIONS", "application/vnd.ogc.se_xml");
        m.put("X", "25");
        m.put("Y", "311");
        m.put("INFO_FORMAT", "text/html");
        
        m.put("QUERY_LAYERS", "tiger%3Agiant_polygon");
        
        // GetFeatureInfo is based on GetMap
        return overrideMap(getGetMapDefaultParams(), m);
    }

    public void testNoRestrictions() {
        if(!TESTS_ENABLED)return;

        final String TEST = "test_no_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();

        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), INVALID_CRED, SC_UNAUTHORIZED, false);
    }

    public void testSizeRestrictions() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_size_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();
        
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        override.clear();
        override.put("WIDTH", "2000");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }

    private Set<String> getLayers(InputStream is) {
        
        List<String> layers = new Vector<String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( is );
            
            System.out.println("document " + document);
            System.out.println("document " + document.getDocumentElement());
            NodeList nl = document.getElementsByTagName("Layer");
            for (int i = 0; i < nl.getLength(); i++) {
                
                Node n = nl.item(i);
                // XXX name could be elsewhere
                String layerName = n.getFirstChild().getTextContent();
                // XXX ignore root layer
                if (layerName.equals("My GeoServer WMS"))
                    continue;
                layers.add(layerName);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new AssertionFailedError("failed parsing capabilities XML");
        }
        System.out.println("Layers: " + layers);
        return new HashSet<String>(layers);
    }
    
    public void testLayerRestrictions() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_layer_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // getCapabilities
        override.clear();
        InputStream is = doTestRequest(TEST, buildQueryString(getGetCapabilitiesDefaultParams(), override), TOMCAT_CRED, SC_OK, false);
        assertEquals(AUTHORIZED_LAYERS_SET1, getLayers(is));
        
        // getMap, with authorized layers -> allowed
        override.clear();
        override.put("LAYERS", AUTHORIZED_LAYERS_SET1_AS_STRING);
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // getMap, with unauthorized layers -> denied
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }

    public void testLayerRestrictions2() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_layer_restrictions2";
        
        Map<String, String> override = new HashMap<String, String>();
        
        InputStream is;
        
        // getCapabilities
        // Alice can see all layers
        override.clear();
        is = doTestRequest(TEST, buildQueryString(getGetCapabilitiesDefaultParams(), override), ALICE_CRED, SC_OK, false);
        assertEquals(NUM_LAYERS, getLayers(is).size());

        // tomcat can see only two
        is = doTestRequest(TEST, buildQueryString(getGetCapabilitiesDefaultParams(), override), TOMCAT_CRED, SC_OK, false);
        assertEquals(AUTHORIZED_LAYERS_SET1, getLayers(is));

        
        // Alice can getMap all layers
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), ALICE_CRED, SC_OK, false);

        // ... not tomcat
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);

        // ... but he can see only two
        override.clear();
        override.put("LAYERS", AUTHORIZED_LAYERS_SET1_AS_STRING);
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // ... so does alice
        override.clear();
        override.put("LAYERS", AUTHORIZED_LAYERS_SET1_AS_STRING);
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), ALICE_CRED, SC_OK, false);        
    }
    
    public void testBboxRestrictions() {
        if(!TESTS_ENABLED)return;
        
        // Keep this in sync with the testBboxOutsideRestrictions
        
        final String TEST = "test_bbox_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // default bbox is surrounding the allowed one -> deny
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);

        // totally inside allowed bbox -> allow
        override.clear();
        override.put("BBOX", "-92.91,26.21,-67.42,52.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // intersects allowed bbox -> deny
        override.clear();
        override.put("BBOX", "-92.91,26.21,-10,52.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);

        // totally outside of allowed bbox -> deny
        override.clear();
        override.put("BBOX", "-92.91,80.21,-67.42,85.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }

    public void testBboxOutsideRestrictions() {
        if(!TESTS_ENABLED)return;
        
        // Keep this in sync with the testBboxRestrictions
        
        final String TEST = "test_bbox_outside_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // default bbox is surrounding the allowed one -> allow
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);
        
        // totally inside allowed bbox -> allow
        override.clear();
        override.put("BBOX", "-92.91,26.21,-67.42,52.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // intersects allowed bbox -> allow
        override.clear();
        override.put("BBOX", "-92.91,26.21,-10,52.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // totally outside of allowed bbox -> deny
        override.clear();
        override.put("BBOX", "-92.91,80.21,-67.42,85.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }
    
    public void testBboxRestrictions2() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_bbox_restrictions2";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // default bbox is outside of allowed -> deny for tomcat ...
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, true);

        // ... and allow for alice
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), ALICE_CRED, SC_OK, false);

        // inside allowed bbox -> allow for tomcat
        override.clear();
        override.put("BBOX", "-92.91,26.21,-67.42,52.18");
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // ... and allow for alice
        doTestRequest(TEST, buildQueryString(getGetMapDefaultParams(), override), ALICE_CRED, SC_OK, false);
    }
    
    public void testGetFeatureInfoNoRestriction() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_no_restrictions";
        
        Map<String, String> override = new HashMap<String, String>();
        
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, false);
    }

    public void testGetFeatureInfoFeaturecount() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_featureinfo_featurecount";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // no feature count is equivalent to 1 feature count -> allowed
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // 200 > 100 -> deny
        override.clear();
        override.put("FEATURE_COUNT", "200");
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }

    public void testGetFeatureInfoFormat() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_featureinfo_format";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // format text/html is allowed
        override.clear();
        override.put("INFO_FORMAT", "text/html");
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // unauthorized format -> denied
        override.clear();
        override.put("INFO_FORMAT", "x-foo/x-bar");
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }
    
    public void testGetFeatureInfoLayers() {
        if(!TESTS_ENABLED)return;
        
        final String TEST = "test_featureinfo_layers";
        
        Map<String, String> override = new HashMap<String, String>();
        
        // default layer is allowed
        override.clear();
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, false);

        // unauthorized layer -> denied
        override.clear();
        override.put("QUERY_LAYERS", "invalid_layer");
        doTestRequest(TEST, buildQueryString(getGetFeatureInfoDefaultParams(), override), TOMCAT_CRED, SC_OK, true);
    }
    
    // TODO: Add expected content type: image / xml / ...
    private InputStream doTestRequest(String servletName, String queryString, Credentials creds,
                                       int expectStatus, boolean expectDenied) {

        
        System.out.println("----------------------------------");
        System.out.println("Testing servlet " + servletName);
        
        String serviceURL = BASE_WMS_URL + servletName;
        
        // XXX not working
        HostnameVerifier dummyVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(dummyVerifier);

        HttpClient client = new HttpClient();
        if (queryString != null) {
            serviceURL += "?" + queryString;
        }
        System.out.println("End point: " + serviceURL);
        HttpMethod method = new GetMethod(serviceURL);

        if (creds != null) {
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(AuthScope.ANY, creds);
        }

        // Execute the method.
        int statusCode = -1;
        try {
            statusCode = client.executeMethod(method);
        } catch (Exception e) {
            fail(e.toString());
        }

        System.out.println("Http status: " + statusCode);
        assertEquals(expectStatus, statusCode);
        
        Header contentTypeHeader = method.getResponseHeader("Content-Type");
        assertNotNull(contentTypeHeader);
        
        System.out.println("Content-Type: " + contentTypeHeader.getValue());

        // WARNING: Going to buffer response body of large or unknown
        // size. Using getResponseBodyAsStream instead is recommended.
        byte[] responseBody = null;
        try {
            responseBody = method.getResponseBody();
        } catch (IOException e) {
            fail(e.toString());
        }

        assertTrue(responseBody.length > 0);
        
        String body = new String(responseBody);
        
        /* Uncomment to save body to file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/out.xml"));
            bw.write(body);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        
        assertEquals("Wrong access restriction", expectDenied, body.contains(DENIED_MESSAGE));
        
        try {
            return method.getResponseBodyAsStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionFailedError("No input stream");
        }
    }
}
