package com.camptocamp.owsproxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Logger Filter for the OWS requests
 */
public class OWSLoggerFilter implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    private String _parametersToString(Map parameters)
            throws UnsupportedEncodingException {
        String result = "";

        boolean first = true;
        for (Object key : parameters.keySet()) {
            if (!first) {
                result += "&";
            } else {
                first = false;
            }
            String[] values = (String[]) parameters.get(key);
            assert values.length == 1;
            String val = URLEncoder.encode(values[0], "UTF-8");
            result += key + "=" + val;

        }
        return result;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (filterConfig == null)
            return;
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        System.out.println("LOGGING request");

        String prefix = new Date().getTime() + " - ";

        Map m = request.getParameterMap();
        String paramsString = _parametersToString(m);
        System.out.println("Value: " + paramsString);

        Map<String, String> params = new HashMap<String, String>();

        params.put("date_readable", new Date().toString());
        params.put("date_unix", "" + new Date().getTime());

        params.put("request", paramsString);
        params.put("local_addr", request.getLocalAddr());
        params.put("local_name", request.getLocalName());
        params.put("local_port", "" + request.getLocalPort());
        // XXX more ... ?
        params.put("remote_host", "" + request.getRemoteHost());

        params.put("user_principal", ""
                + ((HttpServletRequest) request).getUserPrincipal().getName());

        String result = "";
        boolean first = true;
        for (Object key : params.keySet()) {
            if (!first) {
                result += ";";
            } else {
                first = false;
            }
            result += key + "=" + params.get(key);

        }
        System.out.println(result);

        String catalinaBase = System.getProperty("catalina.base");
        String logPath = catalinaBase + File.separator + "logs"
                + File.separator + "owsproxyserver_logs.txt";
        System.out.println("Log path: " + logPath);
        BufferedWriter bf = new BufferedWriter(new FileWriter(logPath, true));
        bf.write(prefix + result + "\n");
        bf.flush();
        bf.close();

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(
                (HttpServletResponse) response);
        chain.doFilter(request, wrapper);
    }

}
