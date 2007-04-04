// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/model/GetTermsResultSet.java,v 1.5 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.portal.standard.gazetteer.model;

import java.util.Arrays;
import java.util.HashMap;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;
import org.deegree.ogcwebservices.gazetteer.SI_LocationInstance;


/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 1.1
 */
public class GetTermsResultSet {
    private HashMap terms = null;
    private String childType = null;
    private String parentType = null;
    private String parentValues = null;

    /**
     * @param parentType
     * @param parentValues
     * @param childType
     * @param terms
     */
    public GetTermsResultSet( String parentType, String parentValues, String childType, 
                              HashMap terms ) {
        this.parentType = parentType;
        this.parentValues = parentValues;
        this.childType = childType;
        this.terms = terms;
    }

    /**
     * @return Returns the childType.
     */
    public String getChildType() {
        return childType;
    }

    /**
     * @param childType The childType to set.
     */
    public void setChildType( String childType ) {
        this.childType = childType;
    }

    /**
     * @return Returns the parentType.
     */
    public String getParentType() {
        return parentType;
    }

    /**
     * @param parentType The parentType to set.
     */
    public void setParentType( String parentType ) {
        this.parentType = parentType;
    }

    /**
     * @return Returns the parentValues.
     */
    public String getParentValues() {
        return parentValues;
    }

    /**
     * @param parentValues The parentValues to set.
     */
    public void setParentValues( String parentValues ) {
        this.parentValues = parentValues;
    }

    /**
     * @return Returns the terms.
     */
    public SI_LocationInstance[] getTerms( String gazetteer ) {        
        return (SI_LocationInstance[])terms.get( gazetteer );
    }
    
    /**
     * @return Returns the terms.
     * @param property nae of the property to be base of the sorting
     * @param up defines the sorting direction
     */
    public SI_LocationInstance[] getSortedTerms( String gazetteer, boolean up ) {
        SI_LocationInstance[] li = (SI_LocationInstance[])terms.get( gazetteer );
        Arrays.sort( li );
        if ( !up ) {
            for (int i = 0; i < li.length/2; i++) {
                SI_LocationInstance tmp = li[li.length-1-i];
                li[li.length-1-i] = li[i];
                li[i] = tmp;
            }
        }
        return li;
    }
    
    /**
     * @return Returns the terms.
     * @param gazetteer name of the gazetteer that has delivered the SI_LocationInstance
     *                  to be sorted
     * @param property nae of the property to be base of the sorting
     * @param up defines the sorting direction 
     */
    public SI_LocationInstance[] getSortedTerms( String gazetteer, String property, 
                                                 boolean up ) {
        SI_LocationInstance[] li = (SI_LocationInstance[])terms.get( gazetteer );
        Comparator[] comp = new Comparator[ li.length ];
        for (int i = 0; i < li.length; i++) {
            // Comparator is just a decorator class to enable sorting
            // by a user defined attribute
            comp[i] = new Comparator( (Feature)li[i], property );
        }
        Arrays.sort( comp );
        if ( !up ) {
            for (int i = 0; i < comp.length/2; i++) {
                SI_LocationInstance tmp = 
                    (SI_LocationInstance)comp[comp.length-1-i].getFeature();
                li[comp.length-1-i] = (SI_LocationInstance)comp[i].getFeature();
                li[i] = tmp;
            } 
        }
        return li;
    }

    /**
     * @param terms The terms to set.
     */
    public void setTerms( HashMap terms ) {
        this.terms = terms;
    }
    
    /**
     * returns the names of all gazetteers which request results (terms) are 
     * contained in this result set
     */
    public String[] getGazetteerNames() {
        return (String[])terms.keySet().toArray( new String[ terms.size() ] );
    }
    
    /**
     * prive decorator to enable sorting of LocationInstances
     * 
     * @author Administrator
     */
    private class Comparator implements Comparable {
        
        private Feature thisFeat; 
        private String compProp;
        
        Comparator( Feature li, String compProp ) {
            this.thisFeat = li;
            this.compProp = compProp;
        }
                       
        public int compareTo( Object o ) {
            Feature li = (Feature)o;
            QualifiedName qn = new QualifiedName( compProp );
            String thisP = ((String)thisFeat.getDefaultProperty( qn ).getValue()).trim();
            String otherP = ((String)li.getDefaultProperty( qn ).getValue()).trim();
            try {                
                Integer.parseInt( ""+thisP.charAt( 0 ) );
                Integer.parseInt( ""+otherP.charAt( 0 ) );
                int n1 = getNumberPart( thisP );
                int n2 = getNumberPart( otherP );
                if ( n1 > n2 ) return 1;
                if ( n1 < n2 ) return -1;
                if ( n1 == n2 ) return 0;
            } catch ( Exception e ) {
                return otherP.compareTo( thisP );
            }
            return 0;
        }
        
        private int getNumberPart(String s) {
            String n = "";
            int i = 0;
            boolean br = false;
            do {
                try {
                    Integer.parseInt( "" + s.charAt( i ) );
                    n = n + s.charAt( i );
                    i++;
                } catch ( Exception e ) {
                    br = true;
                }                
            } while ( br );
            return Integer.parseInt( n );
        }
        
        Feature getFeature() {
            return thisFeat;
        }
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GetTermsResultSet.java,v $
   Revision 1.5  2006/08/29 19:54:14  poth
   footer corrected

   Revision 1.4  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:12  poth
   *** empty log message ***

   Revision 1.8  2006/01/16 20:36:39  poth
   *** empty log message ***

   Revision 1.7  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.6.2.1  2005/11/15 13:36:55  deshmukh
   Modified Object to FeatureProperty

   Revision 1.6  2005/08/30 13:40:03  poth
   no message

   Revision 1.5  2005/06/15 16:16:53  poth
   no message

   Revision 1.4  2005/04/07 09:23:17  poth
   no message

   Revision 1.3  2005/04/06 15:56:46  poth
   no message

   Revision 1.2  2005/01/06 17:51:46  poth
   no message

   Revision 1.2  2004/06/21 08:05:49  ap
   no message

   Revision 1.1  2004/05/22 09:55:36  ap
   no message

   Revision 1.2  2004/03/16 08:07:23  poth
   no message

   Revision 1.1  2004/03/15 07:38:05  poth
   no message



********************************************************************** */
