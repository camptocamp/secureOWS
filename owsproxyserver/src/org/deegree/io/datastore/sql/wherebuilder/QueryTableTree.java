//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/QueryTableTree.java,v 1.22 2006/09/20 11:35:41 mschneider Exp $
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
 Aennchenstraße 19
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
package org.deegree.io.datastore.sql.wherebuilder;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.ConstantContent;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.ogcbase.AttributeStep;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathStep;

/**
 * Represents {@link PropertyPath} instances (properties used in an OGC filter and as sort
 * criteria) and their mapping to a certain relational schema.
 * <p>
 * Encapsulates the associated {@link MappedFeatureType} and the corresponding table name. Also
 * contains joined tables; joined tables represent selected complex (feature) properties. Joined
 * tables are {@link QueryTableTree} instances themselves, so the whole structure is recursive and
 * forms a tree.
 * <p>
 * Every join to a table (=subfeature type) is discriminated using the
 * {@link MappedFeaturePropertyType}. This is necessary, because a feature type may contain
 * several instances of one subfeature type (in different properties). This also implies that it
 * is sometimes necessary to join a table to another table more than once, but with different
 * (unique) aliases.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.22 $, $Date: 2006/09/20 11:35:41 $
 */
public class QueryTableTree {

    private static final ILogger LOG = LoggerFactory.getLogger( QueryTableTree.class );

    private TableAliasGenerator aliasGenerator;

    private FeatureTypeNode root;

    private MappedGMLSchema schema;

    // uses 2 lists instead of Map, because PropertyPath.equals() is overwritten,
    // and identity (==) is needed here (different occurences of "equal" PropertyName
    // in filter must be treated as different PropertyPaths)
    private List<PropertyPath> propertyPaths = new ArrayList<PropertyPath>();

    private List<PropertyNode> propertyNodes = new ArrayList<PropertyNode>();

    /**
     * Creates a new <code>QueryTableTree</code>.
     * 
     * @param featureType
     * @param aliasGenerator
     */
    public QueryTableTree( MappedFeatureType featureType, TableAliasGenerator aliasGenerator ) {

        if ( aliasGenerator != null ) {
            this.aliasGenerator = aliasGenerator;
        } else {
            this.aliasGenerator = new TableAliasGenerator();
        }
        this.schema = featureType.getGMLSchema();
        this.root = new FeatureTypeNode( featureType, aliasGenerator.generateUniqueAlias() );
    }

    /**
     * Returns the root feature type node of the tree.
     *
     * @return the root feature type node of the tree
     */
    public FeatureTypeNode getRootNode() {
        return this.root;
    }

    /**
     * Returns the alias for the root table.
     * 
     * @return the alias for the root table
     */
    public String getRootAlias() {
        return this.root.getAlias();
    }

    /**
     * Returns the property node for the given property path.
     * 
     * @param path
     *            property to be looked up
     * @return the property node for the given property path
     */
    public PropertyNode getPropertyNode( PropertyPath path ) {

        PropertyNode node = null;
        for ( int i = 0; i < this.propertyPaths.size(); i++ ) {
            if ( this.propertyPaths.get( i ) == path ) {
                node = this.propertyNodes.get( i );
                break;
            }
        }
        return node;
    }

    /**
     * Tries to insert the given {@link PropertyPath} as a filter criterion into the query tree.
     * <p>
     * The {@link PropertyPath} is validated during insertion.
     *
     * @param property
     *            property to be inserted, has to have at least one step
     * @throws PropertyPathResolvingException
     *            if the path violates the feature type's schema
     */
    public void addFilterProperty( PropertyPath property )
                            throws PropertyPathResolvingException {
        MappedPropertyType pt = validate( property, false );
        if ( pt instanceof MappedSimplePropertyType ) {
            SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
            if ( content instanceof ConstantContent ) {
                // add SimplePropertyNode to root node (because table path is irrelevant)
                String[] tableAliases = generateTableAliases( pt );
                PropertyNode propertyNode = new SimplePropertyNode( (MappedSimplePropertyType) pt,
                                                                    root, tableAliases );
                this.propertyPaths.add( property );
                this.propertyNodes.add( propertyNode );
                //root.addPropertyNode( propertyNode );
            } else {
                insert( property );
            }
        } else {
            insert( property );
        }
    }

    /**
     * Tries to insert the given {@link PropertyPath} as a sort criterion into the tree.
     * <p>
     * The {@link PropertyPath} is validated during insertion. It is also checked that the
     * path is unique, i.e. every property type on the path must have maxOccurs set to 1.
     *
     * @param property
     *            property to be inserted, has to have at least one step
     * @throws PropertyPathResolvingException
     *            if the path violates the feature type's schema
     */
    public void addSortProperty( PropertyPath property )
                            throws PropertyPathResolvingException {
        MappedPropertyType pt = validate( property, false );
        if ( pt instanceof MappedSimplePropertyType ) {
            SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
            if ( content.isSortable() ) {
                insert( property );
            } else {
                String msg = "Skipping property '" + property + "' as sort criterion.";
                LOG.logDebug( msg );
                // add SimplePropertyNode to root node (because table path is irrelevant)
                String[] tableAliases = generateTableAliases( pt );
                PropertyNode propertyNode = new SimplePropertyNode( (MappedSimplePropertyType) pt,
                                                                    root, tableAliases );
                this.propertyPaths.add( property );
                this.propertyNodes.add( propertyNode );
                //root.addPropertyNode( propertyNode );       
            }
        } else {
            String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_SORT1", property );
            throw new PropertyPathResolvingException( msg );
        }
    }

    /**
     * Validates the {@link PropertyPath} against the {@link MappedGMLSchema} and returns the
     * {@link MappedPropertyType} that the paths ends with.
     *
     * @param propertyPath
     *            PropertyPath to be validated, has to have at least one step
     * @param forceUniquePath
     *            if set to true, an exeption is thrown if the path is not unique, i.e. at least
     *            one property on the path has maxOccurs set to a value > 1
     * @return the type of the property that the path ends with
     * @throws PropertyPathResolvingException
     *            if the path violates the feature type's schema
     */
    public MappedPropertyType validate( PropertyPath propertyPath, boolean forceUniquePath )
                            throws PropertyPathResolvingException {

        MappedPropertyType propertyType = null;
        MappedFeatureType currentFT = this.root.getFeatureType();

        LOG.logDebug( "Trying to validate '" + propertyPath + "' against schema of feature type '"
                      + currentFT + "'..." );

        int firstPropertyPos = 0;
        QualifiedName elementName = propertyPath.getStep( firstPropertyPos ).getPropertyName();

        // must be the name of the feature type or the name of a property of the feature type
        if ( elementName.equals( currentFT.getName() ) ) {
            LOG.logDebug( "First step matches the name of the feature type." );
            firstPropertyPos++;
        } else {
            LOG.logDebug( "First step does not match the name of the feature type. "
                          + "Must be the name of a property then." );
        }

        for ( int step = firstPropertyPos; step < propertyPath.getSteps(); step += 2 ) {
            LOG.logDebug( "Looking up property: " + propertyPath.getStep( step ).getPropertyName() );
            propertyType = getPropertyType( currentFT, propertyPath.getStep( step ) );

            if ( propertyType == null ) {
                String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_RESOLVE4", propertyPath, step,
                                              propertyPath.getStep( step ), currentFT.getName(),
                                              propertyPath.getStep( step ) );
                throw new PropertyPathResolvingException( msg );
            }

            if ( forceUniquePath ) {
                if ( propertyType.getMaxOccurs() != 1 ) {
                    String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_SORT2", propertyPath, step,
                                                  propertyType.getName() );
                    throw new PropertyPathResolvingException( msg );
                }
            }

            if ( propertyType instanceof MappedSimplePropertyType ) {
                if ( step < propertyPath.getSteps() - 1 ) {
                    String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_RESOLVE1", propertyPath,
                                                  step, propertyType.getName(), "simple" );
                    throw new PropertyPathResolvingException( msg );
                }
            } else if ( propertyType instanceof MappedGeometryPropertyType ) {
                if ( step < propertyPath.getSteps() - 1 ) {
                    String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_RESOLVE1", propertyPath,
                                                  step, propertyType.getName(), "geometry" );
                    throw new PropertyPathResolvingException( msg );
                }
            } else if ( propertyType instanceof MappedFeaturePropertyType ) {
                if ( step == propertyPath.getSteps() - 1 ) {
                    String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_RESOLVE2", propertyPath,
                                                  step, propertyType.getName() );
                    throw new PropertyPathResolvingException( msg );
                }
                MappedFeaturePropertyType pt = (MappedFeaturePropertyType) propertyType;
                FeatureType[] allowedTypes = this.schema.getSubstitutions( pt.getFeatureTypeReference().getFeatureType() );
                QualifiedName givenTypeName = propertyPath.getStep( step + 1 ).getPropertyName();
                MappedFeatureType givenType = null;

                for ( int i = 0; i < allowedTypes.length; i++ ) {
                    if ( allowedTypes[i].getName().equals( givenTypeName ) ) {
                        givenType = (MappedFeatureType) allowedTypes[i];
                        break;
                    }
                }
                if ( givenType == null ) {
                    StringBuffer validTypeList = new StringBuffer();
                    for ( int i = 0; i < allowedTypes.length; i++ ) {
                        validTypeList.append( '\'' );
                        validTypeList.append( allowedTypes[i].getName() );
                        validTypeList.append( '\'' );
                        if ( i != allowedTypes.length - 1 ) {
                            validTypeList.append( ", " );
                        }
                    }
                    String msg = Messages.getMessage( "DATASTORE_PROPERTY_PATH_RESOLVE3", propertyPath,
                                                  step + 1, givenTypeName, validTypeList );
                    throw new PropertyPathResolvingException( msg.toString() );
                }
                currentFT = pt.getFeatureTypeReference().getFeatureType();
            } else {
                assert ( false );
            }
        }
        return propertyType;
    }

    /**
     * Tries to insert the given {@link PropertyPath} into the query tree.
     * <p>
     * The {@link PropertyPath} is validated during insertion.
     *
     * @param propertyPath
     *            PropertyPath to be inserted into the tree, has to have at least one step
     */
    private void insert( PropertyPath propertyPath ) {

        LOG.logDebug( "Trying to insert '" + propertyPath + "' into the query table tree." );

        int firstPropertyPos = 0;
        PropertyPathStep step = propertyPath.getStep( firstPropertyPos );
        QualifiedName elementName = step.getPropertyName();

        // must be the name of the feature type or the name of a property of the feature type
        if ( elementName.equals( root.getFeatureType().getName() ) ) {
            LOG.logDebug( "First step matches the name of the feature type." );
            firstPropertyPos++;
        } else {
            LOG.logDebug( "First step does not match the name of the feature type. "
                          + "Must be the name of a property then." );
        }

        FeatureTypeNode featureTypeNode = root;
        PropertyNode propertyNode = null;
        for ( int i = firstPropertyPos; i < propertyPath.getSteps(); i += 2 ) {

            // check for property with step name in the feature type
            MappedFeatureType featureType = featureTypeNode.getFeatureType();

            elementName = propertyPath.getStep( i ).getPropertyName();
            MappedPropertyType propertyType = getPropertyType( featureType,
                                                               propertyPath.getStep( i ) );

            // check for property node in the feature type node
            propertyNode = featureTypeNode.getPropertyNode( propertyType );
            if ( propertyNode == null || propertyNode.getProperty().getMaxOccurs() != 1 ) {
                addPathFragment( featureTypeNode, propertyPath, i );
                break;
            }

            if ( i + 1 < propertyPath.getSteps() ) {
                // more steps? propertyNode must be a FeaturePropertyNode then
                elementName = propertyPath.getStep( i + 1 ).getPropertyName();
                if ( propertyNode instanceof FeaturePropertyNode ) {
                    FeatureTypeNode[] childFeatureTypeNodes = ( (FeaturePropertyNode) propertyNode ).getFeatureTypeNodes();
                    boolean found = false;
                    for ( int j = 0; j < childFeatureTypeNodes.length; j++ ) {
                        if ( elementName.equals( childFeatureTypeNodes[j].getFeatureType().getName() ) ) {
                            found = true;
                            featureTypeNode = childFeatureTypeNodes[j];
                            break;
                        }
                    }
                    if ( !found ) {
                        // add another feature type node
                        FeaturePropertyNode featurePropertyNode = (FeaturePropertyNode) propertyNode;
                        MappedFeaturePropertyType featurePT = (MappedFeaturePropertyType) propertyType;
                        FeatureTypeNode newFeatureTypeNode = getFeatureTypeNode( featurePT,
                                                                                 propertyPath,
                                                                                 i + 1 );
                        addPathFragment( newFeatureTypeNode, propertyPath, i + 2 );
                        featurePropertyNode.addFeatureTypeNode( newFeatureTypeNode );
                        return;
                    }
                } else {
                    assert ( false );
                }
            } else {
                assert ( false );
            }
        }

        // "equal" path is already registered, map this one to existing instance
        if ( getPropertyNode( propertyPath ) == null ) {
            this.propertyPaths.add( propertyPath );
            this.propertyNodes.add( propertyNode );
        }
    }

    private void addPathFragment( FeatureTypeNode featureTypeNode, PropertyPath propertyPath,
                                 int startStep ) {

        for ( int step = startStep; step < propertyPath.getSteps(); step += 2 ) {
            LOG.logDebug( "Looking up property: " + propertyPath.getStep( step ).getPropertyName() );
            MappedPropertyType propertyType = getPropertyType( featureTypeNode.getFeatureType(),
                                                               propertyPath.getStep( step ) );
            if ( propertyType instanceof MappedSimplePropertyType ) {
                addSimplePropertyNode( featureTypeNode, (MappedSimplePropertyType) propertyType,
                                       propertyPath, step );
                break;
            } else if ( propertyType instanceof MappedGeometryPropertyType ) {
                addGeometryPropertyNode( featureTypeNode,
                                         (MappedGeometryPropertyType) propertyType, propertyPath,
                                         step );
                break;
            } else if ( propertyType instanceof MappedFeaturePropertyType ) {
                MappedFeaturePropertyType featurePT = (MappedFeaturePropertyType) propertyType;
                featureTypeNode = addFeaturePropertyNode( featureTypeNode, featurePT, propertyPath,
                                                          step );
            } else {
                assert ( false );
            }
        }
    }

    /**
     * Returns the {@link MappedPropertyType} for the given {@link MappedFeatureType} that matches 
     * the given {@link PropertyPathStep}.
     * 
     * @param featureType
     * @param step
     * @return matching property type or null, if none exists
     */
    private MappedPropertyType getPropertyType( MappedFeatureType featureType, PropertyPathStep step ) {

        MappedPropertyType propertyType = null;
        QualifiedName name = step.getPropertyName();

        if ( step instanceof AttributeStep ) {
            // TODO remove handling of gml:id here (after adaptation of feature model)
            if ( CommonNamespaces.GMLNS.equals( name.getNamespace() )
                 && "id".equals( name.getLocalName() ) ) {
                MappedGMLId gmlId = featureType.getGMLId();
                propertyType = new MappedSimplePropertyType( name, Types.VARCHAR, 1, 1, false,
                                                             null, gmlId.getIdFields()[0] );
            }
        } else {
            // "normal" property (not gml:id)
            propertyType = (MappedPropertyType) featureType.getProperty( name );
        }

        return propertyType;
    }

    private void addSimplePropertyNode( FeatureTypeNode featureTypeNode,
                                       MappedSimplePropertyType propertyType,
                                       PropertyPath propertyPath, int step ) {

        assert ( step == propertyPath.getSteps() - 1 );
        String[] tableAliases = generateTableAliases( propertyType );
        PropertyNode propertyNode = new SimplePropertyNode( propertyType, featureTypeNode,
                                                            tableAliases );
        this.propertyPaths.add( propertyPath );
        this.propertyNodes.add( propertyNode );
        featureTypeNode.addPropertyNode( propertyNode );
    }

    private void addGeometryPropertyNode( FeatureTypeNode featureTypeNode,
                                         MappedGeometryPropertyType propertyType,
                                         PropertyPath propertyPath, int step ) {

        assert ( step == propertyPath.getSteps() - 1 );
        String[] tableAliases = generateTableAliases( propertyType );
        PropertyNode propertyNode = new GeometryPropertyNode( propertyType, featureTypeNode,
                                                              tableAliases );
        this.propertyPaths.add( propertyPath );
        this.propertyNodes.add( propertyNode );
        featureTypeNode.addPropertyNode( propertyNode );
    }

    private FeatureTypeNode addFeaturePropertyNode( FeatureTypeNode featureTypeNode,
                                                   MappedFeaturePropertyType pt,
                                                   PropertyPath propertyPath, int step ) {

        assert ( step < propertyPath.getSteps() - 1 );
        FeatureType[] allowedTypes = this.schema.getSubstitutions( pt.getFeatureTypeReference().getFeatureType() );
        QualifiedName givenTypeName = propertyPath.getStep( step + 1 ).getPropertyName();
        MappedFeatureType givenType = null;

        for ( int i = 0; i < allowedTypes.length; i++ ) {
            if ( allowedTypes[i].getName().equals( givenTypeName ) ) {
                givenType = (MappedFeatureType) allowedTypes[i];
                break;
            }
        }
        assert ( givenType != null );

        // TODO make proper
        String[] tableAliases = this.aliasGenerator.generateUniqueAliases( pt.getTableRelations().length - 1 );
        String tableAlias = this.aliasGenerator.generateUniqueAlias();
        FeatureTypeNode childFeatureTypeNode = new FeatureTypeNode( givenType, tableAlias );

        FeatureType featureType = pt.getFeatureTypeReference().getFeatureType();
        LOG.logDebug( "featureType: " + featureType.getName() );

        PropertyNode propertyNode = new FeaturePropertyNode( pt, featureTypeNode, tableAliases,
                                                             childFeatureTypeNode );
        //        this.propertyPaths.add (propertyPath);
        //        this.propertyNodes.add (propertyNode);
        featureTypeNode.addPropertyNode( propertyNode );
        return childFeatureTypeNode;
    }

    private FeatureTypeNode getFeatureTypeNode( MappedFeaturePropertyType content,
                                               PropertyPath propertyPath, int step ) {

        FeatureType[] allowedTypes = this.schema.getSubstitutions( content.getFeatureTypeReference().getFeatureType() );
        QualifiedName givenTypeName = propertyPath.getStep( step ).getPropertyName();
        MappedFeatureType givenType = null;

        for ( int i = 0; i < allowedTypes.length; i++ ) {
            if ( allowedTypes[i].getName().equals( givenTypeName ) ) {
                givenType = (MappedFeatureType) allowedTypes[i];
                break;
            }
        }
        assert ( givenType != null );
        String tableAlias = this.aliasGenerator.generateUniqueAlias();
        FeatureTypeNode childFeatureTypeNode = new FeatureTypeNode( givenType, tableAlias );

        return childFeatureTypeNode;
    }

    private String[] generateTableAliases( MappedPropertyType pt ) {
        String[] aliases = null;
        TableRelation[] relations = pt.getTableRelations();
        if ( relations != null ) {
            aliases = new String[relations.length];
            for ( int i = 0; i < aliases.length; i++ ) {
                aliases[i] = this.aliasGenerator.generateUniqueAlias();
            }
        }
        return aliases;
    }

    @Override
    public String toString() {
        return root.toString( "" );
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: QueryTableTree.java,v $
 * Revision 1.22  2006/09/20 11:35:41  mschneider
 * Merged datastore related messages with org.deegree.18n.
 *
 * Revision 1.21  2006/09/19 16:16:25  poth
 * *** empty log message ***
 *
 * Revision 1.20  2006/09/19 14:56:04  mschneider
 * Fixed error message.
 *
 * Revision 1.19  2006/09/05 14:44:31  mschneider
 * Splitted validation and adding of PropertyPaths. Added handling of sort properties and constant content.
 *
 * Revision 1.18  2006/09/04 14:16:14  mschneider
 * Cleaned up handling if no TableAliasGenerator is given.
 *
 * Revision 1.17  2006/09/04 13:57:23  mschneider
 * Removed unused member variable allFeatureTypeNodes.
 *
 * Revision 1.16  2006/08/28 16:38:59  mschneider
 * Javadoc fixes.
 *
 * Revision 1.15  2006/08/24 06:40:05  poth
 * File header corrected
 *
 * Revision 1.14  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.13  2006/08/21 15:47:18  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.12  2006/08/15 17:41:28  mschneider
 * Changed signature of  #add ( PropertyPath[] ) to #add( List<PropertyPath> ).
 *
 * Revision 1.11  2006/08/14 16:50:17  mschneider
 * Improved javadoc.
 *
 * Revision 1.10  2006/05/21 19:09:02  poth
 * several methods set to public; required by SDE datastore
 *
 ************************************************************************************************* */