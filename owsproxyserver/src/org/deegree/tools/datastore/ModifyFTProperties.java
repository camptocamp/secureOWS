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
 53177 Bonn
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
package org.deegree.tools.datastore;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.deegree.datatypes.Types;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * This class enables a user to add a new property to a deegree WFS
 * feature type definition. It is possible to add a simple property
 * from the feature types major table, a simple property from another
 * table and a complex property from another already available feature
 * type. 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/08/08 15:52:24 $
 *
 * @since 2.0
 */
public class ModifyFTProperties {
    
    private static NamespaceContext nsCntxt = CommonNamespaces.getNamespaceContext();
    private static URI xsd = CommonNamespaces.XSNS; 
    private static URI dgwfs = CommonNamespaces.DEEGREEWFS;
    
    private URL ftDefFile; 
    private String featureType; 
    private String propertyName;
    private String source;
    private String from; 
    private String to;
    private int relType = 0;
    private String databaseFieldName;
    private int type = 0;
    
    /**
     * 
     * @param ftDefFile schema file containing feature type defintion
     * @param featureType qualified name of the feature to enhance
     * @param propertyName name of the new property
     * @param type type code of the ne2 property (@see org.deegree.datatypes.Types)
     */
    public ModifyFTProperties(URL ftDefFile, String featureType, String propertyName, 
                         String databaseFieldName, int type) {
        this.ftDefFile = ftDefFile;
        this.featureType = featureType;
        this.propertyName = propertyName;
        this.type = type;
        this.databaseFieldName = databaseFieldName;
    }
    
    /**
     * 
     * @param ftDefFile schema file containing feature type defintion
     * @param featureType qualified name of the feature to enhance
     * @param propertyName name of the new property
     * @param type type code of the new property (@see org.deegree.datatypes.Types)
     */
    public ModifyFTProperties(URL ftDefFile, String featureType, String propertyName, 
                              String databaseFieldName, String table, String from, String to, 
                              int type, int relType) {
        this.ftDefFile = ftDefFile;
        this.featureType = featureType;
        this.propertyName = propertyName;
        this.type = type;
        this.source = table;
        this.from = from;
        this.to = to;
        this.relType = relType;
        this.databaseFieldName = databaseFieldName;
    }
    
    /**
     * adds a property from the feature types major table
     * @throws Exception
     */
    public void addSimplePropertyFromMainTable() throws Exception {
        
        XMLFragment xml = new XMLFragment();
        xml.load( ftDefFile );
        
        Element cType = getPropertyParent( xml );
        
        Element elem = XMLTools.appendElement( cType, xsd, "element" );
        elem.setAttribute( "name", propertyName );
        elem.setAttribute( "type", "xsd:" + Types.getXSDTypeForSQLType( type, 0 ) );
        Element el = XMLTools.appendElement( elem, xsd, "annotation" );
        el = XMLTools.appendElement( el, xsd, "appinfo" );
        el = XMLTools.appendElement( el, dgwfs, "deegreewfs:Content" );
        el = XMLTools.appendElement( el, dgwfs, "deegreewfs:MappingField" );
        el.setAttribute( "field", databaseFieldName );
        el.setAttribute( "type", Types.getTypeNameForSQLTypeCode( type ) );
                
        File file = new File( ftDefFile.getFile() );
        FileOutputStream fos = new FileOutputStream( file );
        xml.write( fos );
        fos.close();
    }

    /**
     * returns the parent node where to add the additional property
     * @param xml
     * @return
     * @throws XMLParsingException
     */
    private Element getPropertyParent( XMLFragment xml )
                            throws XMLParsingException {
        String xpath = StringTools.concat( 100, "xs:complexType[./@name = '", 
                                           featureType, "Type']/xs:complexContent/",
                                           "xs:extension/xs:sequence" );
        return (Element)XMLTools.getNode( xml.getRootElement(), xpath, nsCntxt );
    }
    
    public void addSimplePropertyFromOtherTable() throws Exception {
        XMLFragment xml = new XMLFragment();
        xml.load( ftDefFile );
        
        Element cType = getPropertyParent( xml );
        
        Element elem = XMLTools.appendElement( cType, xsd, "element" );
        elem.setAttribute( "name", propertyName );
        elem.setAttribute( "type", "xsd:" + Types.getXSDTypeForSQLType( type, 0 ) );
        Element el = XMLTools.appendElement( elem, xsd, "annotation" );
        el = XMLTools.appendElement( el, xsd, "appinfo" );
        el = XMLTools.appendElement( el, dgwfs, "deegreewfs:Content" );        
        Element mfElem = XMLTools.appendElement( el, dgwfs, "deegreewfs:MappingField" );
        mfElem.setAttribute( "field", databaseFieldName );
        mfElem.setAttribute( "type", Types.getTypeNameForSQLTypeCode( type ) );
        
        // append relation informations
        Element relElem  = XMLTools.appendElement( el, dgwfs, "deegreewfs:Relation" );
        el = XMLTools.appendElement( relElem, dgwfs, "deegreewfs:From" );
        el = XMLTools.appendElement( el, dgwfs, "deegreewfs:MappingField" );
        el.setAttribute( "field", from );
        el.setAttribute( "type", Types.getTypeNameForSQLTypeCode( relType ) );        
        el = XMLTools.appendElement( relElem, dgwfs, "deegreewfs:To" );
        el = XMLTools.appendElement( el, dgwfs, "deegreewfs:MappingField" );
        el.setAttribute( "field", to );
        el.setAttribute( "type", Types.getTypeNameForSQLTypeCode( relType ) );
        el.setAttribute( "table", source );
                
        File file = new File( ftDefFile.getFile() );
        FileOutputStream fos = new FileOutputStream( file );
        xml.write( fos );
        fos.close();
    }
    
    public void addComplexProperty() {
        // TODO
    }
    
    
    private static boolean validate(Properties map) {
        return true;
    }
    
    private static void printHelp() {
        // TODO
    }

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        
        Properties map = new Properties();
        for (int i = 0; i < args.length; i += 2) {
            System.out.println( args[i + 1] );            
            map.put( args[i], args[i + 1] );
        }
        if ( !validate( map ) ) {
            
        }
    
        String action = map.getProperty( "-action" ); 
        URL url = new URL( map.getProperty( "-xsd" ) );
        String ft = map.getProperty( "-featureType" );
        String prop = map.getProperty( "-propertyName" );
        if ( "addProperty".equals( action ) ) {
            String field = map.getProperty( "-fieldName" );
            int type = Types.getTypeCodeForSQLType( map.getProperty( "-propertyType" ) );        
            if ( "simple".equals( map.getProperty( "-type" ) ) && 
                 map.getProperty( "-source" ) == null ) {            
                ModifyFTProperties add = new ModifyFTProperties( url, ft, prop, field, type );
                add.addSimplePropertyFromMainTable();
            } if ( "simple".equals( map.getProperty( "-type" ) ) && 
                 map.getProperty( "-source" ) != null ) {
                String table = map.getProperty( "-source" );
                String from = map.getProperty( "-fkSource" );
                String to = map.getProperty( "-fkTarget" );
                int fkType = Types.getTypeCodeForSQLType( map.getProperty( "-fkType" ) );
                ModifyFTProperties add = 
                    new ModifyFTProperties( url, ft, prop, field, table, from, to, fkType, type );
                add.addSimplePropertyFromOtherTable();
            } else if ( "complex".equals( map.getProperty( "-type" ) ) ) {
                // TODO
                throw new Exception( "not supported yet" );
            } else {
                throw new Exception( "not supported operation" );
            }
            
        } else if ( "removeProperty".equals( action ) ) {
            // TODO
            throw new Exception( "not supported yet" );
        } else {
            throw new Exception( "not supported operation" );
        }
        
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ModifyFTProperties.java,v $
Revision 1.6  2006/08/08 15:52:24  poth
*** empty log message ***

Revision 1.5  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
