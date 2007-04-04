/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon GmbH
http://www.lat-lon.de

It has been implemented within SEAGIS - An OpenSource implementation of OpenGIS specification
(C) 2001, Institut de Recherche pour le D�veloppement (http://sourceforge.net/projects/seagis/)
SEAGIS Contacts:  Surveillance de l'Environnement Assist�e par Satellite
                  Institut de Recherche pour le D�veloppement / US-Espace
                  mailto:seasnet@teledetection.fr


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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree.model.csct.resources.css;


/**
 * Resource keys. This interface is used when compiling sources, but
 * no dependencies to <code>ResourceKeys</code> should appear in any
 * resulting class files.  Since Java compiler inline final integers
 * values, using long identifiers will not bloat constant pools of
 * classes compiled against the interface, providing that no class
 * implements this interface.
 *
 * @see org.deegree.model.csct.resources.ResourceBundle
 */
public interface ResourceKeys
{
    public static final int ABRIDGED_MOLODENSKI_TRANSFORM               =     0;
    public static final int AFFINE_TRANSFORM                            =     1;
    public static final int ALTITUDE                                    =     2;
    public static final int BAROMETRIC_ALTITUDE                         =     3;
    public static final int BURSA_WOLF_PARAMETERS_REQUIRED              =     4;
    public static final int CLASSIC                                     =     5;
    public static final int CONVERSION                                  =     6;
    public static final int CONVERSION_AND_TRANSFORMATION               =     7;
    public static final int CYLINDRICAL_MERCATOR_PROJECTION             =     8;
    public static final int DEPTH                                       =     9;
    public static final int DISCONTINUOUS                               =    10;
    public static final int DOWN                                        =    11;
    public static final int EAST                                        =    12;
    public static final int ELLIPSOIDAL                                 =    13;
    public static final int FUTURE                                      =    14;
    public static final int GEOCENTRIC                                  =    15;
    public static final int GEOID_MODEL_DERIVED                         =    16;
    public static final int GMT                                         =    17;
    public static final int HORIZONTAL                                  =    18;
    public static final int INSIDE                                      =    19;
    public static final int INVERSE_$1                                  =    20;
    public static final int LAMBERT_CONFORMAL_PROJECTION                =    21;
    public static final int LATITUDE                                    =    22;
    public static final int LOADED_JDBC_DRIVER_$3                       =    23;
    public static final int LOCAL                                       =    24;
    public static final int LONGITUDE                                   =    25;
    public static final int MTM_PROJECTION                              =    26;
    public static final int NORMAL                                      =    27;
    public static final int NORTH                                       =    28;
    public static final int ORTHOMETRIC                                 =    29;
    public static final int OTHER                                       =    30;
    public static final int OUTSIDE                                     =    31;
    public static final int PAST                                        =    32;
    public static final int PROJECTION_ALREADY_BOUNDS_$1                =    33;
    public static final int SOUTH                                       =    34;
    public static final int STEREOGRAPHIC_PROJECTION                    =    35;
    public static final int TEMPORAL                                    =    36;
    public static final int TIME                                        =    37;
    public static final int TRANSFORMATION                              =    38;
    public static final int UP                                          =    39;
    public static final int UTC                                         =    40;
    public static final int UTM_PROJECTION                              =    41;
    public static final int VERTICAL                                    =    42;
    public static final int WEST                                        =    43;
    public static final int PLATE_CARREE_PROJECTION                     =  1043;

    public static final int ERROR_ANGLE_OVERFLOW_$1                     =    44;
    public static final int ERROR_ANTIPODE_LATITUDES_$2                 =    45;
    public static final int ERROR_CANT_CONCATENATE_CS_$2                =    46;
    public static final int ERROR_CANT_REDUCE_TO_TWO_DIMENSIONS_$1      =    47;
    public static final int ERROR_COLINEAR_AXIS_$2                      =    48;
    public static final int ERROR_ILLEGAL_ANGLE_PATTERN_$1              =    49;
    public static final int ERROR_ILLEGAL_ARGUMENT_$2                   =    50;
    public static final int ERROR_ILLEGAL_ARRAY_LENGTH_FOR_DIMENSION_$1 =    51;
    public static final int ERROR_ILLEGAL_AXIS_ORIENTATION_$2           =    52;
    public static final int ERROR_ILLEGAL_CS_DIMENSION_$1               =    53;
    public static final int ERROR_ILLEGAL_ENVELOPE_ORDINATE_$1          =    54;
    public static final int ERROR_INCOMPATIBLE_ELLIPSOID_$2             =    55;
    public static final int ERROR_INDEX_OUT_OF_BOUNDS_$1                =    56;
    public static final int ERROR_LATITUDE_OUT_OF_RANGE_$1              =    57;
    public static final int ERROR_LONGITUDE_OUT_OF_RANGE_$1             =    58;
    public static final int ERROR_MATRIX_NOT_REGULAR                    =    59;
    public static final int ERROR_MISMATCHED_DIMENSION_$2               =    60;
    public static final int ERROR_MISSING_PARAMETER_$1                  =    61;
    public static final int ERROR_NONINVERTIBLE_TRANSFORM               =    62;
    public static final int ERROR_NON_ANGULAR_UNIT_$1                   =    63;
    public static final int ERROR_NON_LINEAR_UNIT_$1                    =    64;
    public static final int ERROR_NON_TEMPORAL_UNIT_$1                  =    65;
    public static final int ERROR_NOT_AN_AFFINE_TRANSFORM               =    66;
    public static final int ERROR_NOT_AN_ANGLE_OBJECT_$1                =    67;
    public static final int ERROR_NOT_TWO_DIMENSIONAL_$1                =    68;
    public static final int ERROR_NO_CONVERGENCE                        =    69;
    public static final int ERROR_NO_DESTINATION_AXIS_$1                =    70;
    public static final int ERROR_NO_TRANSFORMATION_PATH_$2             =    71;
    public static final int ERROR_NO_TRANSFORM_FOR_CLASSIFICATION_$1    =    72;
    public static final int ERROR_NULL_ARGUMENT_$1                      =    73;
    public static final int ERROR_PARSE_ANGLE_EXCEPTION_$2              =    74;
    public static final int ERROR_POLE_PROJECTION_$1                    =    75;
    public static final int ERROR_UNMODIFIABLE_AFFINE_TRANSFORM         =    76;
    public static final int ERROR_VALUE_TEND_TOWARD_INFINITY            =    77;
}
