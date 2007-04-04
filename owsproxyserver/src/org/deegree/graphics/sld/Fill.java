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
import java.util.HashMap;
import java.util.Iterator;

import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * A Fill allows area geometries to be filled. There are two types of fills:
 * solid-color and repeated GraphicFill. In general, if a Fill element is
 * omitted in its containing element, no fill will be rendered. The default is a
 * solid 50%-gray (color "#808080") opaque fill.
 * <p>
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.10 $ $Date: 2006/10/17 20:31:18 $
 */
public class Fill extends Drawing implements Marshallable {
    
    // default values
    public static final Color FILL_DEFAULT = Color.decode("#808080");

    public static final double OPACITY_DEFAULT = 1.0;

    /**
     * Constructs a new <tt>Fill</tt>.
     */
    protected Fill() {
        super(new HashMap(), null);
    }

    /**
     * Constructs a new <tt>Fill</tt>.
     */
    protected Fill(HashMap cssParams, GraphicFill graphicFill) {
        super(cssParams, graphicFill);
    }

    /**
     * Returns the (evaluated) value of the fill's CssParameter 'fill'.
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluation fails or the value is invalid
     */
    public Color getFill(Feature feature) throws FilterEvaluationException {
        Color awtColor = FILL_DEFAULT;

        CssParameter cssParam = (CssParameter) cssParams.get("fill");

        if (cssParam != null) {
            String s = cssParam.getValue(feature);

            try {
                awtColor = Color.decode(s);
            } catch (NumberFormatException e) {
                throw new FilterEvaluationException("Given value ('" + s
                        + "') for CSS-Parameter 'fill' "
                        + "does not denote a valid color!");
            }
        }

        return awtColor;
    }

    /**
     * sets the value of the fill's CssParameter 'fill' as a simple color
     * 
     * @param color
     *            color to be set
     */
    public void setFill(Color color) {

        String hex = ColorUtils.toHexCode( "#", color);
        CssParameter fill = StyleFactory.createCssParameter("fill", hex);

        cssParams.put("fill", fill);
    }

    /**
     * Returns the (evaluated) value of the fill's CssParameter 'fill-opacity'.
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluation fails or the value is invalid
     */
    public double getOpacity(Feature feature) throws FilterEvaluationException {
        double opacity = OPACITY_DEFAULT;

        CssParameter cssParam = (CssParameter) cssParams.get("fill-opacity");

        if (cssParam != null) {
            String value = cssParam.getValue(feature);

            try {
                opacity = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new FilterEvaluationException(
                        "Given value for parameter 'fill-opacity' ('" + value
                                + "') has invalid format!");
            }

            if ((opacity < 0.0) || (opacity > 1.0)) { throw new FilterEvaluationException(
                    "Value for parameter 'fill-opacity' (given: '" + value
                            + "') must be between 0.0 and 1.0!"); }
        }

        return opacity;
    }

    /**
     * sets the value of the opacity's CssParameter 'opacity' as a value. Valid
     * values ranges from 0 .. 1. If a value < 0 is passed it will be set to 0.
     * If a value > 1 is passed it will be set to 1.
     * 
     * @param opacity
     *            opacity to be set
     */
    public void setOpacity(double opacity) {

        if (opacity > 1) {
            opacity = 1;
        } else if (opacity < 0) {
            opacity = 0;
        }

        CssParameter fillOp = StyleFactory.createCssParameter("fill-opacity",
                "" + opacity);
        cssParams.put("fill-opacity", fillOp);
    }

    /**
     * exports the content of the CssParameter as XML formated String
     * 
     * @return xml representation of the CssParameter
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer("<Fill>");

        if (graphicFill != null) {
            sb.append(((Marshallable) graphicFill).exportAsXML());
        }
        Iterator iterator = cssParams.values().iterator();
        while (iterator.hasNext()) {
            sb.append(((Marshallable) iterator.next()).exportAsXML());
        }

        sb.append("</Fill>");

        return sb.toString();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Fill.java,v $
Revision 1.10  2006/10/17 20:31:18  poth
*** empty log message ***

Revision 1.9  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
