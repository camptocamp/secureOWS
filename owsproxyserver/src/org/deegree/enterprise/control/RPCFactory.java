package org.deegree.enterprise.control;

import java.io.Reader;
import java.util.Date;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Factory class for creating RPC methodCall and methodResponse objects from their XML
 * representation
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.12 $ $Date: 2006/10/05 09:33:09 $
 */
public class RPCFactory {
    
    private static final ILogger LOG = LoggerFactory.getLogger( RPCFactory.class );

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /**
     * creates an instance of <tt>RPCMethodCall</tt> from an XML document that can be accessed
     * through the passed <tt>Reader</tt>
     * 
     * @param reader
     *            reader to access an XML document
     * @return an RPCMethodCall
     */
    public static RPCMethodCall createRPCMethodCall( Reader reader ) throws RPCException {
        
        Document doc = null;
        try {
            doc = XMLTools.parse( reader );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return createRPCMethodCall( doc );
    }

    /**
     * creates an instance of <tt>RPCMethodCall</tt> from the XML document passed
     * 
     * @param doc
     *            XML document containing a RPC method call
     * @return an RPCMethodCall
     */
    public static RPCMethodCall createRPCMethodCall( Document doc ) throws RPCException {
     
        RPCMethodCall mc = null;
        try {
            Element root = doc.getDocumentElement();
            // get methode name - mandatory
            String methodName = XMLTools.getRequiredStringValue( "methodName", null, root );

            Element params = XMLTools.getChildElement( "params", null, root );

            RPCParameter[] parameters = null;
            if ( params != null ) {
                ElementList el = XMLTools.getChildElements( params );
                if ( el != null ) {
                    parameters = new RPCParameter[el.getLength()];
                    for (int i = 0; i < el.getLength(); i++) {
                        parameters[i] = createRPCParam( el.item( i ) );
                    }
                }
            } else {
                parameters = new RPCParameter[0];
            }

            mc = new RPCMethodCall( methodName, parameters );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return mc;
    }

    /**
     * creates an instance of <tt>RPCMethodResponse</tt> from an XML document that can be accessed
     * through the passed <tt>Reader</tt>
     * 
     * @param reader
     *            reader to access an XML document
     * @return created <tt>RPCMethodResponse</tt>
     */
    public static RPCMethodResponse createRPCMethodResponse( Reader reader ) throws RPCException {
     
        Document doc = null;
        try {
            doc = XMLTools.parse( reader );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return createRPCMethodResponse( doc );
    }

    /**
     * creates an instance of <tt>RPCMethodResponse</tt> from the XML document passed
     * 
     * @param doc
     *            XML document containing a RPC method call
     * @return created <tt>RPCMethodResponse</tt>
     */
    public static RPCMethodResponse createRPCMethodResponse( Document doc ) throws RPCException {
        
        RPCMethodResponse mc = null;
        try {
            Element root = doc.getDocumentElement();

            Element params = XMLTools.getChildElement( "params", null, root );

            if ( params != null ) {
                ElementList el = XMLTools.getChildElements( params );
                RPCParameter[] parameters = null;
                if ( el != null ) {
                    parameters = new RPCParameter[el.getLength()];
                    for (int i = 0; i < el.getLength(); i++) {
                        parameters[i] = createRPCParam( el.item( i ) );
                    }
                }
                mc = new RPCMethodResponse( parameters );
            } else {
                // a fault is contained instead of the expected result
                Element fault = XMLTools.getChildElement( "fault", null, root );
                RPCFault rpcFault = createRPCFault( fault );
                mc = new RPCMethodResponse( rpcFault );
            }

        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return mc;
    }

    /**
     * @param par
     * @return
     * @throws RPCException
     */
    public static RPCMethodResponse createRPCMethodResponse( RPCParameter[] par ) {
        RPCMethodResponse mc = null;
        if ( par != null ) {
            RPCParameter[] params = par;
            mc = new RPCMethodResponse( params );
        } else {
            LOG.logError( "Fehler createRPCMEthodResponse in RPSFactory" );
        }
        return mc;
    }

    /**
     * creates a <tt>RPCParameter</tt> from its XML representation
     * 
     * @param param
     *            element containing a RPC param
     * @return created <tt>RPCParameter</tt>
     */
    private static RPCParameter createRPCParam( Element param ) throws RPCException {
        
        RPCParameter parameter = null;
        try {
            Element value = XMLTools.getChildElement( "value", null, param );
            Element child = XMLTools.getFirstChildElement( value );
            Object o = null;
            Class cl = null;
            if ( child.getNodeName().equals( "struct" ) ) {
                o = createRPCStruct( child );
                cl = RPCStruct.class;
            } else if ( child.getNodeName().equals( "string" ) ) {
                o = XMLTools.getRequiredStringValue( "string", null, value );
                cl = String.class;
            } else if ( child.getNodeName().equals( "int" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./int", nsContext );
                o = new Integer( (int) d );
                cl = Integer.class;
            } else if ( child.getNodeName().equals( "i4" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./i4", nsContext );
                o = new Integer( (int) d );
                cl = Integer.class;
            } else if ( child.getNodeName().equals( "double" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./double", nsContext );
                o = new Double( d );
                cl = Double.class;
            } else if ( child.getNodeName().equals( "boolean" ) ) {
                o = Boolean.valueOf( child.getFirstChild().getNodeValue().equals( "1" ) );
                cl = Boolean.class;
            } else if ( child.getNodeName().equals( "dateTime.iso8601" ) ) {
                String s = XMLTools.getRequiredStringValue( "dateTime.iso8601", null, value );
                o = TimeTools.createCalendar( s ).getTime();
                cl = Date.class;
            } else if ( child.getNodeName().equals( "base64" ) ) {
            } else if ( child.getNodeName().equals( "array" ) ) {
                o = createArray( child );
                cl = RPCParameter[].class;
            }
            parameter = new RPCParameter( cl, o );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return parameter;
    }

    /**
     * creates a RPC struture object from the passed <tt>Element</tt>
     * 
     * @param struct
     *            element containing a RPC struct
     * @return created <tt>RPCStruct</tt>
     */
    private static RPCStruct createRPCStruct( Element struct ) throws RPCException {

        RPCStruct structure = null;
        try {
            ElementList el = XMLTools.getChildElements( struct );
            RPCMember[] members = null;
            if ( el != null ) {
                members = new RPCMember[el.getLength()];
                for (int i = 0; i < el.getLength(); i++) {
                    members[i] = createRPCMember( el.item( i ) );
                }
            }
            structure = new RPCStruct( members );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return structure;
    }

    /**
     * creates a RPC struture member object from the passed <tt>Element</tt>
     * 
     * @param member
     *            element containing a RPC member
     * @return created <tt>RPCMember</tt>
     */
    private static RPCMember createRPCMember( Element member ) throws RPCException {

        RPCMember mem = null;
        try {
            String name = XMLTools.getRequiredStringValue( "name", null, member );
            Element value = XMLTools.getChildElement( "value", null, member );
            Element child = XMLTools.getFirstChildElement( value );
            Object o = null;
            Class cl = null;
            if ( child.getNodeName().equals( "struct" ) ) {
                o = createRPCStruct( child );
                cl = RPCStruct.class;
            } else if ( child.getNodeName().equals( "string" ) ) {
                o = XMLTools.getRequiredStringValue( "string", null, value );
                cl = String.class;
            } else if ( child.getNodeName().equals( "int" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./int", nsContext );
                o = new Integer( (int) d );
                cl = Integer.class;
            } else if ( child.getNodeName().equals( "i4" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./i4", nsContext );
                o = new Integer( (int) d );
                cl = Integer.class;
            } else if ( child.getNodeName().equals( "double" ) ) {
                double d = XMLTools.getRequiredNodeAsDouble( value, "./double", nsContext );
                o = new Double( d );
                cl = Double.class;
            } else if ( child.getNodeName().equals( "boolean" ) ) {
                o = Boolean.valueOf( child.getFirstChild().getNodeValue().equals( "1" ) );
                cl = Boolean.class;
            } else if ( child.getNodeName().equals( "dateTime.iso8601" ) ) {
                String s = XMLTools.getRequiredStringValue( "dateTime.iso8601", null, value );
                o = TimeTools.createCalendar( s ).getTime();
                cl = Date.class;
            } else if ( child.getNodeName().equals( "base64" ) ) {
            } else if ( child.getNodeName().equals( "array" ) ) {
                o = createArray( child );
                cl = RPCParameter[].class;
            }        
            mem = new RPCMember( cl, o, name );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        } 

        return mem;
    }

    /**
     * creates an <tt>ArrayList</tt> object from the passed <tt>Element</tt>
     * 
     * @param array
     * @return created <tt>ArrayList</tt>
     */
    private static RPCParameter[] createArray( Element array ) throws RPCException {

        RPCParameter[] param = null;
        try {
            Element data = XMLTools.getChildElement( "data", null, array );
            ElementList el = XMLTools.getChildElements( data );
            if ( el != null ) {
                param = new RPCParameter[el.getLength()];
                for (int i = 0; i < el.getLength(); i++) {
                    Element child = XMLTools.getFirstChildElement( el.item( i ) );
                    Object o = null;
                    Class cl = null;
                    if ( child.getNodeName().equals( "struct" ) ) {
                        o = createRPCStruct( child );
                        cl = RPCStruct.class;
                    } else if ( child.getNodeName().equals( "string" ) ) {
                        o = XMLTools.getRequiredStringValue( "string", null, el.item( i ) );
                        cl = String.class;
                    } else if ( child.getNodeName().equals( "int" ) ) {
                        double d = XMLTools.getRequiredNodeAsDouble( el.item( i ), "./int", nsContext );
                        o = new Integer( (int) d );
                        cl = Integer.class;
                    } else if ( child.getNodeName().equals( "i4" ) ) {
                        double d = XMLTools.getRequiredNodeAsDouble( el.item( i ), "./i4", nsContext );
                        o = new Integer( (int) d );
                        cl = Integer.class;
                    } else if ( child.getNodeName().equals( "double" ) ) {
                        double d = XMLTools.getRequiredNodeAsDouble( el.item( i ), "./double",
                            nsContext );
                        o = new Double( d );
                        cl = Double.class;
                    } else if ( child.getNodeName().equals( "boolean" ) ) {
                        o = Boolean.valueOf( child.getFirstChild().getNodeValue().equals( "1" ) );
                        cl = Boolean.class;
                    } else if ( child.getNodeName().equals( "dateTime.iso8601" ) ) {
                        String s = XMLTools.getRequiredStringValue( "dateTime.iso8601", null, el
                            .item( i ) );
                        o = TimeTools.createCalendar( s ).getTime();
                        cl = Date.class;
                    } else if ( child.getNodeName().equals( "base64" ) ) {
                    } else if ( child.getNodeName().equals( "array" ) ) {
                        o = createArray( child );
                        cl = RPCParameter[].class;
                    }
                    param[i] = new RPCParameter( cl, o );
                }
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return param;
    }

    /**
     * creates an <tt>RPCFault</tt> object from the passed <tt>Element</tt>
     * 
     * @param fault
     *            fault element
     * @return created <tt>RPCFault</tt>
     */
    private static RPCFault createRPCFault( Element fault ) throws RPCException {
        
        RPCFault rpcFault = null;
        try {
            Element value = XMLTools.getChildElement( "value", null, fault );
            Element child = XMLTools.getFirstChildElement( value );
            RPCStruct struct = createRPCStruct( child );
            String s1 = null;
            String s2 = null;
            Object o = struct.getMember( "faultCode" ).getValue();
            if ( o != null ) {
                s1 = o.toString();
            }
            o = struct.getMember( "faultString" ).getValue();
            if ( o != null ) {
                s2 = o.toString();
            }
            rpcFault = new RPCFault( s1, s2 );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new RPCException( e.toString() );
        }

        return rpcFault;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RPCFactory.java,v $
Revision 1.12  2006/10/05 09:33:09  mays
remove a bug: now every RPCMethodCall contains RPCParameters, even though they might be empty.

Revision 1.11  2006/07/29 08:49:25  poth
references to deprecated classes removed

Revision 1.10  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
