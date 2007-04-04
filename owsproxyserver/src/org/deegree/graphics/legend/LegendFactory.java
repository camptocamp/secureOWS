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
package org.deegree.graphics.legend;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;

/**
 * factory class for creating legend elements/images to be used
 * with WMS GetLegendGraphic request
 * 
 * @version $Revision: 1.15 $
 * @author $author$
 */
public class LegendFactory {
    
    private static final ILogger LOG = LoggerFactory.getLogger( LegendFactory.class );

    private String label = "";

    private String legendtitle = "";

    /**
     * creates a <tt>LegendElement</tt> using the passed
     * <tt>BufferedImage</tt>
     * 
     * @return <tt>LegendElement</tt>
     */
    public LegendElement createLegendElement(BufferedImage legendImage) {
        return new LegendElement(legendImage);
    }

    /**
     * creates a <tt>LegendElement</tt> from a SLD <tt>Style</tt>.
     * Depending on the <tt>Style</tt> the returned <tt>LegendElement</tt>
     * may is a <tt>LegendElementCollection</tt>.
     * 
     * @return <tt>LegendElement</tt>
     */
    public LegendElement createLegendElement(AbstractStyle style, int width,
                                             int height, String title) throws LegendException {

        setLegendTitle(title);

        if (style instanceof UserStyle) {

            LegendElement le = null;

            FeatureTypeStyle[] fts = ((UserStyle) style).getFeatureTypeStyles();
            LegendElementCollection lec = new LegendElementCollection();

            for (int a = 0; a < fts.length; a++) {
                // legendtitle
                if (title != null && title.length() > 0) {                  
                    if ( ((UserStyle) style).getTitle() != null ) {                     
                        setLegendTitle( title );
                    } else {
                        setLegendTitle( title );
                    }
                } else {
                    setLegendTitle(fts[a].getName());
                }
                Rule[] rules = fts[a].getRules();

                for (int b = 0; b < rules.length; b++) {

                    if (rules[b].getFilter() != null) {
                        Filter f = rules[b].getFilter();
                        String category = rules[b].getTitle();
                        if ( category == null ) {
                            category = rules[b].getName();
                        }
                        if ( category == null ) {
                            category = getPropertyNameFromFilter(f);
                        }
                        le = new LegendElement(new Rule[] { rules[b] }, category, 
                                                    0, 4, true, width, height);
                        lec.addLegendElement(le);
                    } else {
                        String category = ((UserStyle) style).getTitle();
                        if ( category == null ) {
                            category = ((UserStyle) style).getName();
                        }
                        if ( category == null ) {
                            category = title;
                        }                   
                        category = "";
                        le = new LegendElement(rules, category, 0, 4, true, width, height);
                    }
                }
            }

            if (lec.getSize() >= 1) {
                lec.setTitle(getLegendTitle());
                return lec;
            }
            return le;
        }
        throw new LegendException(
                "LegendFactory: Error in creating the LegendElement:\n"
                        + "Given style is not a valid UserStyle.");
    }

    /**
     * creates an empty <tt>LegendElementCollection</tt>
     * 
     * @return <tt>LegendElementCollection</tt>
     */
    public LegendElementCollection createLegendElementCollection() {
        return new LegendElementCollection();
    }

    /**
     * creates a <tt>LegendElementCollection</tt> and fills it with the passed
     * <tt>LegendElement</tt>s.
     * 
     * @return <tt>LegendElementCollection</tt>
     */
    public LegendElementCollection createLegendElementCollection(
            LegendElement[] legendElements) {
        LegendElementCollection lec = new LegendElementCollection();

        for (int i = 0; i < legendElements.length; i++) {
            lec.addLegendElement(legendElements[i]);
        }
        return lec;
    }

    /**
     * 
     * @param sld
     * @param width
     * @param height
     * @return @throws
     *         LegendException
     */
    public BufferedImage[] createAllThumbnails(StyledLayerDescriptor sld,
                                               int width, int height, 
                                               String mime) throws LegendException {

        ArrayList list = new ArrayList();

        org.deegree.graphics.sld.AbstractLayer[] nl = sld.getNamedLayers();
        for (int i = 0; i < nl.length; i++) {
            AbstractStyle[] styles = nl[i].getStyles();
            for (int j = 0; j < styles.length; j++) {
                if (styles[j] instanceof UserStyle) {
                    list.add(styles[j]);
                }
            }
        }

        nl = sld.getUserLayers();
        for (int i = 0; i < nl.length; i++) {
            AbstractStyle[] styles = nl[i].getStyles();
            for (int j = 0; j < styles.length; j++) {
                if (styles[j] instanceof UserStyle) {
                    list.add(styles[j]);
                }
            }
        }

        LegendElement le = null;
        BufferedImage bi_temp = null; // just temporary
        BufferedImage[] buffi = new BufferedImage[list.size()]; // @return

        for (int i = 0; i < list.size(); i++) {
            AbstractStyle style = (AbstractStyle) list.get(i);
            String name = style.getName();
            name = name.replace(':', '_');
            LOG.logInfo( "creating: " + name );
            le = createLegendElement(style, width, height, "");
            bi_temp = le.exportAsImage(mime);
            buffi[i] = bi_temp;
        }

        return buffi;
    }

    /**
     * gets the property-names for creating the legend text
     */
    private String getPropertyNameFromFilter(Filter filter)
            throws LegendException {
        ComplexFilter cf = (ComplexFilter) filter;

        LOG.logDebug( "Name der Operation: " + cf.getOperation().getOperatorName() + 
                      "\n" + cf.toXML() ); 
        Operation operation = cf.getOperation();
        String ret = getPropertyNameFromOperation(operation);
        return ret;

    }

    /**
     * 
     * @param operation
     * @return @throws
     *         LegendException
     */
    private String getPropertyNameFromOperation(Operation operation)
            throws LegendException {

        String legendlabel = "";

        // determines the operation
        // IS COM
        if (operation instanceof PropertyIsCOMPOperation) {
            PropertyIsCOMPOperation pCOMPo = (PropertyIsCOMPOperation) operation;
            // gets the PropertyName of the operation for creating a legendtitle
            if (pCOMPo.getFirstExpression() instanceof PropertyName) {
                PropertyName propertyname = (PropertyName) pCOMPo
                        .getFirstExpression();
                // setLegendTitleFilterProperty(propertyname.getValue());
                legendlabel += propertyname.getValue();
            } else {
                throw new LegendException(
                        "LegendElement_Impl: An error occured "
                                + "during the parsing of the Filter in the SLD."
                                + "First Operation Expression is not of type Literal");
            }
            legendlabel += getOperationString(pCOMPo.getOperatorId());
            // gets the Literal of the operation
            if (pCOMPo.getSecondExpression() instanceof Literal) {
                Literal literal = (Literal) pCOMPo.getSecondExpression();
                legendlabel += literal.getValue();
            } else {
                throw new LegendException(
                        "LegendElement_Impl: An error occured "
                                + "during the parsing of the Filter in the SLD."
                                + "Second Operation Expression is not of type Literal");
            }
            // LOGICAL
        } else if (operation instanceof LogicalOperation) {
            LogicalOperation logOp = (LogicalOperation) operation;
            String operatorstring = getOperationString(logOp.getOperatorId());

            // Operator-ID: AND = 200, OR = 201, NOT = 202
            if (logOp.getOperatorId() == OperationDefines.AND) {
                List<Operation> andlist = logOp.getArguments();
                String andstring = "";
                for (int i = 0; i < andlist.size(); i++) {
                    andstring += getPropertyNameFromOperation( andlist.get(i) );
                    if (i < andlist.size() - 1) {
                        andstring += operatorstring;
                    }
                }
                legendlabel = andstring;
            } else if (logOp.getOperatorId() == OperationDefines.OR) {
                List<Operation> orlist = logOp.getArguments();
                String orstring = "";
                for (int i = 0; i < orlist.size(); i++) {
                    orstring += getPropertyNameFromOperation( orlist.get(i) );
                    if (i < orlist.size() - 1) {
                        orstring += operatorstring;
                    }
                }
                legendlabel = orstring;
            } else if (logOp.getOperatorId() == OperationDefines.NOT) {
                List<Operation> notlist = logOp.getArguments();
                String notstring = getPropertyNameFromOperation( notlist.get(0) );
                // not is followed by brackets: not (ID = 1 and ID = 2)
                legendlabel = operatorstring + "(" + notstring + ")";
            }

            // SPATIAL
        } else if (operation instanceof SpatialOperation) {

            SpatialOperation spatop = (SpatialOperation) operation;

            legendlabel = "spatial operation" + spatop;
            // PROPERTY IS LIKE
        } else if (operation instanceof PropertyIsLikeOperation) {

            PropertyIsLikeOperation prilop = (PropertyIsLikeOperation) operation;

            legendlabel = prilop.getPropertyName().getValue()
                    + getOperationString(prilop.getOperatorId())
                    + prilop.getLiteral().getValue();
            // LOGICAL
        } else {
            LOG.logWarning( operation.toString() );
            // TODO implement other filter-operations and ELSE!
            throw new LegendException("Filter-Operation <"
                    + operation.getOperatorName()
                    + "> is no PropertyIsCOMPOperation.");
        }

        return legendlabel;

    }

    /**
     * 
     * @param operationID
     * @return
     */
    private String getOperationString(int operationID) {
        String operationString = "";

        switch (operationID) {
            case OperationDefines.PROPERTYISEQUALTO:
                operationString = " = ";
                break;
            case OperationDefines.PROPERTYISLESSTHAN:
                operationString = " < ";
                break;
            case OperationDefines.PROPERTYISGREATERTHAN:
                operationString = " > ";
                break;
            case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
                operationString = " <= ";
                break;
            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
                operationString = " >=  ";
                break;
            case OperationDefines.PROPERTYISLIKE:
                operationString = " is like ";
                break;
            case OperationDefines.PROPERTYISNULL:
                operationString = " is NULL ";
                break;
            case OperationDefines.PROPERTYISBETWEEN:
                operationString = " is between ";
                break;
            case OperationDefines.AND:
                operationString = " and ";
                break;
            case OperationDefines.OR:
                operationString = " or ";
                break;
            case OperationDefines.NOT:
                operationString = " not ";
                break;
        }

        return operationString;
    }

    /**
     * sets the label of the <tt>LegendElement</tt>
     * 
     * @param label
     *            label of the <tt>LegendElement</tt>
     * 
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * returns the label set to <tt>LegendElement</tt>. If no label is set,
     * the method returns <tt>null</tt>
     * 
     * @return label of the <tt>LegendElement</tt> or <tt>null</tt>
     * 
     * @uml.property name="label"
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @return
     */
    protected String getLegendTitle() {
        return this.legendtitle;
    }

    /**
     * @param string
     */
    private void setLegendTitle(String string) {
        this.legendtitle = string;
    }

    /**
     * @return 
     * private String getLegendTitleFilterProperty() { return
     * legendtitlefilterproperty; }
     */

    /**
     * @param string
     * 
     * private void setLegendTitleFilterProperty(String string) {
     * legendtitlefilterproperty = string; }
     */

}

/* ******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * LegendFactory.java,v $ Revision 1.2 2004/07/09 07:45:40 ap no message
 * 
 * Revision 1.1 2004/06/24 06:22:06 ap no message
 * 
 * Revision 1.5 2004/06/07 13:39:12 tf code adapted to wfs1 refactoring
 * 
 * Revision 1.4 2004/05/27 06:39:45 ap no message
 * 
 * Revision 1.3 2004/05/24 15:48:45 ap no message
 * 
 * Revision 1.2 2004/05/24 07:11:45 ap no message
 * 
 * Revision 1.1 2004/05/22 10:02:55 ap no message
 * 
 * Revision 1.14 2004/05/14 07:45:59 poth no message
 * 
 * Revision 1.13 2004/04/07 10:58:46 axel_schaefer bugfix
 *  
 ******************************************************************************/