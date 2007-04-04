/*
 * Created on 25.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.deegree.ogcwebservices.getcapabilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * ServiceOperation
 * 
 * @author Administrator
 *
 * @author last edited by: $Author: poth $
 *
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/07/12 14:46:16 $
 *  
 * @since 2.0
 */
public class ServiceOperation {
    
    private List dcpList;
    
    public ServiceOperation() {
        this.dcpList = new ArrayList();
    }
    
    public DCPType[] getDCPTypes(Protocol protocol) {
        DCPType[] typeArray;
        List returnTypeList = new ArrayList();
        Iterator iterator = dcpList.iterator();
        while (iterator.hasNext()) {
            DCPType element = (DCPType) iterator.next();
            if (element.getProtocol().equals(protocol)) {
                returnTypeList.add(element);
            }
        }
        typeArray = new DCPType[returnTypeList.size()];
        return (DCPType[]) returnTypeList.toArray(typeArray);
    }
    
    /**
     * Set all DCP types.
     * First empyt list, then sets 
     * @param types
     */
    public void setDCPTypes(DCPType[] types) {
        this.dcpList.clear();
        for (int i = 0; i < types.length; i++) {
            this.addDCPType(types[i]);
        }
    }
    
    public void addDCPType(DCPType type) {
        this.dcpList.add( type );
    }
    
    

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceOperation.java,v $
Revision 1.2  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
