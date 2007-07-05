#!/bin/sh
BASE=$(dirname $0)
java -classpath $BASE/bin:$BASE/lib/jetty-6.0.1.jar:$BASE/lib/jetty-util-6.0.1.jar:$BASE/lib/servlet-api-2.5-6.0.1.jar:$BASE/lib/commons-httpclient-3.0.1.jar:$BASE/lib/commons-logging-1.1.jar:$BASE/lib/commons-logging-adapters-1.1.jar:$BASE/lib/commons-logging-api-1.1.jar:$BASE/lib/commons-codec-1.3.jar:/home/sypasche/c2c/projects/wms/netbeans/owsproxyclient/dist/owsproxyclient.jar:/home/sypasche/c2c/projects/wms/netbeans/owsproxyclient/dist/lib/swing-layout-1.0.jar com.camptocamp.owsproxy.OWSClient
