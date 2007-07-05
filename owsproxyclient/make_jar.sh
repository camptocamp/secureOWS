#!/bin/sh
#
# Script to create a standalone .jar file of the project

VERSION=0.2

[ -d dist ] && rm -rf dist
mkdir dist

BASE=$(pwd)

DIRS=$BASE/bin:$BASE/lib/jetty-6.0.1.jar:$BASE/lib/jetty-util-6.0.1.jar:\
$BASE/lib/servlet-api-2.5-6.0.1.jar:$BASE/lib/commons-httpclient-3.0.1.jar:\
$BASE/lib/commons-logging-1.1.jar:$BASE/lib/commons-logging-adapters-1.1.jar:\
$BASE/lib/commons-logging-api-1.1.jar:$BASE/lib/commons-codec-1.3.jar:\
$BASE/netbeans/owsproxyclient/dist/owsproxyclient.jar:\
$BASE/netbeans/owsproxyclient/dist/lib/swing-layout-1.0.jar

echo $DIRS

for j in $(echo $DIRS|sed 's/:/ /g'); do
    if ! echo $j | grep -q jar; then
        continue
    fi
    echo Handling lib: $j
    (cd dist && jar xf $j)

done

cp -r bin/* dist

cp MANIFEST.MF dist/META-INF/

(cd dist && jar -cfm  ../releases/owsproxyclient-$VERSION.jar  ../MANIFEST.MF  .)
