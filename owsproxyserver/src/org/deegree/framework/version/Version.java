/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.framework.version;

import java.util.Properties;

import org.deegree.framework.util.BootLogger;

/**
 * The version number is created by 3 parts, the first represents the version number, the second a
 * essential update of a release, the third the build number. The version number could be numeric or
 * alphanumeric, e.g. 'Foo2' or '2.0'.<BR>
 * e.g.:<BR>
 * 2.0alpha.142 - version no. 2, release 0 alpha, build 142<BR>
 * 2.0beta.178 - version no. 2, release 0 beta, build 178 <BR>
 * 2.0.198 - version no. 2, release 0, build 198 <BR>
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version 3.0 . $Revision: 1.10 $, $Date: 2006/08/02 13:23:33 $
 */
public final class Version {

    private static String BUILD_DATE;

    private static String VERSION_NUMBER;

    private static String BUILD_NUMBER;

    private static String BUILD_BY;

    /*
     * Class loader to get version properties from resource file
     */
    static {
        try {
            // fetch version property
            Properties versionProps = new Properties();
            versionProps.load( Version.class.getResourceAsStream( "version.properties" ) );
            VERSION_NUMBER = versionProps.getProperty( "version.number" );

            // fetch build properties
            Properties buildProps = new Properties();
            buildProps.load( Version.class.getResourceAsStream( "buildId.properties" ) );
            BUILD_DATE = buildProps.getProperty( "build.date" );
            BUILD_NUMBER = buildProps.getProperty( "build.number" );
            BUILD_BY = buildProps.getProperty( "build.by" );
        } catch ( Exception ex ) {
            BootLogger.logError( "Error fetching version / build properties: " + ex.getMessage(),
                                 ex );
        }
    }

    /**
     * Don't let anyone instantiate this class.
     */
    private Version() {
    }

    /**
     * Returns the version of the application. The version number is created by 3 parts, the first
     * represents the version number, the second a essential update of a release, the third the
     * build number. The version number could be numeric or alphanumeric, e.g. 'Foo2' or '2.0'.
     * <P>
     * e.g.:<BR>
     * 1.0.42 - version no. 1, release 0, build 42<BR>
     * 1.1.78 - version no. 1, release 1, build 78<BR>
     * 2.0.98 - version no. 2, release 0, build 98<BR>
     * 
     * @return the version string
     */
    public static String getVersion() {
        final String s = getVersionNumber() + " (" + getBuildDate() + " build-" + getBuildNumber()
                         + "-" + getBuildBy() + ")";
        return s;
    }

    /**
     * Returns the version number.
     * 
     * @return the version number
     */
    public static String getVersionNumber() {
        return Version.VERSION_NUMBER;
    }

    /**
     * Returns the current build number.
     * 
     * @return the current build number
     */
    public static String getBuildNumber() {
        return Version.BUILD_NUMBER;
    }

    /**
     * Returns the date string when the current build was created.
     * 
     * @return the date as String
     */
    public static String getBuildDate() {
        return Version.BUILD_DATE;
    }

    /**
     * Returns the name of the builder.
     * 
     * @return the name of the builder
     */
    public static String getBuildBy() {
        return Version.BUILD_BY;
    }

    public static void main( String[] args ) {
        System.out.println( "deegree version: " + Version.getVersion() );
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Version.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/08/02 13:23:33  mschneider
 * Changes to this class. What the people have been up to: Fixed version string.
 * Changes to this class. What the people have been up to: Revision 1.9
 * 2006/07/27 16:20:14 mschneider Splitted former Version.properties into
 * version.properties+buildId.properties.
 * 
 * Revision 1.8 2006/07/12 14:46:19 poth comment footer added
 * 
 **************************************************************************************************/
