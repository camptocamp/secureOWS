/*
----------------    FILE HEADER  ------------------------------------------
 
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

package org.deegree.model.metadata.iso19115;

import java.util.ArrayList;

/**
 * Citation_Impl.java
 *
 * Created on 16. September 2002, 09:55
 * <p>----------------------------------------------------------------------</p>
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer</a>
 * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:18 $
 */
public class Citation {

    private ArrayList alternatetitle = null;
    private ArrayList citedresponsibleparty  = null;
    private String collectiontitle = null;
    private ArrayList date = null;
    private String edition = null;
    private String editiondate = null;
    private ArrayList identifier = null;
    private ArrayList identifiertype = null;
    private String isbn = null;
    private String issn = null;
    private String issueidentification = null;
    private String othercitationdtails = null;
    private String page = null;
    private ArrayList presentationformcode = null;
    private String seriesname = null;
    private String title = null;
    
    /** Creates a new instance of Citation_Impl */
    public Citation( String[] alternatetitle,
                          CitedResponsibleParty[] citedresponsibleparty,
                          String collectiontitle,
                          Date[] date,
                          String edition,
                          String editiondate,
                          String[] identifier,
                          String[] identifiertype,
                          String isbn,
                          String issn,
                          String issueidentification,
                          String othercitationdtails,
                          String page,
                          PresentationFormCode[] presentationformcode,
                          String seriesname,
                          String title) {
        
        this.alternatetitle = new ArrayList();
        this.citedresponsibleparty = new ArrayList();
        this.date = new ArrayList();
        this.identifier = new ArrayList();
        this.identifiertype = new ArrayList();
        this.presentationformcode = new ArrayList();

        setAlternateTitle(alternatetitle);
        setCitedResponsibleParty(citedresponsibleparty);
        setCollectionTitle(collectiontitle);
        setDate(date);
        setEdition(edition);
        setEditionDate(editiondate);
        setIdentifier(identifier);
        setIdentifierType(identifiertype);
        setIsbn(isbn);
        setIssn(issn);
        setIssueIdentification(issueidentification);
        setOtherCitationDetails(othercitationdtails);
        setPage(page);
        setPresentationFormCode(presentationformcode);
        setSeriesName(seriesname);
        setTitle(title);
    }    


    /** minOccurs="0" maxOccurs="unbounded"
     * @return
     *
     */
    public String[] getAlternateTitle() {
        return (String[])alternatetitle.toArray( new String[alternatetitle.size()] );
    }
       
    
    /**
     * @see Citation#getAlternateTitle()
     */
    public void addAlternateTitle(String alternatetitle) {
        this.alternatetitle.add(alternatetitle);
    }
    
    /**
     * @see Citation#getAlternateTitle()
     */
    public void setAlternateTitle(String[] alternatetitle) {
        this.alternatetitle.clear();
        for (int i = 0; i < alternatetitle.length; i++) {
            this.alternatetitle.add( alternatetitle[i] );
        }
    }
    

    
    /**
     * minOccurs="0" maxOccurs="unbounded"
     * @return
     */
    public CitedResponsibleParty[] getCitedResponsibleParty() {
        return (CitedResponsibleParty[])citedresponsibleparty.toArray( new String[citedresponsibleparty.size()] );
    }
    
    /**
     * @see Citation#getCitedResponsibleParty()
     */
    public void addCitedResponsibleParty (CitedResponsibleParty citedresponsibleparty) {
        this.citedresponsibleparty.add(citedresponsibleparty);
    }
    
    /**
     * @see Citation#getCitedResponsibleParty()
     */
    public void setCitedResponsibleParty(CitedResponsibleParty[] citedresponsibleparty) {
        this.citedresponsibleparty.clear();
        for (int i = 0; i < citedresponsibleparty.length; i++) {
            this.citedresponsibleparty.add( citedresponsibleparty[i] );
        }
    }

    
    /**
     * minOccurs="0"
     * @return
     */
    public String getCollectionTitle() {
        return collectiontitle;
    }
    
    /**
     * @see Citation#getCollectionTitle()
     */
    public void setCollectionTitle(String collectiontitle) {
        this.collectiontitle = collectiontitle;
    }

    /**
     * maxOccurs="unbounded"
     * @return
     * 
     * @uml.property name="date"
     */
    public Date[] getDate() {
        return (Date[]) date.toArray(new Date[date.size()]);
    }

    
    /**
     * @see Citation#getDate()
     */
    public void addDate(Date date) {
        this.date.add(date);
    }
    
    /**
     * @see Citation#getDate()
     */
    public void setDate(Date[] date) {
        this.date.clear();
        for (int i = 0; i < date.length; i++) {
            this.date.add( date[i] );
        }
    }

    /**
     * minOccurs="0"
     * @return String
     * 
     * @uml.property name="edition"
     */
    public String getEdition() {
        return edition;
    }

    /**
     * @see Citation#getEdition()
     * 
     * @uml.property name="edition"
     */
    public void setEdition(String edition) {
        this.edition = edition;
    }


    
    /**
     * minOccurs="0"
     * @return
     */
    public String getEditionDate() {
        return editiondate;
    }
    
    /**
     * @see Citation#getEditionDate()
     */
    public void setEditionDate(String editiondate) {
        this.editiondate = editiondate;
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * @return
     * 
     * @uml.property name="identifier"
     */
    public String[] getIdentifier() {
        return (String[]) identifier.toArray(new String[identifier.size()]);
    }

    
    /**
     * @see Citation#getIdentifier()
     */
    public void addIdentifier(String identifier) {
        this.identifier.add(identifier);
    }
    
    /**
     * @see Citation#getIdentifier()
     */
    public void setIdentifier(String[] identifier) {
        this.identifier.clear();
        for (int i = 0; i < identifier.length; i++) {
            this.identifier.add( identifier[i] );
        }
    }

    
    /** minOccurs="0" maxOccurs="unbounded"
     * @return
     *
     */
    public String[] getIdentifierType() {
        return (String[])identifiertype.toArray( new String[identifiertype.size()] );
    }
    
    /**
     * @see Citation#getIdentifierType()
     */
    public void addIdentifierType(String identifiertype) {
        this.identifiertype.add(identifiertype);
    }
    
    /**
     * @see Citation#getIdentifierType()
     */
    public void setIdentifierType(String[] identifiertype) {
        this.identifiertype.clear();
        for (int i = 0; i < identifiertype.length; i++) {
            this.identifiertype.add( identifiertype[i] );
        }
    }

    /**
     * minOccurs="0"
     * @return
     * 
     * @uml.property name="isbn"
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @see Citation#getIsbn()
     * 
     * @uml.property name="isbn"
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * minOccurs="0"
     * @return
     * 
     * @uml.property name="issn"
     */
    public String getIssn() {
        return issn;
    }

    /**
     * @see Citation#getIssn()
     * 
     * @uml.property name="issn"
     */
    public void setIssn(String issn) {
        this.issn = issn;
    }



    /** minOccurs="0"
     * @return
     *
     */
    public String getIssueIdentification() {
        return issueidentification;
    }
    
    /**
     * @see Citation#getIssueIdentification()
     */
    public void setIssueIdentification(String issueidentification) {
        this.issueidentification = issueidentification;
    }
    

    /** minOccurs="0"
     * @return
     *
     */
    public String getOtherCitationDetails() {
        return othercitationdtails;
    }
    
    /**
     * @see Citation#getOtherCitationDetails()
     */
    public void setOtherCitationDetails(String othercitationdtails) {
        this.othercitationdtails = othercitationdtails;
    }

    /**
     * minOccurs="0"
     * @return
     * 
     * @uml.property name="page"
     */
    public String getPage() {
        return page;
    }

    /**
     * @see Citation#getPage()
     * 
     * @uml.property name="page"
     */
    public void setPage(String page) {
        this.page = page;
    }

    

    /** minOccurs="0" maxOccurs="unbounded"
     * @return
     *
     */
    public PresentationFormCode[] getPresentationFormCode() {
        return (PresentationFormCode[])presentationformcode.toArray( new PresentationFormCode[presentationformcode.size()] );
    }
    
    /**
     * @see Citation#getPresentationFormCode()
     */
    public void addPresentationFormCode(PresentationFormCode presentationformcode) {
        this.presentationformcode.add(presentationformcode);
    }
    
    /**
     * @see Citation#getPresentationFormCode()
     */
    public void setPresentationFormCode(PresentationFormCode[] presentationformcode) {
        this.presentationformcode.clear();
        for (int i = 0; i < presentationformcode.length; i++) {
            this.presentationformcode.add( presentationformcode[i] );
        }
    }

    
    /** minOccurs="0"
     * @return
     *
     */
    public String getSeriesName() {
        return seriesname;
    }
    
    /**
     * @see Citation#getSeriesName()
     */
    public void setSeriesName(String seriesname) {
        this.seriesname = seriesname;
    }

    /**
     * @return
     * 
     * @uml.property name="title"
     */
    public String getTitle() {
        return title;
    }

    /**
     * @see Citation#getTitle()
     * 
     * @uml.property name="title"
     */
    public void setTitle(String title) {
        this.title = title;
    }

	/**
     * to String method
     */
	public String toString() {
		String ret = null;
		ret = "alternatetitle = " + alternatetitle + "\n";
		ret += "citedresponsibleparty = " + citedresponsibleparty + "\n";
		ret += "collectiontitle = " + collectiontitle + "\n";
		ret += "date = " + date + "\n";
		ret += "edition = " + edition + "\n";
		ret += "editiondate = " + editiondate + "\n";
		ret += "identifier = " + identifier + "\n";
		ret += "identifiertype = " + identifiertype + "\n";
		ret += "isbn = " + isbn + "\n";
		ret += "issn = " + issn + "\n";
		ret += "issueidentification = " + issueidentification + "\n";
		ret += "othercitationdtails = " + othercitationdtails + "\n";
		ret += "page = " + page + "\n";
		ret += "presentationformcode = " + presentationformcode + "\n";
		ret += "seriesname = " + seriesname + "\n";
		ret += "title = " + title + "\n";
		return ret;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Citation.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
