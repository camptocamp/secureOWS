//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/util/Attic/QuadTreeSplitter.java,v 1.4 2006/12/04 17:06:43 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wpvs.util;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.vecmath.Vector3d;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

/**
 * <p>
 * The <code>QuadTreeSplitter</code> class can be used to create x-y axis alligned request quads
 * from a qiven List of {@link ResolutionStripe} s. These Stripes depend on the ViewFrustrum and
 * it's projection on the x-y plane (the so called footprint). To create an approximation of this
 * footprint a Quadtree (a geometric spatial structure, which recursively divides a boundingbox into
 * four containing boundingboxes) is built. The leafs of this tree are merged according to their
 * resolution and size to create the requeststripes.
 * </p>
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/12/04 17:06:43 $
 * 
 */

public class QuadTreeSplitter {

    private static final ILogger LOG = LoggerFactory.getLogger( QuadTreeSplitter.class );

    private QuadNode rootNode;

    private ArrayList<ResolutionStripe> resolutionStripes;

    private double minimalTerrainHeight;

    private double minimalResolution;

    private CoordinateSystem crs;

    private double imageWidth;

    private double imageHeight;

    private boolean highQuality;

    /**
     * Creates a new Quadtree, from the given resolutionstripes. The resulting tree can be used to
     * generate requeststripes which are (x-y) axis aligned.
     * <p>
     * The Quality argument is used for the recursive termination criteria, if it is set to true the
     * requeststripes will accurately approximate the footprint (=projection of the viewfrustrum
     * onto the ground) and the resolutions given in the resolutionstripes this results in a lot of
     * requests which can slow down the wpvs. If set to false the footprint and the given
     * resolutions will be approximated poorly but only a few requeststripes are created, resulting
     * in a faster wpvs.
     * </p>
     * 
     * @param resolutionStripes
     *            the different resolutionstripes.
     * @param imageWidth
     *            the width of the target image, necessary for calculating the width resolution of
     *            the requeststripe.
     * @param imageHeight
     *            the height of the target image, necessary for calculating the height resolution of
     *            the requeststripe.
     * @param highQuality
     *            true if accurate (but many) requeststripes should be generated, false if none
     *            accurate (but only a few) requests should be generated.
     * @param g2d
     *            for debugging -> TODO delete
     */
    public QuadTreeSplitter( ArrayList<ResolutionStripe> resolutionStripes, double imageWidth,
                            double imageHeight, boolean highQuality, Graphics2D g2d ) {
        if ( resolutionStripes == null && resolutionStripes.size() <= 0 )
            return;
        this.resolutionStripes = resolutionStripes;
        this.crs = resolutionStripes.get( 0 ).getCRSName() ;
        this.minimalTerrainHeight = resolutionStripes.get( 0 ).getMinimalTerrainHeight();
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.highQuality = highQuality;
        // For the merge, the check is newmin < existing_min therefore -> min large and max small
        Position minPos = GeometryFactory.createPosition( 0, 0, 0 );
        Position maxPos = GeometryFactory.createPosition( 0, 0, 0 );
        Envelope bbox = GeometryFactory.createEnvelope( minPos, maxPos, this.crs );
        minimalResolution = Double.MAX_VALUE;
        double maxResolution = Double.MIN_VALUE;
        // find the highest and loweset maxResolution (which are needed for termination decissions
        // and
        // find create the axis-alligned bbox of the resolutionstripes which will be the rootnode.
        for ( int i = 0; i < resolutionStripes.size(); ++i ) {
            try {
                bbox = bbox.merge( resolutionStripes.get( i ).getSurface().getEnvelope() );
                // minimalResolution is the smallest number
                minimalResolution = Math.min( minimalResolution,
                                              resolutionStripes.get( i ).getMinResolution() );
                maxResolution = Math.max( maxResolution,
                                          resolutionStripes.get( i ).getMaxResolution() );
            } catch ( GeometryException e ) {
                e.printStackTrace();
                System.out.println( e.getLocalizedMessage() );
            }
        }
        try {
            if ( Math.abs( minimalResolution ) < 0.00001 ) // almost null
                minimalResolution = Math.pow( maxResolution, 1.0 / resolutionStripes.size() );
            Position min = bbox.getMin();

            double zValue = min.getZ();
            if ( new Double( min.getZ() ).equals( Double.NaN ) )
                zValue = 0;

            Vector3d leftPoint = new Vector3d( min.getX(), min.getY(), zValue );
            Vector3d rightPoint = new Vector3d( min.getX() + ( bbox.getWidth() ),
                                                min.getY() + ( bbox.getHeight() ), zValue );
            double currentResolution = StripeFactory.calcScaleOfVector( leftPoint, rightPoint,
                                                                        imageWidth );

            rootNode = new QuadNode( GeometryFactory.createSurface( bbox, crs ), maxResolution,
                                     minimalResolution );

            createTree( rootNode, currentResolution, g2d );

        } catch ( GeometryException e ) {
            e.printStackTrace();
            System.out.println( e.getLocalizedMessage() );
        }
    }

    /**
     * After instantiating a Quadtree, this method can be called to build the (x-y) axis-alligned
     * request stripes.
     * 
     * @param g2d
     *            TODO just for debugging to be removed.
     * @return the (x-y) axis-alligned request squares best fitted the given resolutionstripes.
     */
    public ArrayList<ResolutionStripe> getRequestQuads( Graphics2D g2d ) {

        ArrayList<ResolutionStripe> resultList = new ArrayList<ResolutionStripe>();
        if ( rootNode != null ) {
            LinkedHashMap<Integer, ArrayList<QuadNode>> lhm = new LinkedHashMap<Integer, ArrayList<QuadNode>>(
                                                                                                               100 );
            outputNodes( rootNode, lhm );
            Set<Integer> keys = lhm.keySet();
            for ( Integer resolution : keys ) {

                if ( lhm.containsKey( resolution ) ) {
                    ArrayList<QuadNode> originalNodes = lhm.get( resolution );
                    ArrayList<QuadNode> result = new ArrayList<QuadNode>( originalNodes.size() / 2 );
                    // sorted to x values first.
                    ArrayList<QuadNode> resultX = new ArrayList<QuadNode>( result.size() );
                    boolean resort = mergeAndSort( originalNodes, resultX );
                    while ( resort ) {
                        result.clear();
                        result.addAll( resultX );
                        resultX.clear();
                        resort = mergeAndSort( result, resultX );
                    }
                    // Check if sorting to y results in better values;
                    for ( QuadNode node : originalNodes ) {
                        node.compareY();
                    }
                    ArrayList<QuadNode> resultY = new ArrayList<QuadNode>();
                    resort = mergeAndSort( originalNodes, resultY );
                    while ( resort ) {
                        result.clear();
                        result.addAll( resultY );
                        resultY.clear();
                        resort = mergeAndSort( result, resultY );
                    }
                    result.clear();
                    // Find the optimal sorting order (lesser quads) and check if the perpendicular
                    // order results in lesser requeststripes (it usually does)
                    if ( resultX.size() < resultY.size() ) {
                        for ( QuadNode node : resultX ) {
                            node.compareY();
                        }
                        while ( mergeAndSort( resultX, result ) ) {
                            resultX.clear();
                            resultX.addAll( result );
                            result.clear();
                        }
                    } else {
                        for ( QuadNode node : resultY ) {
                            node.compareX();
                        }
                        while ( mergeAndSort( resultY, result ) ) {
                            resultY.clear();
                            resultY.addAll( result );
                            result.clear();
                        }
                    }
                    for ( QuadNode quad : result ) {
                        Position envMin = quad.getBBox().getEnvelope().getMin();
                        Position envMax = quad.getBBox().getEnvelope().getMax();

                        Vector3d minVec = new Vector3d( envMin.getX(), envMin.getY(), envMin.getZ() );
                        Vector3d maxVec = new Vector3d( envMax.getX(), envMin.getY(), envMin.getZ() );
                        
                        double maxResolution = StripeFactory.calcScaleOfVector( minVec, maxVec,
                                                                                imageWidth );
                        double minResolution = maxResolution;
                        if( maxResolution < 1 ){
                            maxResolution = 1;
                        }
                        ResolutionStripe rs = new ResolutionStripe( quad.getBBox(), maxResolution,
                                                                    minResolution,
                                                                    minimalTerrainHeight );

//                        minVec = new Vector3d( envMin.getX(), envMin.getY(), envMin.getZ() );
//                        maxVec = new Vector3d( envMin.getX(), envMax.getY(), envMin.getZ() );
//                        maxResolution = StripeFactory.calcScaleOfVector( minVec, maxVec,
//                                                                         imageHeight );
                        //rs.setMaxHeightResolution( maxResolution );
                        resultList.add( rs );

                    }
                }
            }
        }
        if ( g2d != null ) {
            for ( ResolutionStripe stripe : resultList ) {
                drawSquare( new QuadNode( stripe.getSurface(), stripe.getMaxResolution(),
                                          stripe.getMinResolution() ), g2d, Color.MAGENTA );
                // System.out.println( stripe.toWKT() );
            }
        }
        return resultList;
    }

    /**
     * A little helper function which first sorts the given list of quadnodes according to their
     * sorting order and afterwards merges all stripes which are adjacent and have the same
     * resolution.
     * 
     * @param toBeMerged the list of Quadnodes which must be sorted and merged.
     * @param resultList the list containing the  merged quadnodes.
     * @return true if the list should be rechecked (that is, if one or more merge(s) took place)
     */
    private boolean mergeAndSort( ArrayList<QuadNode> toBeMerged, ArrayList<QuadNode> resultList ) {
        Collections.sort( toBeMerged );
        Iterator<QuadNode> it = toBeMerged.iterator();
        QuadNode first = null;
        QuadNode second = null;
        boolean needsResort = false;
        resultList.clear();
        while ( second != null || it.hasNext() ) {
            if ( second == null ) {
                first = it.next();
            } else {
                first = second;
                second = null;
            }
            Envelope requestEnvelope = first.getBBox().getEnvelope();
            while ( it.hasNext() && second == null ) {
                second = it.next();
                if ( first.canMerge( second ) ) {
                    try {
                        requestEnvelope = requestEnvelope.merge( second.getBBox().getEnvelope() );
                    } catch ( GeometryException ge ) {
                        // An error occured, it might be best to not merge these envelopes.
                        LOG.logError( ge.getLocalizedMessage(), ge );
                    }
                    second = null;
                    needsResort = true;
                }
            }
            Surface resultSurface = null;
            try {
                resultSurface = GeometryFactory.createSurface( requestEnvelope, crs );
            } catch ( GeometryException ge ) {
                // An error occured, it might be best not to merge these envelopes.
                LOG.logError( ge.getLocalizedMessage(), ge );
            }
            resultList.add( new QuadNode( resultSurface, first.getMaxResolution(),
                                          first.getMinResolution(), first.isComparingX() ) );
        }
        return needsResort;
    }

    /**
     * This Method actually builds the tree. The decission of a split is made by evaluating the
     * minimal maxResolution of the intersecting ResultionStripe.
     * 
     * @param father
     *            the Father node which will be splittet into four axis aligned sons
     * @param fatherResolution
     *            the maxResolution of a width axis of the axis-aligned bbox
     * @param g2d
     *            just for debugging-> TODO must be deleted
     * @throws GeometryException
     *             if the Envelope cannot be created
     */
    private void createTree( QuadNode father, double fatherResolution, Graphics2D g2d )
                            throws GeometryException {
        Position min = father.getBBox().getEnvelope().getMin();
        double widthHalf = 0.5 * father.getBBox().getEnvelope().getWidth();
        double heightHalf = 0.5 * father.getBBox().getEnvelope().getHeight();
        double lowerLeftX = min.getX();
        double lowerLeftY = min.getY();
        double currentResolution = 0.5 * fatherResolution;
        // no more recursion.
        if ( currentResolution < minimalResolution )
            return;

        checkSon( father, currentResolution, QuadNode.LOWER_LEFT_SON, lowerLeftX, lowerLeftY,
                  lowerLeftX + widthHalf, lowerLeftY + heightHalf, g2d );

        // lowerright
        checkSon( father, currentResolution, QuadNode.LOWER_RIGHT_SON, lowerLeftX + widthHalf,
                  lowerLeftY, lowerLeftX + ( 2 * widthHalf ), lowerLeftY + heightHalf, g2d );

        // upperleft
        checkSon( father, currentResolution, QuadNode.UPPER_LEFT_SON, lowerLeftX, lowerLeftY
                                                                                  + heightHalf,
                  lowerLeftX + widthHalf, lowerLeftY + ( 2 * heightHalf ), g2d );

        // upperright
        checkSon( father, currentResolution, QuadNode.UPPER_RIGHT_SON, lowerLeftX + widthHalf,
                  lowerLeftY + heightHalf, lowerLeftX + 2 * widthHalf, lowerLeftY + 2 * heightHalf,
                  g2d );
    }

    /**
     * Decides if the father quad has to be subdivided into it's sons.
     * 
     * @param father
     *            the Father quad to divide
     * @param maxResolution
     *            the maxResolution of the fathers son (half the maxResolution of the father)
     * @param quadArea
     *            the area of a son of the son (1/16 of the fathers area)
     * @param SON_ID
     *            the son to check
     * @param lowerLeftX
     *            minx of the bbox of the fathers son
     * @param lowerLeftY
     *            miny of the bbox of the fathers son
     * @param upperRightX
     *            maxx of the bbox of the fathers son
     * @param upperRightY
     *            maxY of the bbox of the fathers son
     * @param g2d
     *            for debugging -> TODO delete
     * @throws GeometryException
     *             if no surface can be created
     */
    private void checkSon( QuadNode father, double resolution, final int SON_ID, double lowerLeftX,
                          double lowerLeftY, double upperRightX, double upperRightY, Graphics2D g2d )
                            throws GeometryException {
        Position min = GeometryFactory.createPosition(
                                                       lowerLeftX,
                                                       lowerLeftY,
                                                       father.getBBox().getEnvelope().getMin().getZ() );
        Position max = GeometryFactory.createPosition(
                                                       upperRightX,
                                                       upperRightY,
                                                       father.getBBox().getEnvelope().getMax().getZ() );
        Surface bbox = GeometryFactory.createSurface(
                                                      GeometryFactory.createEnvelope( min, max, crs ),
                                                      crs );
        ResolutionStripe intersectedStripe = ( highQuality ) ? getIntersectionForQualityConfiguration( bbox )
                                                            : getIntersectionForFastConfiguration( bbox );
        if ( intersectedStripe != null ) { // found an intersecting resolutionStripe
            QuadNode son = new QuadNode( bbox, intersectedStripe.getMaxResolution(),
                                         intersectedStripe.getMinResolution() );
            double sonsResolution = intersectedStripe.getMinResolution();
            father.addSon( SON_ID, son );
            if ( resolution >= sonsResolution ) {
                drawSquare( son, g2d, Color.YELLOW );
                createTree( son, resolution, g2d );
            }
        }
    }

    /**
     * Finds the resolutionstripe with the lowest minResolution which intersects with the given
     * bbox. Resulting in a lot of different requests.
     * 
     * @param bbox
     *            the BoundingBox of the Envelope to check.
     * @return the resolutionStripe which intersects the bbox.
     */
    private ResolutionStripe getIntersectionForQualityConfiguration( Surface bbox ) {
        ResolutionStripe resultStripe = null;
        for ( ResolutionStripe stripe : resolutionStripes ) {
            if ( bbox.intersects( stripe.getSurface() ) ) {
                if ( resultStripe != null ) {
                    if ( ( stripe.getMinResolution() < resultStripe.getMinResolution() ) ) {
                        resultStripe = stripe;
                    }
                } else {
                    resultStripe = stripe;
                }
            }
        }
        return resultStripe;
    }

    /**
     * Finds the resolutionstripe with the highest maxResolution which intersects with the given
     * bbox. Resulting in only a few different requests.
     * 
     * @param bbox
     *            the BoundingBox of the Envelope to check.
     * @return the resolutionStripe which intersects the bbox.
     */
    private ResolutionStripe getIntersectionForFastConfiguration( Surface bbox ) {
        ResolutionStripe resultStripe = null;
        for ( ResolutionStripe stripe : resolutionStripes ) {
            if ( bbox.intersects( stripe.getSurface() ) ) {
                if ( resultStripe != null ) {
                    if ( ( stripe.getMaxResolution() > resultStripe.getMaxResolution() ) ) {
                        resultStripe = stripe;
                    }
                } else {
                    resultStripe = stripe;
                }
            }
        }
        return resultStripe;
    }

    /**
     * Outputs the tree
     * 
     * @param g2d
     *            if the quadtree should be drawn.
     */
    public void outputTree( Graphics2D g2d ) {
        if ( rootNode != null ) {
            if ( g2d != null ) {
                System.out.println( "number Of leaves-> " + outputNodes( rootNode, g2d ) );
            } else {
                outputNodes( rootNode, "" );
            }
        }
    }

    private int outputNodes( QuadNode father, Graphics2D g2d ) {
        if ( father.isLeaf() ) {
            drawSquare( father, g2d, Color.BLACK );
            return 1;
        }
        QuadNode[] nodes = father.getSons();
        int result = 0;
        for ( QuadNode node : nodes ) {
            if ( node != null ) {
                result += outputNodes( node, g2d );
            }

        }
        return result;
    }

    private void outputNodes( QuadNode father, String indent ) {
        if ( father.isLeaf() ) {
            System.out.println( indent + "(father)" + father.getBBox() );
        } else {
            QuadNode[] nodes = father.getSons();
            for ( QuadNode node : nodes ) {
                if ( node != null ) {
                    indent += "-";
                    outputNodes( node, indent );
                }
            }
        }
    }

    /**
     * Find the leaf nodes and add them according to their maxResolution in a LinkedHashMap.
     * 
     * @param father
     *            the node to check
     * @param outputMap
     *            the map to output to.
     */
    private void outputNodes( QuadNode father, LinkedHashMap<Integer, ArrayList<QuadNode>> outputMap ) {
        if ( father.isLeaf() ) {
            Integer key = new Integer( (int) Math.floor( father.getMaxResolution() ) );
            ArrayList<QuadNode> ts = outputMap.get( key );
            if ( ts == null ) { // I know, but I don't put null values so it's ok
                ts = new ArrayList<QuadNode>();
                outputMap.put( key, ts );
            }
            if ( ts.add( father ) == false ) {
                System.out.println( "quadnode allready in set" );
            }
        } else {
            QuadNode[] nodes = father.getSons();
            for ( QuadNode node : nodes ) {
                if ( node != null ) {
                    outputNodes( node, outputMap );
                }
            }
        }
    }

    private void drawSquare( QuadNode node, Graphics2D g2d, Color c ) {
        if ( g2d != null ) {
            g2d.setColor( c );
            Envelope env = node.getBBox().getEnvelope();
            Position min = env.getMin();
            int height = (int) env.getHeight();
            int width = (int) env.getWidth();
            g2d.drawRect( (int) min.getX(), (int) min.getY(), width, height );
            Composite co = g2d.getComposite();
            g2d.setColor( new Color( c.getRed(), c.getGreen(), c.getBlue(), 64 ) );
            g2d.fillRect( (int) min.getX(), (int) min.getY(), width, height );
            g2d.setComposite( co );
        }
    }

    /**
     * 
     * The <code>QuadNode</code> class is the bean for every node of the quadtree. It contains a
     * axis-aligned BBox and the maxResolution of its associated resolutionStripe. It can have upto
     * four sons.
     * 
     * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
     * 
     * @author last edited by: $Author: bezema $
     * 
     * @version $Revision: 1.4 $, $Date: 2006/12/04 17:06:43 $
     * 
     */
    private class QuadNode implements Comparable<QuadNode> {

        private QuadNode[] sons;

        private Surface bbox;

        private double maxResolution;

        private double minResolution;

        private double comparePosition;

        private double compareLength;

        private boolean comparingX;

        static final int LOWER_LEFT_SON = 0;

        static final int LOWER_RIGHT_SON = 1;

        static final int UPPER_LEFT_SON = 2;

        static final int UPPER_RIGHT_SON = 3;

        private QuadNode( Surface bbox, double maxResolution, double minResolution ) {
            this.bbox = bbox;
            sons = new QuadNode[4];
            this.maxResolution = maxResolution;
            this.minResolution = minResolution;
            comparePosition = bbox.getEnvelope().getMin().getX();
            compareLength = bbox.getEnvelope().getWidth();
            comparingX = true;
        }

        private QuadNode( Surface bbox, double maxResolution, double minResolution,
                         boolean compareDirection ) {
            this( bbox, maxResolution, minResolution );
            if ( compareDirection )
                compareX();
            else
                compareY();
        }

        void addSon( final int sonID, QuadNode son ) {
            if ( sonID == LOWER_LEFT_SON || sonID == LOWER_RIGHT_SON || sonID == UPPER_LEFT_SON
                 || sonID == UPPER_RIGHT_SON ) {
                this.sons[sonID] = son;
            }
        }

        Surface getBBox() {
            return bbox;
        }

        void compareX() {
            comparePosition = bbox.getEnvelope().getMin().getX();
            compareLength = bbox.getEnvelope().getWidth();
            comparingX = true;
        }

        void compareY() {
            comparePosition = bbox.getEnvelope().getMin().getY();
            compareLength = bbox.getEnvelope().getHeight();
            comparingX = false;
        }

        /**
         * If this Quadnode has no sons it is called a leaf.
         * 
         * @return true if no sons, false otherwhise.
         */
        boolean isLeaf() {
            return ( sons[0] == null && sons[1] == null && sons[2] == null && sons[3] == null );
        }

        QuadNode[] getSons() {
            return sons;
        }

        QuadNode getSon( final int sonID ) {
            if ( sonID != LOWER_LEFT_SON || sonID != LOWER_RIGHT_SON || sonID != UPPER_LEFT_SON
                 || sonID != UPPER_RIGHT_SON )
                return null;
            return sons[sonID];
        }

        /**
         * @return The max maxResolution of the Stripe.
         */
        double getMaxResolution() {
            return maxResolution;
        }

        /**
         * @return the minResolution value.
         */
        double getMinResolution() {
            return minResolution;
        }

        boolean isComparingX() {
            return comparingX;
        }

        double getComparePosition() {
            return comparePosition;
        }

        double getCompareLength() {
            return compareLength;
        }

        /*
         * Attention, the equal result "0" is not really a check for the equality of two Quadnodes,
         * it just reflex, that two QuadNodes have the same sorting properties -> the position - (y
         * or x) and the length in this direction are equal. It is very plausible that they have
         * totally different positions and length in the other (not checked) direction.
         * 
         * @see java.lang.Comparable#compareTo(T)
         */
        public int compareTo( QuadNode other ) {
            double otherPosition = other.getComparePosition();
            if ( Math.abs( comparePosition - otherPosition ) < 0.00001 ) {
                double otherLength = other.getCompareLength();
                if ( Math.abs( compareLength - otherLength ) < 0.00001 ) {
                    if ( comparingX ) {
                        double thisMinY = this.bbox.getEnvelope().getMin().getY();
                        double otherMinY = other.getBBox().getEnvelope().getMin().getY();
                        if ( ( Math.abs( thisMinY - otherMinY ) < 0.00001 ) )
                            return 0;
                        if ( thisMinY < otherMinY )
                            return 1;
                        return -1;
                    }
                    double thisMinX = this.bbox.getEnvelope().getMin().getX();
                    double otherMinX = other.getBBox().getEnvelope().getMin().getX();
                    if ( ( Math.abs( thisMinX - otherMinX ) < 0.00001 ) )
                        return 0;
                    if ( thisMinX < otherMinX )
                        return 1;
                    return -1;
                }
                if ( compareLength < otherLength ) {
                    return -1;
                }
                return 1;
            }
            if ( comparePosition < otherPosition )
                return -1;
            return 1;
        }

        /**
         * simple check if two quadnodes can be merged, according to their positions, length and if
         * they are adjacent.
         * 
         * @param other
         * @return true if this QuadNode can be merged with the Other.
         */
        boolean canMerge( QuadNode other ) {
            double otherPosition = other.getComparePosition();
            if ( Math.abs( comparePosition - otherPosition ) < 0.01 ) {
                double otherLength = other.getCompareLength();
                if ( Math.abs( compareLength - otherLength ) < 0.01 ) {
                    // the origins and the length are mergable, now check if the Quadnodes are
                    // adjacent
                    if ( comparingX ) {
                        double thisMaxY = this.bbox.getEnvelope().getMax().getY();
                        double thisMinY = this.bbox.getEnvelope().getMin().getY();
                        double otherMinY = other.getBBox().getEnvelope().getMin().getY();
                        double otherMaxY = other.getBBox().getEnvelope().getMax().getY();
                        if ( ( Math.abs( thisMaxY - otherMinY ) < 0.00001 )
                             || ( Math.abs( thisMinY - otherMaxY ) < 0.00001 ) ) {
                            return true;
                        }
                    } else {

                        double thisMaxX = this.bbox.getEnvelope().getMax().getX();
                        double thisMinX = this.bbox.getEnvelope().getMin().getX();
                        double otherMinX = other.getBBox().getEnvelope().getMin().getX();
                        double otherMaxX = other.getBBox().getEnvelope().getMax().getX();
                        if ( ( Math.abs( thisMaxX - otherMinX ) < 0.00001 )
                             || ( Math.abs( thisMinX - otherMaxX ) < 0.00001 ) ) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "QuadNode sorted in Direction: " + ( ( comparingX ) ? "x" : "y" )
                   + " comparePosition: " + comparePosition + " compareLength: " + compareLength;
        }

    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: QuadTreeSplitter.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/12/04 17:06:43  bezema
 * Changes to this class. What the people have been up to: enhanced dgm from wcs support
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/11/27 15:43:11  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/

