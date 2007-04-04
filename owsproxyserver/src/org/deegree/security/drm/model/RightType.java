package org.deegree.security.drm.model;

/**
 * A <code>RightType</code> defines a certain type of right, e.g. an * 'access' right. It encapsulates a unique id and an also unique name. *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.5 $
 */

public class RightType {

    // predefined right types
    // general
    public static final RightType ACCESS = new RightType(1, "access");

    public static final RightType QUERY = new RightType(2, "query");

    public static final RightType DELETE = new RightType(3, "delete");

    public static final RightType INSERT = new RightType(4, "insert");

    public static final RightType EXECUTE = new RightType(5, "execute");

    public static final RightType UPDATE = new RightType(6, "update");

    public static final RightType VIEW = new RightType(7, "view");

    public static final RightType GRANT = new RightType(8, "grant");
    
    // WMS
    public static final RightType GETMAP = new RightType(9, "GetMap");
    
    public static final RightType GETFEATUREINFO = new RightType( 10, "GetFeatureInfo");

    public static final RightType GETLEGENDGRAPHIC = new RightType( 11, "GetLegendGraphic");

    // WFS
    public static final RightType GETFEATURE = new RightType(13, "GetFeature");

    public static final RightType DESCRIBEFEATURETYPE = new RightType( 14, "DescribeFeatureType");

    // WCS
    public static final RightType GETCOVERAGE = new RightType(15, "GetCoverage");

    public static final RightType DESCRIBECOVERAGE = new RightType( 16, "DescribeCoverage");
    
    // CSW
    public static final RightType GETRECORDS = new RightType(17, "GetRecords");
    
    public static final RightType GETRECORDBYID = new RightType(18, "GetRecordById");

    public static final RightType DESCRIBERECORDTYPE = new RightType( 19, "DescribeRecordType");

	
	private int id;
	private String name;

	/**
	 * Creates a new <code>RightType</code>-instance.
	 * 
	 * @param id
	 * @param name
	 */
	public RightType (int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the unique identifier of this <code>RightType</code>.
	 */
	public int getID () {
		return id;
	}

    /**
     * Returns the name of this <code>RightType</code>.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

	
	/**
	 * Indicates whether some other <code>RightType</code> instance is
	 * "equal to" this one.
	 *
	 * @param that
	 */	
	public boolean equals (Object that) {
		if (that instanceof RightType) {
			return (((RightType) that).getID () == getID ());
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object. This method is supported
	 * for the benefit of hashtables such as those provided by
	 * java.util.Hashtable.
	 */		
	public int hashCode () {
		return id;
	}

	/**
	 * Returns a <code>String</code> representation of this object.
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer ("Id: ").
			append (id).append (", Name: ").append (name);
		return sb.toString ();
	}	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RightType.java,v $
Revision 1.5  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.4  2006/08/02 13:01:03  poth
support for CSW right types added

Revision 1.3  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
