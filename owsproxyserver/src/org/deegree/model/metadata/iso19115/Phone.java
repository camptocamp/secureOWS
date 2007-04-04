/*
 * ---------------- FILE HEADER ------------------------------------------
 * 
 * This file is part of deegree. Copyright (C) 2001-2006 by: EXSE, Department of
 * Geography, University of Bonn http://www.giub.uni-bonn.de/deegree/ lat/lon
 * Fitzke/Fretter/Poth GbR http://www.lat-lon.de
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * Andreas Poth lat/lon GmbH Aennchenstr. 19 53115
 * Bonn Germany E-Mail: poth@lat-lon.de
 * 
 * Prof. Dr. Klaus Greve Department of Geography University of Bonn Meckenheimer Allee 166
 * 53115 Bonn Germany E-Mail: greve@giub.uni-bonn.de
 * 
 * 
 * ---------------------------------------------------------------------------
 */

package org.deegree.model.metadata.iso19115;

import java.util.ArrayList;

/**
 * Represents a contact phone number.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $
 * 
 * @since 2.0
 */
public class Phone {

    private ArrayList facsimile;
    private ArrayList other;
    private ArrayList othertype;
    private ArrayList voice;

    private Phone() {
        this.facsimile = new ArrayList();
        this.other = new ArrayList();
        this.othertype = new ArrayList();
        this.voice = new ArrayList();
    }
    
    /**
     * Creates a new instance of Phone
     * 
     * @param facsimile
     * @param voice
     */
    public Phone(String[] facsimile, String[] voice) {
        this();

        setFacsimile(facsimile);
        setVoice(voice);
    }

    /**
     * Creates a new instance of Phone
     * 
     * @param facsimile
     * @param other
     * @param othertype
     * @param voice
     */
    public Phone(String[] facsimile, String[] other, String[] othertype,
            String[] voice) {
        this();

        setFacsimile(facsimile);
        setOther(other);
        setOtherType(othertype);
        setVoice(voice);
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * 
     * @uml.property name="facsimile"
     */
    public String[] getFacsimile() {
        return (String[]) facsimile.toArray(new String[facsimile.size()]);
    }


    /**
     * @see #getFacsimile()
     */
    public void addFacsimile(String facsimile) {
        this.facsimile.add(facsimile);
    }

    /**
     * @see #getFacsimile()
     */
    public void setFacsimile(String[] facsimile) {
        this.facsimile.clear();
        for (int i = 0; i < facsimile.length; i++) {
            this.facsimile.add(facsimile[i]);
        }
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * 
     * @uml.property name="other"
     */
    public String[] getOther() {
        return (String[]) other.toArray(new String[other.size()]);
    }


    /**
     * @see #getOther()
     */
    public void addOther(String other) {
        this.other.add(other);
    }

    /**
     * @see #getOther()
     */
    public void setOther(String[] other) {
        this.other.clear();
        if (other != null) {
            for (int i = 0; i < other.length; i++) {
                this.other.add(other[i]);
            }
        }
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     *  
     */
    public String[] getOtherType() {
        return (String[]) othertype.toArray(new String[othertype.size()]);
    }

    /**
     * @see #getOtherType()
     */
    public void addOtherType(String othertype) {
        this.othertype.add(othertype);
    }

    /**
     * @see #getOtherType()
     */
    public void setOtherType(String[] othertype) {
        this.othertype.clear();
        if (othertype != null) {
            for (int i = 0; i < othertype.length; i++) {
                this.othertype.add(othertype[i]);
            }
        }
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * 
     * @uml.property name="voice"
     */
    public String[] getVoice() {
        return (String[]) voice.toArray(new String[voice.size()]);
    }

    /**
     * @see #getVoice()
     */
    public void addVoice(String voice) {
        this.voice.add(voice);
    }

    /**
     * @see #getVoice()
     */
    public void setVoice(String[] voice) {
        this.voice.clear();
        for (int i = 0; i < voice.length; i++) {
            this.voice.add(voice[i]);
        }
    }

    /**
     * to String method
     */
    public String toString() {
        String ret = null;
        ret = "facsimile = " + facsimile + "\n";
        ret += "other = " + other + "\n";
        ret += "othertype = " + othertype + "\n";
        ret += "voice = " + voice + "\n";
        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Phone.java,v $
Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
