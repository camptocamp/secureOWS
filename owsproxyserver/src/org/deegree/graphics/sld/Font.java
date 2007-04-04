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
package org.deegree.graphics.sld;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;


/**
 * The Font element identifies a font of a certain family, style, weight, size
 * and color.
 * <p>
 * The supported CSS-Parameter names are:
 * <ul>
 * <li>font-family
 * <li>font-style
 * <li>font-weight
 * <li>font-size
 * <li>font-color
 * <p>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.12 $ $Date: 2006/10/17 20:31:18 $
 */
public class Font implements Marshallable {
    
    public static final int STYLE_NORMAL  = java.awt.Font.PLAIN;
    public static final int STYLE_ITALIC  = java.awt.Font.ITALIC;
    public static final int STYLE_OBLIQUE = java.awt.Font.ITALIC;

    public static final int WEIGHT_NORMAL = java.awt.Font.PLAIN;
    public static final int WEIGHT_BOLD   = java.awt.Font.BOLD;
    
    public static final int SIZE_DEFAULT = 10;
    
    public static final Color COLOR_DEFAULT = new Color (127, 127, 127);
    
    private Map cssParams = null;

    /**
     * Constructs a new <tt>Font<tt>.
     * <p>
     * @param cssParams keys are <tt>Strings<tt> (see above), values are
     *                  <tt>CssParameters</tt>
     */
    protected Font( Map cssParams ) {
        this.cssParams = cssParams;
    }
    
    
    /**
     * returns the Map of the CssParameters describing a Font
     * @return
     */
    public Map getCssParameters() {
        return cssParams;
    }


    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-family'.
     * <p>
     * @param feature specifies the <tt>Feature</tt> to be used for evaluation
     *        of the underlying 'sld:ParameterValueType'
     * @return the (evaluated) <tt>String</tt> value of the parameter
     * @throws FilterEvaluationException if the evaluation fails
     */
    public String getFamily( Feature feature ) throws FilterEvaluationException {
        CssParameter cssParam = (CssParameter)cssParams.get( "font-family" );

        if ( cssParam == null ) {
            return null;
        }

        return cssParam.getValue( feature ).trim();
    }

    /**
     * Sets the value of the font's CssParameter 'font-family'.
     * <p>
     * @param family font family to be set
     */
    public void setFamily(String family)     {
        CssParameter fontFamily = StyleFactory.createCssParameter( "font-family", "" + family );
        cssParams.put( "font-family", fontFamily );
    }
    
    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-style'.
     * <p>
     * @param feature specifies the <tt>Feature</tt> to be used for evaluation
     *        of the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException if the evaluation fails or the
     *         specified style is not one of the following: 'normal', 'italic'
     *         and 'oblique'
     */
    public int getStyle( Feature feature ) throws FilterEvaluationException {
        CssParameter cssParam = (CssParameter)cssParams.get( "font-style" );

        if ( cssParam == null ) {
            return STYLE_NORMAL;
        }

        String s = cssParam.getValue( feature ).trim();

        if ( s.equals( "normal" ) ) {
            return STYLE_NORMAL;
        } else if ( s.equals( "italic" ) ) {
            return STYLE_ITALIC;
        } else if ( s.equals( "oblique" ) ) {
            return STYLE_OBLIQUE;
        }

        throw new FilterEvaluationException( "Given value ('" + s + 
                                             "') for CssParameter 'font-style' is " + 
                                             "invalid: allowed values are 'normal', 'italic' and 'oblique'." );
    }
    
    /**
     * Sets the value of the font's CssParameter 'font-style'.
     * <p>
     * @param style font-style to be set
     */
    public void setStyle(int style)    {
        CssParameter fontStyle = StyleFactory.createCssParameter( "font-style", "" + style );
        cssParams.put( "font-style", fontStyle );
    }

    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-weight'
     * as a <tt>ParameterValueType</tt>.
     * <p>
     * @param feature specifies the <tt>Feature</tt> to be used for evaluation
     *        of the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException if the evaluation fails or the
     *         specified weight is not one of the following: 'normal' and 'bold'
     */
    public int getWeight(Feature feature) throws FilterEvaluationException {
        CssParameter cssParam = (CssParameter)cssParams.get( "font-weight" );

        if ( cssParam == null ) {
            return WEIGHT_NORMAL;
        }

        String s = cssParam.getValue( feature ).trim();

        if ( s.equals( "normal" ) ) {
            return WEIGHT_NORMAL;
        } else if ( s.equals( "bold" ) ) {
            return WEIGHT_BOLD;
        }

        throw new FilterEvaluationException( "Given value ('" + s + 
                                             "') for CssParameter 'font-weight' is " + 
                                             "invalid: allowed values are 'normal' and 'bold'." );
    }

    /**
     * Sets the value of the font's CssParameter 'font-weight'.
     * <p>
     * @param weight font-weight to be set
     */
    public void setWeight (int weight)  {
        CssParameter fontWeight = StyleFactory.createCssParameter( "font-weight", "" + weight );
        cssParams.put( "font-weight", fontWeight );
    }
    
    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-size'.
     * <p>
     * @param feature specifies the <tt>Feature</tt> to be used for evaluation
     *        of the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException if the evaluation fails or the value
     *         does not denote a valid number or the number is not greater or 
     *         equal zero
     */
    public int getSize(Feature feature) throws FilterEvaluationException {
        CssParameter cssParam = (CssParameter)cssParams.get( "font-size" );
        int sizeInt = SIZE_DEFAULT;

        if ( cssParam != null ) {
            String s = cssParam.getValue( feature ).trim();

            try {                
                sizeInt = (int)Double.parseDouble( s );
            } catch ( NumberFormatException e ) {
                throw new FilterEvaluationException( "Given value ('" + s + 
                                                     "') for CssParameter 'font-size' is " + 
                                                     "not a valid number." );
            }

            if ( sizeInt <= 0 ) {
                throw new FilterEvaluationException( 
                        "Value of CssParameter 'font-size' must be greater or " + "equal zero." );
            }
        }

        return sizeInt;
    }
    
    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-size'.
     * <p>
     * @param size font-size to be set
     */
    public void setSize (int size) {        
        CssParameter fontSize = StyleFactory.createCssParameter( "font-size", "" + size );
        cssParams.put( "font-size", fontSize );
    }

    /**
     * Returns the (evaluated) value of the font's CssParameter 'font-color'.
     * <p>
     * @param feature specifies the <tt>Feature</tt> to be used for evaluation
     *        of the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException if the evaluation fails
     */
    public Color getColor( Feature feature ) throws FilterEvaluationException {
        CssParameter cssParam = (CssParameter)cssParams.get( "font-color" );
        Color awtColor = COLOR_DEFAULT;

        if ( cssParam != null ) {
            String s = cssParam.getValue( feature ).trim();

            try {
                awtColor = Color.decode( s );
            } catch ( NumberFormatException e ) {
                throw new FilterEvaluationException( "Given value ('" + s + 
                                                     "') for CSS-Parameter 'font-color' " + 
                                                     "does not denote a valid color!" );
            }
        }

        return awtColor;
    }

    /**
     * Sets the value of the font's CssParameter 'font-color'.
     * <p>
     * @param color the font-color to be set
     */    
    public void setColor(Color color) {
        String hex = ColorUtils.toHexCode( "#", color ) ;
        CssParameter fontColor = StyleFactory.createCssParameter( "font-color", hex );
        cssParams.put( "font-color", fontColor );
    }   
    
    
    /**
     * exports the content of the Font as XML formated String
     *
     * @return xml representation of the Font
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<Font>" );
        Iterator iterator = cssParams.values().iterator();
        while ( iterator.hasNext() ) {
            sb.append( ((Marshallable)iterator.next()).exportAsXML()  );
        }
        
        sb.append( "</Font>" );
        
        return sb.toString();
    }

    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Font.java,v $
Revision 1.12  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.11  2006/09/25 20:29:28  poth
changes required for extracting PropertyPath's used by a Style

Revision 1.10  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
