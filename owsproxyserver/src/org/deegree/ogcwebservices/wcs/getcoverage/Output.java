package org.deegree.ogcwebservices.wcs.getcoverage;

import org.deegree.datatypes.Code;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.wcs.WCSException;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: bezema $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2006/11/29 15:58:57 $ *  * @since 2.0
 */

public class Output {

    /**
     * 
     * @uml.property name="crs"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Code crs = null;

    /**
     * 
     * @uml.property name="format"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Code format = null;
 
    
    /**
     * @param crs
     * @param format
     * @throws WCSException if one of the parameters is null
     */
    public Output(Code crs, Code format) throws WCSException {
        if ( crs == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new WCSException( "GetCoverage", "'crs' is missing", code );
        }
        if ( format == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new WCSException( "GetCoverage", "'format' is missing", code );
        }
        this.crs = crs;
        this.format = format;
    }

    /**
     * @return Returns the crs.
     * 
     * @uml.property name="crs"
     */
    public Code getCrs() {
        return crs;
    }

    /**
     * @return Returns the format.
     * 
     * @uml.property name="format"
     */
    public Code getFormat() {
        return format;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer(300);
        sb.append("CRS=");
        sb.append( crs );
        sb.append(", format="  );
        sb.append( format );
        return sb.toString();
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Output.java,v $
   Revision 1.3  2006/11/29 15:58:57  bezema
   added toString and fixed javadoc and warnings

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.3  2004/06/18 06:18:46  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
