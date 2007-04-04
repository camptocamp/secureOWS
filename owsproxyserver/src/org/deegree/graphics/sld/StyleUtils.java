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
package org.deegree.graphics.sld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterTools;
import org.deegree.ogcbase.PropertyPath;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/11/07 10:59:27 $
 *
 * @since 2.0
 */
public class StyleUtils {

    /**
     * returns a list of all @see PropertyPath's required by the passed UserStyles 
     * 
     * @param styles
     * @param scaleDen
     * @return
     * @throws PropertyPathResolvingException
     */
    public static List<PropertyPath> extractRequiredProperties( List<UserStyle> styles,
                                                               double scaleDen )
                            throws PropertyPathResolvingException {
        List<PropertyPath> pp = new ArrayList<PropertyPath>();

        for ( int i = 0; i < styles.size(); i++ ) {
            FeatureTypeStyle[] fts = styles.get( i ).getFeatureTypeStyles();
            for ( int j = 0; j < fts.length; j++ ) {
                Rule[] rules = fts[j].getRules();
                for ( int k = 0; k < rules.length; k++ ) {
                    double minScale = rules[k].getMinScaleDenominator();
                    double maxScale = rules[k].getMaxScaleDenominator();
                    if ( minScale <= scaleDen && maxScale > scaleDen ) {
                        Filter filter = rules[k].getFilter();
                        List list = FilterTools.extractPropertyPaths( filter );
                        pp.addAll( list );
                        Symbolizer[] sym = rules[k].getSymbolizers();
                        for ( int d = 0; d < sym.length; d++ ) {
                            if ( sym[d] instanceof PointSymbolizer ) {
                                pp = extractPPFromPointSymbolizer( (PointSymbolizer) sym[d], pp );
                            } else if ( sym[d] instanceof LineSymbolizer ) {
                                pp = extractPPFromLineSymbolizer( (LineSymbolizer) sym[d], pp );
                            } else if ( sym[d] instanceof PolygonSymbolizer ) {
                                pp = extractPPFromPolygonSymbolizer( (PolygonSymbolizer) sym[d], pp );
                            } else if ( sym[d] instanceof TextSymbolizer ) {
                                pp = extractPPFromTextSymbolizer( (TextSymbolizer) sym[d], pp );
                            }
                        }
                    }
                }
            }
        }

        List<PropertyPath> tmp = new ArrayList<PropertyPath>( pp.size() );
        for ( int i = 0; i < pp.size(); i++ ) {
            if ( !tmp.contains( pp.get( i ) ) ) {
                tmp.add( pp.get( i ) );
            }
        }

        return tmp;
    }

    /**
     * 
     * @param symbolizer
     * @param pp
     * @return
     * @throws PropertyPathResolvingException 
     */
    private static List<PropertyPath> extractPPFromTextSymbolizer( TextSymbolizer symbolizer,
                                                                  List<PropertyPath> pp )
                            throws PropertyPathResolvingException {
        Map css = null;
        if ( symbolizer.getFill() != null ) {
            css = symbolizer.getFill().getCssParameters();
            pp = extractPPFromCssParameter( css, pp );
        }
        css = symbolizer.getFont().getCssParameters();
        pp = extractPPFromCssParameter( css, pp );
        if ( symbolizer.getGeometry() != null ) {
            pp.add( symbolizer.getGeometry().getPropertyPath() );
        }
        Halo halo = symbolizer.getHalo();
        ParameterValueType pvt = null;
        if ( halo != null ) {
            pvt = halo.getRadius();
            pp = extractPPFromParamValueType( pvt, pp );
            css = halo.getFill().getCssParameters();
            pp = extractPPFromCssParameter( css, pp );
            css = halo.getStroke().getCssParameters();
            pp = extractPPFromCssParameter( css, pp );
        }
        pvt = symbolizer.getLabel();
        pp = extractPPFromParamValueType( pvt, pp );
        LinePlacement lp = symbolizer.getLabelPlacement().getLinePlacement();
        if ( lp != null ) {
            pvt = lp.getGap();
            pp = extractPPFromParamValueType( pvt, pp );
            pvt = lp.getLineWidth();
            pp = extractPPFromParamValueType( pvt, pp );
            pvt = lp.getPerpendicularOffset();
            pp = extractPPFromParamValueType( pvt, pp );
        }

        return pp;
    }

    /**
     * 
     * @param symbolizer
     * @param pp
     * @return
     * @throws PropertyPathResolvingException 
     */
    private static List<PropertyPath> extractPPFromPolygonSymbolizer( PolygonSymbolizer symbolizer,
                                                                     List<PropertyPath> pp )
                            throws PropertyPathResolvingException {

        Map css = null;
        if ( symbolizer != null ) {
            if ( symbolizer.getFill() != null ) {
                css = symbolizer.getFill().getCssParameters();
                pp = extractPPFromCssParameter( css, pp );
            }

            if ( symbolizer.getGeometry() != null ) {
                pp.add( symbolizer.getGeometry().getPropertyPath() );
            }

            if ( symbolizer.getStroke() != null ) {
                css = symbolizer.getStroke().getCssParameters();
                pp = extractPPFromCssParameter( css, pp );
            }
        }

        return pp;
    }

    /**
     * 
     * @param symbolizer
     * @param pp
     * @return
     * @throws PropertyPathResolvingException 
     */
    private static List<PropertyPath> extractPPFromLineSymbolizer( LineSymbolizer symbolizer,
                                                                  List<PropertyPath> pp )
                            throws PropertyPathResolvingException {

        if ( symbolizer.getGeometry() != null ) {
            pp.add( symbolizer.getGeometry().getPropertyPath() );
        }

        Map css = symbolizer.getStroke().getCssParameters();
        pp = extractPPFromCssParameter( css, pp );

        return pp;
    }

    /**
     * 
     * @param symbolizer
     * @param pp
     * @return
     * @throws PropertyPathResolvingException 
     */
    private static List<PropertyPath> extractPPFromPointSymbolizer( PointSymbolizer symbolizer,
                                                                   List<PropertyPath> pp )
                            throws PropertyPathResolvingException {

        Graphic graphic = symbolizer.getGraphic();
        if ( graphic != null ) {
            if ( graphic.getOpacity() != null ) {
                pp = extractPPFromParamValueType( graphic.getOpacity(), pp );
            }

            if ( graphic.getRotation() != null ) {
                pp = extractPPFromParamValueType( graphic.getRotation(), pp );
            }

            if ( graphic.getSize() != null ) {
                pp = extractPPFromParamValueType( graphic.getSize(), pp );
            }

            if ( symbolizer.getGeometry() != null ) {
                pp.add( symbolizer.getGeometry().getPropertyPath() );
            }
        }

        return pp;
    }

    private static List<PropertyPath> extractPPFromCssParameter( Map css, List<PropertyPath> pp )
                            throws PropertyPathResolvingException {
        if ( css != null ) {
            Iterator iter = css.values().iterator();
            while ( iter.hasNext() ) {
                ParameterValueType pvt = ( (CssParameter) iter.next() ).getValue();
                pp = extractPPFromParamValueType( pvt, pp );
            }
        }
        return pp;
    }

    private static List<PropertyPath> extractPPFromParamValueType( ParameterValueType pvt,
                                                                  List<PropertyPath> pp )
                            throws PropertyPathResolvingException {
        if ( pvt != null ) {
        Object[] components = pvt.getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof Expression ) {
                    pp = FilterTools.extractPropertyPaths( (Expression) components[i], pp );
                }
            }
        }
        return pp;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: StyleUtils.java,v $
 Revision 1.6  2006/11/07 10:59:27  poth
 bug fix - null checking for ParameterValueTypes and CSSParameters

 Revision 1.5  2006/10/11 07:59:14  poth
 implementation completed (Point Symbolizer)

 Revision 1.4  2006/10/11 06:55:08  poth
 bug fix

 Revision 1.3  2006/10/09 15:55:40  poth
 bug fix - null check for polygon symbolizer

 Revision 1.2  2006/10/02 10:01:18  poth
 bug fix determining properties required by a style

 Revision 1.1  2006/09/26 12:44:34  poth
 initial check in


 ********************************************************************** */