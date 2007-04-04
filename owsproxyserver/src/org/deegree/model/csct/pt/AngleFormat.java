/*
 * Geotools 2 - OpenSource mapping toolkit
 * (C) 2003, Geotools Project Managment Committee (PMC)
 * (C) 2001, Institut de Recherche pour le D�veloppement
 * (C) 1999, Fisheries and Oceans Canada
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Contacts:
 *     UNITED KINGDOM: James Macgill
 *             mailto:j.macgill@geog.leeds.ac.uk
 *
 *     FRANCE: Surveillance de l'Environnement Assist�e par Satellite
 *             Institut de Recherche pour le D�veloppement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 */
package org.deegree.model.csct.pt;

// Input/output
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.XMath;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;


/**
 * Parse and format angle according a specified pattern. The pattern is a string
 * containing any characters, with a special meaning for the following characters:
 *
 * <blockquote><table cellpadding="3">
 *     <tr><td><code>D</code></td><td>&nbsp;&nbsp;The integer part of degrees</td></tr>
 *     <tr><td><code>d</code></td><td>&nbsp;&nbsp;The fractional part of degrees</td></tr>
 *     <tr><td><code>M</code></td><td>&nbsp;&nbsp;The integer part of minutes</td></tr>
 *     <tr><td><code>m</code></td><td>&nbsp;&nbsp;The fractional part of minutes</td></tr>
 *     <tr><td><code>S</code></td><td>&nbsp;&nbsp;The integer part of seconds</td></tr>
 *     <tr><td><code>s</code></td><td>&nbsp;&nbsp;The fractional part of seconds</td></tr>
 *     <tr><td><code>.</code></td><td>&nbsp;&nbsp;The decimal separator</td></tr>
 * </table></blockquote>
 * <br>
 * Upper-case letters <code>D</code>, <code>M</code> and <code>S</code> are for the integer
 * parts of degrees, minutes and seconds respectively. They must appear in this order (e.g.
 * "<code>M'D</code>" is illegal because "M" and "S" are inverted; "<code>D�S</code>" is
 * illegal too because there is no "M" between "D" and "S"). Lower-case letters <code>d</code>,
 * <code>m</code> and <code>s</code> are for fractional parts of degrees, minutes and seconds
 * respectively. Only one of those may appears in a pattern, and it must be the last special
 * symbol (e.g. "<code>D.dd�MM'</code>" is illegal because "d" is followed by "M";
 * "<code>D.mm</code>" is illegal because "m" is not the fractional part of "D").
 * <br><br>
 * The number of occurrence of <code>D</code>, <code>M</code>, <code>S</code> and their
 * lower-case counterpart is the number of digits to format. For example, "DD.ddd" will
 * format angle with two digits for the integer part and three digits for the fractional
 * part (e.g. 4.4578 will be formatted as "04.458"). Separator characters like <code>�</code>,
 * <code>'</code> and <code>"</code> and inserted "as-is" in the formatted string (except the
 * decimal separator dot ("<code>.</code>"), which is replaced by the local-dependent decimal
 * separator). Separator characters may be completely omitted; <code>AngleFormat</code> will
 * still differentiate degrees, minutes and seconds fields according the pattern. For example,
 * "<code>0480439</code>" with the pattern "<code>DDDMMmm</code>" will be parsed as 48�04.39'.
 * <br><br>
 * The following table gives some examples of legal patterns.
 *
 * <blockquote><table cellpadding="3">
 * <tr><th>Pattern                </th>  <th>Example   </th></tr>
 * <tr><td><code>DD�MM'SS" </code></td>  <td>48�30'00" </td></tr>
 * <tr><td><code>DD�MM'    </code></td>  <td>48�30'    </td></tr>
 * <tr><td><code>DD.ddd    </code></td>  <td>48.500    </td></tr>
 * <tr><td><code>DDMM      </code></td>  <td>4830      </td></tr>
 * <tr><td><code>DDMMSS    </code></td>  <td>483000    </td></tr>
 * </table></blockquote>
 *
 * @see Angle
 * @see Latitude
 * @see Longitude
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class AngleFormat extends Format {
    /**
     * Caract�re repr�sentant l'h�misph�re nord.
     * Il doit obligatoirement �tre en majuscule.
     */
    private static final char NORTH='N';
    
    /**
     * Caract�re repr�sentant l'h�misph�re sud.
     * Il doit obligatoirement �tre en majuscule.
     */
    private static final char SOUTH='S';
    
    /**
     * Caract�re repr�sentant l'h�misph�re est.
     * Il doit obligatoirement �tre en majuscule.
     */
    private static final char EAST='E';
    
    /**
     * Caract�re repr�sentant l'h�misph�re ouest.
     * Il doit obligatoirement �tre en majuscule.
     */
    private static final char WEST='W';
    
    /**
     * Constante indique que l'angle
     * � formater est une longitude.
     */
    static final int LONGITUDE=0;
    
    /**
     * Constante indique que l'angle
     * � formater est une latitude.
     */
    static final int LATITUDE=1;
    
    /**
     * Constante indique que le nombre
     * � formater est une altitude.
     */
    static final int ALTITUDE=2;
    
    /**
     * A constant for the symbol to appears before the degrees fields.
     * Fields PREFIX, DEGREES, MINUTES and SECONDS <strong>must</strong>
     * have increasing values (-1, 0, +1, +2, +3).
     */
    private static final int PREFIX_FIELD = -1;
    
    /**
     * Constant for degrees field. When formatting a string, this value may be
     * specified to the {@link java.text.FieldPosition} constructor in order to
     * get the bounding index where degrees have been written.
     */
    public static final int DEGREES_FIELD = 0;
    
    /**
     * Constant for minutes field. When formatting a string, this value may be
     * specified to the {@link java.text.FieldPosition} constructor in order to
     * get the bounding index where minutes have been written.
     */
    public static final int MINUTES_FIELD = 1;
    
    /**
     * Constant for seconds field. When formatting a string, this value may be
     * specified to the {@link java.text.FieldPosition} constructor in order to
     * get the bounding index where seconds have been written.
     */
    public static final int SECONDS_FIELD = 2;
    
    /**
     * Constant for hemisphere field. When formatting a string, this value may be
     * specified to the {@link java.text.FieldPosition} constructor in order to
     * get the bounding index where the hemisphere synbol has been written.
     */
    public static final int HEMISPHERE_FIELD = 3;
    
    /**
     * Symboles repr�sentant les degr�s (0),
     * minutes (1) et les secondes (2).
     */
    private static final char[] SYMBOLS = {'D', 'M', 'S'};
    
    /**
     * Nombre minimal d'espaces que doivent occuper les parties
     * enti�res des degr�s (0), minutes (1) et secondes (2). Le
     * champs <code>widthDecimal</code> indique la largeur fixe
     * que doit avoir la partie d�cimale. Il s'appliquera au
     * dernier champs non-zero dans <code>width0..2</code>.
     */
    private int width0=1, width1=2, width2=0, widthDecimal=0;
    
    /**
     * Caract�res � ins�rer au d�but (<code>prefix</code>) et � la
     * suite des degr�s, minutes et secondes (<code>suffix0..2</code>).
     * Ces champs doivent �tre <code>null</code> s'il n'y a rien � ins�rer.
     */
    private String prefix=null, suffix0="�", suffix1="'", suffix2="\"";
    
    /**
     * Indique s'il faut utiliser le s�parateur d�cimal pour s�parer la partie
     * enti�re de la partie fractionnaire. La valeur <code>false</code> indique
     * que les parties enti�res et fractionnaires doivent �tre �crites ensembles
     * (par exemple 34867 pour 34.867). La valeur par d�faut est <code>true</code>.
     */
    private boolean decimalSeparator=true;
    
    /**
     * Format � utiliser pour �crire les nombres
     * (degr�s, minutes ou secondes) � l'int�rieur
     * de l'�criture d'un angle.
     */
    private final DecimalFormat numberFormat;
    
    /**
     * Objet � transmetre aux m�thodes <code>DecimalFormat.format</code>.
     * Ce param�tre existe simplement pour �viter de cr�er cet objet trop
     * souvent, alors qu'on ne s'y int�resse pas.
     */
    private transient FieldPosition dummy = new FieldPosition(0);
    
    /**
     * Restore fields after deserialization.
     */
    private void readObject(final ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        dummy = new FieldPosition(0);
    }
    
    /**
     * Returns the width of the specified field.
     */
    private int getWidth(final int index) {
        switch (index) {
            case DEGREES_FIELD:  return width0;
            case MINUTES_FIELD:  return width1;
            case SECONDS_FIELD:  return width2;
            default:             return 0; // Must be 0 (important!)
        }
    }
    
    /**
     * Set the width for the specified field.
     * All folowing fields will be set to 0.
     */
    private void setWidth(final int index, int width) {
        switch (index) {
            case DEGREES_FIELD: width0=width; width=0; // fall through
            case MINUTES_FIELD: width1=width; width=0; // fall through
            case SECONDS_FIELD: width2=width;          // fall through
        }
    }
    
    /**
     * Returns the suffix for the specified field.
     */
    private String getSuffix(final int index) {
        switch (index) {
            case  PREFIX_FIELD: return prefix;
            case DEGREES_FIELD: return suffix0;
            case MINUTES_FIELD: return suffix1;
            case SECONDS_FIELD: return suffix2;
            default:            return null;
        }
    }
    
    /**
     * Set the suffix for the specified field. Suffix
     * for all following fields will be set to their
     * default value.
     */
    private void setSuffix(final int index, String s) {
        switch (index) {
            case  PREFIX_FIELD:  prefix=s; s="�";  // fall through
            case DEGREES_FIELD: suffix0=s; s="'";  // fall through
            case MINUTES_FIELD: suffix1=s; s="\""; // fall through
            case SECONDS_FIELD: suffix2=s;         // fall through
        }
    }
    
    /**
     * Construct a new <code>AngleFormat</code> using
     * the current default locale and a default pattern.
     */
    public AngleFormat() {
        this("D�MM.m'");
    }
    
    /**
     * Construct a new <code>AngleFormat</code> using the
     * current default locale and the specified pattern.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     *         See class description for an explanation of how this pattern work.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public AngleFormat(final String pattern) throws IllegalArgumentException {
        this(pattern, new DecimalFormatSymbols());
    }
    
    /**
     * Construct a new <code>AngleFormat</code>
     * using the specified pattern and locale.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     *         See class description for an explanation of how this pattern work.
     * @param  locale Locale to use.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public AngleFormat(final String pattern, final Locale locale) throws IllegalArgumentException {
        this(pattern, new DecimalFormatSymbols(locale));
    }
    
    /**
     * Construct a new <code>AngleFormat</code>
     * using the specified pattern and decimal symbols.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     *         See class description for an explanation of how this pattern work.
     * @param  symbols The symbols to use for parsing and formatting numbers.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public AngleFormat(final String pattern, final DecimalFormatSymbols symbols) {
        // NOTE: pour cette routine, il ne faut PAS que DecimalFormat
        //       reconnaisse la notation exponentielle, parce que �a
        //       risquerait d'�tre confondu avec le "E" de "Est".
        numberFormat=new DecimalFormat("#0", symbols);
        applyPattern(pattern);
    }
    
    /**
     * Set the pattern to use for parsing and formatting angle.
     * See class description for an explanation of how patterns work.
     *
     * @param  pattern Pattern to use for parsing and formatting angle.
     * @throws IllegalArgumentException If the specified pattern is not legal.
     */
    public synchronized void applyPattern(final String pattern) throws IllegalArgumentException {
        widthDecimal = 0;
        decimalSeparator = true;
        int startPrefix = 0;
        int symbolIndex = 0;
        boolean parseFinished = false;
        final int length = pattern.length();
        for (int i=0; i<length; i++) {
            /*
             * On examine un � un tous les caract�res du patron en
             * sautant ceux qui ne sont pas r�serv�s ("D", "M", "S"
             * et leur �quivalents en minuscules). Les caract�res
             * non-reserv�s seront m�moris�s comme suffix plus tard.
             */
            final char c = pattern.charAt(i);
            final char upperCaseC = Character.toUpperCase(c);
            for (int field=DEGREES_FIELD; field<SYMBOLS.length; field++) {
                if (upperCaseC == SYMBOLS[field]) {
                    /*
                     * Un caract�re r�serv� a �t� trouv�. V�rifie maintenant
                     * s'il est valide. Par exemple il serait illegal d'avoir
                     * comme patron "MM.mm" sans qu'il soit pr�c�d� des degr�s.
                     * On attend les lettres "D", "M" et "S" dans l'ordre. Si
                     * le caract�re est en lettres minuscules, il doit �tre le
                     * m�me que le dernier code (par exemple "DD.mm" est illegal).
                     */
                    if (c==upperCaseC) {
                        symbolIndex++;
                    }
                    if (field!=symbolIndex-1 || parseFinished) {
                        setWidth(DEGREES_FIELD, 1);
                        setSuffix(PREFIX_FIELD, null);
                        widthDecimal=0;
                        decimalSeparator=true;
                        throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_ILLEGAL_ANGLE_PATTERN_$1, pattern));
                    }
                    if (c==upperCaseC) {
                        /*
                         * M�morise les caract�res qui pr�c�daient ce code comme suffix
                         * du champs pr�c�dent. Puis on comptera le nombre de fois que le
                         * code se r�p�te, en m�morisant cette information comme largeur
                         * de ce champ.
                         */
                        setSuffix(field-1, (i>startPrefix) ? pattern.substring(startPrefix, i) : null);
                        int w=1; while (++i<length && pattern.charAt(i)==c) w++;
                        setWidth(field, w);
                    } else {
                        /*
                         * Si le caract�re est une minuscule, ce qui le pr�c�dait sera le
                         * s�parateur d�cimal plut�t qu'un suffix. On comptera le nombre
                         * d'occurences du caract�res pour obtenir la pr�cision.
                         */
                        switch (i-startPrefix) {
                            case 0: decimalSeparator=false; break;
                            case 1: if (pattern.charAt(startPrefix)=='.') {
                                decimalSeparator=true;
                                break;
                            }
                            default: throw new IllegalArgumentException(Resources.format(
                                         ResourceKeys.ERROR_ILLEGAL_ANGLE_PATTERN_$1, pattern));
                        }
                        int w=1; while (++i<length && pattern.charAt(i)==c) w++;
                        widthDecimal=w;
                        parseFinished=true;
                    }
                    startPrefix = i--;
                    break; // Break 'j' and continue 'i'.
                }
            }
        }
        setSuffix(symbolIndex-1, (startPrefix<length) ? pattern.substring(startPrefix) : null);
    }
    
    /**
     * Returns the pattern used for parsing and formatting angles.
     * See class description for an explanation of how patterns work.
     */
    public synchronized String toPattern() {
        char symbol='#';
        final StringBuffer buffer=new StringBuffer();
        for (int field=DEGREES_FIELD; field<=SYMBOLS.length; field++) {
            final String previousSuffix=getSuffix(field-1);
            int w=getWidth(field);
            if (w>0) {
                /*
                 * Proc�de � l'�criture de la partie enti�re des degr�s,
                 * minutes ou secondes. Le suffix du champs pr�c�dent
                 * sera �crit avant les degr�s, minutes ou secondes.
                 */
                if (previousSuffix!=null) {
                    buffer.append(previousSuffix);
                }
                symbol=SYMBOLS[field];
                do buffer.append(symbol);
                while (--w>0);
            } else {
                /*
                 * Proc�de � l'�criture de la partie d�cimale des
                 * degr�s, minutes ou secondes. Le suffix du ce
                 * champs sera �crit apr�s cette partie fractionnaire.
                 */
                w=widthDecimal;
                if (w>0) {
                    if (decimalSeparator) buffer.append('.');
                    symbol=Character.toLowerCase(symbol);
                    do buffer.append(symbol);
                    while (--w>0);
                }
                if (previousSuffix!=null) {
                    buffer.append(previousSuffix);
                }
                break;
            }
        }
        return buffer.toString();
    }
    
    /**
     * Format an angle. The string will be formatted according
     * the pattern set in the last call to {@link #applyPattern}.
     *
     * @param  angle Angle to format, in degrees.
     * @return The formatted string.
     */
    public final String format(final double angle) {
        return format(angle, new StringBuffer(), null).toString();
    }
    
    /**
     * Formats an angle and appends the resulting text to a given string buffer.
     * The string will be formatted according the pattern set in the last call
     * to {@link #applyPattern}.
     *
     * @param  angle      Angle to format, in degrees.
     * @param  toAppendTo Where the text is to be appended.
     * @param  pos        An optional {@link FieldPosition} identifying a field
     *                    in the formatted text, or <code>null</code> if this
     *                    information is not wanted. This field position shall
     *                    be constructed with one of the following constants:
     *                    {@link #DEGREES_FIELD},
     *                    {@link #MINUTES_FIELD},
     *                    {@link #SECONDS_FIELD} or
     *                    {@link #HEMISPHERE_FIELD}.
     *
     * @return The string buffer passed in as <code>toAppendTo</code>, with formatted text appended.
     */
    public synchronized StringBuffer format(final double angle,
                                            StringBuffer toAppendTo,
                                            final FieldPosition pos)
    {
        double degrees = angle;
        /*
         * Calcule � l'avance les minutes et les secondes. Si les minutes et secondes
         * ne doivent pas �tre �crits, on m�morisera NaN. Notez que pour extraire les
         * parties enti�res, on utilise (int) au lieu de 'Math.floor' car (int) arrondie
         * vers 0 (ce qui est le comportement souhait�) alors que 'floor' arrondie vers
         * l'entier inf�rieur.
         */
        double minutes  = Double.NaN;
        double secondes = Double.NaN;
        if (width1!=0 && !Double.isNaN(angle)) {
            int tmp = (int) degrees; // Arrondie vers 0 m�me si n�gatif.
            minutes = Math.abs(degrees-tmp)*60;
            degrees = tmp;
            if (minutes<0 || minutes>60) {
                // Erreur d'arrondissement (parce que l'angle est trop �lev�)
                throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_ANGLE_OVERFLOW_$1, new Double(angle)));
            }
            if (width2 != 0) {
                tmp      = (int) minutes; // Arrondie vers 0 m�me si n�gatif.
                secondes = (minutes-tmp)*60;
                minutes  = tmp;
                if (secondes<0 || secondes>60) {
                    // Erreur d'arrondissement (parce que l'angle est trop �lev�)
                    throw new IllegalArgumentException(Resources.format(ResourceKeys.ERROR_ANGLE_OVERFLOW_$1, new Double(angle)));
                }
                /*
                 * On applique maintenant une correction qui tiendra
                 * compte des probl�mes d'arrondissements.
                 */
                final double puissance=XMath.pow10(widthDecimal);
                secondes=Math.rint(secondes*puissance)/puissance;
                tmp = (int) (secondes/60);
                secondes -= 60*tmp;
                minutes += tmp;
            } else {
                final double puissance=XMath.pow10(widthDecimal);
                minutes = Math.rint(minutes*puissance)/puissance;
            }
            tmp = (int) (minutes/60); // Arrondie vers 0 m�me si n�gatif.
            minutes -= 60*tmp;
            degrees += tmp;
        }
        /*
         * Les variables 'degr�s', 'minutes' et 'secondes' contiennent
         * maintenant les valeurs des champs � �crire, en principe �pur�s
         * des probl�mes d'arrondissements. Proc�de maintenant � l'�criture
         * de l'angle.
         */
        if (prefix != null) {
            toAppendTo.append(prefix);
        }
        final int field;
        if (pos != null) {
            field = pos.getField();
            pos.setBeginIndex(0);
            pos.setEndIndex(0);
        } else {
            field=PREFIX_FIELD;
        }
        toAppendTo = formatField(degrees, toAppendTo,
                                 field==DEGREES_FIELD ? pos : null,
                                 width0, width1==0, suffix0);
        if (!Double.isNaN(minutes)) {
            toAppendTo=formatField(minutes, toAppendTo,
                                   field==MINUTES_FIELD ? pos : null,
                                   width1, width2==0, suffix1);
        }
        if (!Double.isNaN(secondes)) {
            toAppendTo=formatField(secondes, toAppendTo,
                                   field==SECONDS_FIELD ? pos : null,
                                   width2, true, suffix2);
        }
        return toAppendTo;
    }
    
    /**
     * Proc�de � l'�criture d'un champ de l'angle.
     *
     * @param value Valeur � �crire.
     * @param toAppendTo Buffer dans lequel �crire le champs.
     * @param pos Objet dans lequel m�moriser les index des premiers
     *        et derniers caract�res �crits, ou <code>null</code>
     *        pour ne pas m�moriser ces index.
     * @param w Nombre de minimal caract�res de la partie enti�re.
     * @param last <code>true</code> si ce champs est le dernier,
     *        et qu'il faut donc �crire la partie d�cimale.
     * @param s Suffix � �crire apr�s le nombre (peut �tre nul).
     */
    private StringBuffer formatField(double value,
                                     StringBuffer toAppendTo, final FieldPosition pos,
                                     final int w, final boolean last, final String s)
    {
        final int startPosition=toAppendTo.length();
        if (!last) {
            numberFormat.setMinimumIntegerDigits(w);
            numberFormat.setMaximumFractionDigits(0);
            toAppendTo = numberFormat.format(value, toAppendTo, dummy);
        } else if (decimalSeparator) {
            numberFormat.setMinimumIntegerDigits(w);
            numberFormat.setMinimumFractionDigits(widthDecimal);
            numberFormat.setMaximumFractionDigits(widthDecimal);
            toAppendTo = numberFormat.format(value, toAppendTo, dummy);
        } else {
            value *= XMath.pow10(widthDecimal);
            numberFormat.setMaximumFractionDigits(0);
            numberFormat.setMinimumIntegerDigits(w+widthDecimal);
            toAppendTo = numberFormat.format(value, toAppendTo, dummy);
        }
        if (s!=null) {
            toAppendTo.append(s);
        }
        if (pos!=null) {
            pos.setBeginIndex(startPosition);
            pos.setEndIndex(toAppendTo.length()-1);
        }
        return toAppendTo;
    }
    
    /**
     * Formats an angle, a latitude or a longitude and appends the resulting text
     * to a given string buffer. The string will be formatted according the pattern
     * set in the last call to {@link #applyPattern}. The argument <code>obj</code>
     * shall be an {@link Angle} object or one of its derived class ({@link Latitude},
     * {@link Longitude}). If <code>obj</code> is a {@link Latitude} object, then a
     * symbol "N" or "S" will be appended to the end of the string (the symbol will
     * be choosen according the angle's sign). Otherwise, if <code>obj</code> is a
     * {@link Longitude} object, then a symbol "E" or "W" will be appended to the
     * end of the string. Otherwise, no hemisphere symbol will be appended.
     * <br><br>
     * Strictly speaking, formatting ordinary numbers is not the
     * <code>AngleFormat</code>'s job. Nevertheless, this method
     * accept {@link Number} objects. This capability is provided
     * only as a convenient way to format altitude numbers together
     * with longitude and latitude angles.
     *
     * @param  obj        {@link Angle} or {@link Number} object to format.
     * @param  toAppendTo Where the text is to be appended.
     * @param  pos        An optional {@link FieldPosition} identifying a field
     *                    in the formatted text, or <code>null</code> if this
     *                    information is not wanted. This field position shall
     *                    be constructed with one of the following constants:
     *                    {@link #DEGREES_FIELD},
     *                    {@link #MINUTES_FIELD},
     *                    {@link #SECONDS_FIELD} or
     *                    {@link #HEMISPHERE_FIELD}.
     *
     * @return The string buffer passed in as <code>toAppendTo</code>, with
     *         formatted text appended.
     * @throws IllegalArgumentException if <code>obj</code> if not an object
     *         of class {@link Angle} or {@link Number}.
     */
    public synchronized StringBuffer format(final Object obj,
                                            StringBuffer toAppendTo,
                                            final FieldPosition pos)
        throws IllegalArgumentException
    {
        if (obj instanceof Latitude) {
            return format(((Latitude) obj).degrees(), toAppendTo, pos, NORTH, SOUTH);
        }
        if (obj instanceof Longitude) {
            return format(((Longitude) obj).degrees(), toAppendTo, pos, EAST, WEST);
        }
        if (obj instanceof Angle) {
            return format(((Angle) obj).degrees(), toAppendTo, pos);
        }
        if (obj instanceof Number) {
            numberFormat.setMinimumIntegerDigits(1);
            numberFormat.setMinimumFractionDigits(0);
            numberFormat.setMaximumFractionDigits(2);
            return numberFormat.format(obj, toAppendTo, (pos!=null) ? pos : dummy);
        }
        throw new IllegalArgumentException(Resources.format(
                                           ResourceKeys.ERROR_NOT_AN_ANGLE_OBJECT_$1,
                                           Utilities.getShortClassName(obj)));
    }
    
    /**
     * Proc�de � l'�criture d'un angle, d'une latitude ou d'une longitude.
     *
     * @param  type       Type de l'angle ou du nombre:
     *                    {@link #LONGITUDE},
     *                    {@link #LATITUDE} ou
     *                    {@link #ALTITUDE}.
     * @param  toAppendTo Buffer dans lequel �crire l'angle.
     * @param  pos        En entr�, le code du champs dont on d�sire les index
     *                    ({@link #DEGREES_FIELD},
     *                     {@link #MINUTES_FIELD},
     *                     {@link #SECONDS_FIELD} ou
     *                     {@link #HEMISPHERE_FIELD}).
     *                    En sortie, les index du champs demand�. Ce param�tre
     *                    peut �tre nul si cette information n'est pas d�sir�e.
     *
     * @return Le buffer <code>toAppendTo</code> par commodit�.
     */
    synchronized StringBuffer format(final double number, final int type,
                                     StringBuffer toAppendTo,
                                     final FieldPosition pos)
    {
        switch (type) {
            default:        throw new IllegalArgumentException(Integer.toString(type)); // Should not happen.
            case LATITUDE:  return format(number, toAppendTo, pos, NORTH, SOUTH);
            case LONGITUDE: return format(number, toAppendTo, pos, EAST,  WEST );
            case ALTITUDE: {
                numberFormat.setMinimumIntegerDigits(1);
                numberFormat.setMinimumFractionDigits(0);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat.format(number, toAppendTo, (pos!=null) ? pos : dummy);
            }
        }
    }
    
    /**
     * Proc�de � l'�criture d'un angle suivit d'un suffix 'N','S','E' ou 'W'.
     * L'angle sera format� en utilisant comme mod�le le patron sp�cifi� lors
     * du dernier appel de la m�thode {@link #applyPattern}.
     *
     * @param  angle      Angle � �crire, en degr�s.
     * @param  toAppendTo Buffer dans lequel �crire l'angle.
     * @param  pos        En entr�, le code du champs dont on d�sire les index
     *                    ({@link #DEGREES_FIELD},
     *                     {@link #MINUTES_FIELD},
     *                     {@link #SECONDS_FIELD} ou
     *                     {@link #HEMISPHERE_FIELD}).
     *                    En sortie, les index du champs demand�. Ce param�tre
     *                    peut �tre nul si cette information n'est pas d�sir�e.
     * @param north       Caract�res � �crire si l'angle est positif ou nul.
     * @param south       Caract�res � �crire si l'angle est n�gatif.
     *
     * @return Le buffer <code>toAppendTo</code> par commodit�.
     */
    private StringBuffer format(final double angle,
                                StringBuffer toAppendTo,
                                final FieldPosition pos,
                                final char north, final char south)
    {
        toAppendTo = format(Math.abs(angle), toAppendTo, pos);
        final int start = toAppendTo.length();
        toAppendTo.append(angle<0 ? south : north);
        if (pos!=null && pos.getField()==HEMISPHERE_FIELD) {
            pos.setBeginIndex(start);
            pos.setEndIndex(toAppendTo.length()-1);
        }
        return toAppendTo;
    }
    
    /**
     * Ignore le suffix d'un nombre. Cette m�thode est appell�e par la m�thode
     * {@link #parse} pour savoir quel champs il vient de lire. Par exemple si
     * l'on vient de lire les degr�s dans "48�12'", alors cette m�thode extraira
     * le "�" et retournera 0 pour indiquer que l'on vient de lire des degr�s.
     *
     * Cette m�thode se chargera d'ignorer les espaces qui pr�c�dent le suffix.
     * Elle tentera ensuite de d'abord interpr�ter le suffix selon les symboles
     * du patron (sp�cifi� avec {@link #applyPattern}. Si le suffix n'a pas �t�
     * reconnus, elle tentera ensuite de le comparer aux symboles standards
     * (� ' ").
     *
     * @param  source Cha�ne dans laquelle on doit sauter le suffix.
     * @param  pos En entr�, l'index du premier caract�re � consid�rer dans la
     *         cha�ne <code>pos</code>. En sortie, l'index du premier caract�re
     *         suivant le suffix (c'est-�-dire index � partir d'o� continuer la
     *         lecture apr�s l'appel de cette m�thode). Si le suffix n'a pas �t�
     *         reconnu, alors cette m�thode retourne par convention <code>SYMBOLS.length</code>.
     * @param  field Champs � v�rifier de pr�f�rences. Par exemple la valeur 1 signifie que les
     *         suffix des minutes et des secondes devront �tre v�rifi�s avant celui des degr�s.
     * @return Le num�ro du champs correspondant au suffix qui vient d'�tre extrait:
     *         -1 pour le pr�fix de l'angle, 0 pour le suffix des degr�s, 1 pour le
     *         suffix des minutes et 2 pour le suffix des secondes. Si le texte n'a
     *         pas �t� reconnu, retourne <code>SYMBOLS.length</code>.
     */
    private int skipSuffix(final String source, final ParsePosition pos, int field) {
        /*
         * Essaie d'abord de sauter les suffix qui
         * avaient �t� sp�cifi�s dans le patron.
         */
        final int length=source.length();
        int start=pos.getIndex();
        for (int j=SYMBOLS.length; j>=0; j--) { // C'est bien j>=0 et non j>0.
            int index=start;
            final String toSkip=getSuffix(field);
            if (toSkip!=null) {
                final int toSkipLength=toSkip.length();
                do {
                    if (source.regionMatches(index, toSkip, 0, toSkipLength)) {
                        pos.setIndex(index+toSkipLength);
                        return field;
                    }
                }
                while (index<length && Character.isSpaceChar(source.charAt(index++)));
            }
            if (++field >= SYMBOLS.length) field=-1;
        }
        /*
         * Le texte trouv� ne correspondant � aucun suffix du patron,
         * essaie maintenant de sauter un des suffix standards (apr�s
         * avoir ignor� les espaces qui le pr�c�daient).
         */
        char c;
        do {
            if (start>=length) {
                return SYMBOLS.length;
            }
        }
        while (Character.isSpaceChar(c=source.charAt(start++)));
        switch (c) {
            case '�' : pos.setIndex(start); return DEGREES_FIELD;
            case '\'': pos.setIndex(start); return MINUTES_FIELD;
            case '"' : pos.setIndex(start); return SECONDS_FIELD;
            default  : return SYMBOLS.length; // Unknow field.
        }
    }
    
    /**
     * Parse a string as an angle. This method can parse an angle even if it
     * doesn't comply exactly to the expected pattern. For example, this method
     * will parse correctly string "<code>48�12.34'</code>" even if the expected
     * pattern was "<code>DDMM.mm</code>" (i.e. the string should have been
     * "<code>4812.34</code>"). Spaces between degrees, minutes and secondes
     * are ignored. If the string ends with an hemisphere symbol "N" or "S",
     * then this method returns an object of class {@link Latitude}. Otherwise,
     * if the string ends with an hemisphere symbol "E" or "W", then this method
     * returns an object of class {@link Longitude}. Otherwise, this method
     * returns an object of class {@link Angle}.
     *
     * @param source A String whose beginning should be parsed.
     * @param pos    Position where to start parsing.
     * @return       The parsed string as an {@link Angle}, {@link Latitude}
     *               or {@link Longitude} object.
     */
    public Angle parse(final String source, final ParsePosition pos) {
        return parse(source, pos, false);
    }
    
    /**
     * Interpr�te une cha�ne de caract�res repr�sentant un angle. Les r�gles
     * d'interpr�tation de cette m�thode sont assez souples. Par exemple cettte
     * m�thode interpr�tera correctement la cha�ne "48�12.34'" m�me si le patron
     * attendu �tait "DDMM.mm" (c'est-�-dire que la cha�ne aurait du �tre "4812.34").
     * Les espaces entre les degr�s, minutes et secondes sont accept�s. Si l'angle
     * est suivit d'un symbole "N" ou "S", alors l'objet retourn� sera de la classe
     * {@link Latitude}. S'il est plutot suivit d'un symbole "E" ou "W", alors l'objet
     * retourn� sera de la classe {@link Longitude}. Sinon, il sera de la classe
     * {@link Angle}.
     *
     * @param source           Cha�ne de caract�res � lire.
     * @param pos              Position � partir d'o� interpr�ter la cha�ne.
     * @param spaceAsSeparator Indique si l'espace est accept� comme s�parateur
     *                         � l'int�rieur d'un angle. La valeur <code>true</code>
     *                         fait que l'angle "45 30" sera interpr�t� comme "45�30".
     * @return L'angle lu.
     */
    private synchronized Angle parse(final String source,
                                     final ParsePosition pos,
                                     final boolean spaceAsSeparator)
    {
        double degrees   = Double.NaN;
        double minutes  = Double.NaN;
        double secondes = Double.NaN;
        final int length=source.length();
        ///////////////////////////////////////////////////////////////////////////////
        // BLOC A: Analyse la cha�ne de caract�res 'source' et affecte aux variables //
        //         'degr�s', 'minutes' et 'secondes' les valeurs appropri�es.        //
        //         Les premi�res accolades ne servent qu'� garder locales            //
        //         les variables sans int�r�t une fois la lecture termin�e.          //
        ///////////////////////////////////////////////////////////////////////////////
        {
            /*
             * Extrait le pr�fix, s'il y en avait un. Si on tombe sur un symbole des
             * degr�s, minutes ou secondes alors qu'on n'a pas encore lu de nombre,
             * on consid�rera que la lecture a �chou�e.
             */
            final int indexStart=pos.getIndex();
            int index=skipSuffix(source, pos, PREFIX_FIELD);
            if (index>=0 && index<SYMBOLS.length) {
                pos.setErrorIndex(indexStart);
                pos.setIndex(indexStart);
                return null;
            }
            /*
             * Saute les espaces blancs qui
             * pr�c�dent le champs des degr�s.
             */
            index=pos.getIndex();
            while (index<length && Character.isSpaceChar(source.charAt(index))) index++;
            pos.setIndex(index);
            /*
             * Lit les degr�s. Notez que si aucun s�parateur ne s�parait les degr�s
             * des minutes des secondes, alors cette lecture pourra inclure plusieurs
             * champs (exemple: "DDDMMmmm"). La s�paration sera faite plus tard.
             */
            Number fieldObject=numberFormat.parse(source, pos);
            if (fieldObject==null) {
                pos.setIndex(indexStart);
                if (pos.getErrorIndex()<indexStart) {
                    pos.setErrorIndex(index);
                }
                return null;
            }
            degrees=fieldObject.doubleValue();
            int indexEndField=pos.getIndex();
            boolean swapDM=true;
BigBoss:    switch (skipSuffix(source, pos, DEGREES_FIELD)) {
                /* ----------------------------------------------
                 * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�S DEGR�S
                 * ----------------------------------------------
                 * Les degr�s �taient suivit du pr�fix d'un autre angle. Le pr�fix sera donc
                 * retourn� dans le buffer pour un �ventuel traitement par le prochain appel
                 * � la m�thode 'parse' et on n'ira pas plus loin dans l'analyse de la cha�ne.
                 */
                case PREFIX_FIELD: {
                    pos.setIndex(indexEndField);
                    break BigBoss;
                }
                /* ----------------------------------------------
                 * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�S DEGR�S
                 * ----------------------------------------------
                 * On a trouv� le symbole des secondes au lieu de celui des degr�s. On fait
                 * la correction dans les variables 'degr�s' et 'secondes' et on consid�re
                 * que la lecture est termin�e.
                 */
                case SECONDS_FIELD: {
                    secondes = degrees;
                    degrees = Double.NaN;
                    break BigBoss;
                }
                /* ----------------------------------------------
                 * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�S DEGR�S
                 * ----------------------------------------------
                 * Aucun symbole ne suit les degr�s. Des minutes sont-elles attendues?
                 * Si oui, on fera comme si le symbole des degr�s avait �t� l�. Sinon,
                 * on consid�rera que la lecture est termin�e.
                 */
                default: {
                    if (width1==0)         break BigBoss;
                    if (!spaceAsSeparator) break BigBoss;
                    // fall through
                }
                /* ----------------------------------------------
                 * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�S DEGR�S
                 * ----------------------------------------------
                 * Un symbole des degr�s a �t� explicitement trouv�. Les degr�s sont peut-�tre
                 * suivit des minutes. On proc�dera donc � la lecture du prochain nombre, puis
                 * � l'analyse du symbole qui le suit.
                 */
                case DEGREES_FIELD: {
                    final int indexStartField = index = pos.getIndex();
                    while (index<length && Character.isSpaceChar(source.charAt(index))) {
                        index++;
                    }
                    if (!spaceAsSeparator && index!=indexStartField) {
                        break BigBoss;
                    }
                    pos.setIndex(index);
                    fieldObject=numberFormat.parse(source, pos);
                    if (fieldObject==null) {
                        pos.setIndex(indexStartField);
                        break BigBoss;
                    }
                    indexEndField = pos.getIndex();
                    minutes = fieldObject.doubleValue();
                    switch (skipSuffix(source, pos, (width1!=0) ? MINUTES_FIELD : PREFIX_FIELD)) {
                        /* ------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES MINUTES
                         * ------------------------------------------------
                         * Le symbole trouv� est bel et bien celui des minutes.
                         * On continuera le bloc pour tenter de lire les secondes.
                         */
                        case MINUTES_FIELD: {
                            break; // continue outer switch
                        }
                        /* ------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES MINUTES
                         * ------------------------------------------------
                         * Un symbole des secondes a �t� trouv� au lieu du symbole des minutes
                         * attendu. On fera la modification dans les variables 'secondes' et
                         * 'minutes' et on consid�rera la lecture termin�e.
                         */
                        case SECONDS_FIELD: {
                            secondes = minutes;
                            minutes = Double.NaN;
                            break BigBoss;
                        }
                        /* ------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES MINUTES
                         * ------------------------------------------------
                         * Aucun symbole n'a �t� trouv�. Les minutes �taient-elles attendues?
                         * Si oui, on les acceptera et on tentera de lire les secondes. Si non,
                         * on retourne le texte lu dans le buffer et on termine la lecture.
                         */
                        default: {
                            if (width1!=0) break; // Continue outer switch
                            // fall through
                        }
                        /* ------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES MINUTES
                         * ------------------------------------------------
                         * Au lieu des minutes, le symbole lu est celui des degr�s. On consid�re
                         * qu'il appartient au prochain angle. On retournera donc le texte lu dans
                         * le buffer et on terminera la lecture.
                         */
                        case DEGREES_FIELD: {
                            pos.setIndex(indexStartField);
                            minutes=Double.NaN;
                            break BigBoss;
                        }
                        /* ------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES MINUTES
                         * ------------------------------------------------
                         * Apr�s les minutes (qu'on accepte), on a trouv� le pr�fix du prochain
                         * angle � lire. On retourne ce pr�fix dans le buffer et on consid�re la
                         * lecture termin�e.
                         */
                        case PREFIX_FIELD: {
                            pos.setIndex(indexEndField);
                            break BigBoss;
                        }
                    }
                    swapDM=false;
                    // fall through
                }
                /* ----------------------------------------------
                 * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�S DEGR�S
                 * ----------------------------------------------
                 * Un symbole des minutes a �t� trouv� au lieu du symbole des degr�s attendu.
                 * On fera donc la modification dans les variables 'degr�s' et 'minutes'. Ces
                 * minutes sont peut-�tre suivies des secondes. On tentera donc de lire le
                 * prochain nombre.
                 */
                case MINUTES_FIELD: {
                    if (swapDM) {
                        minutes = degrees;
                        degrees = Double.NaN;
                    }
                    final int indexStartField = index = pos.getIndex();
                    while (index<length && Character.isSpaceChar(source.charAt(index))) {
                        index++;
                    }
                    if (!spaceAsSeparator && index!=indexStartField) {
                        break BigBoss;
                    }
                    pos.setIndex(index);
                    fieldObject=numberFormat.parse(source, pos);
                    if (fieldObject==null) {
                        pos.setIndex(indexStartField);
                        break;
                    }
                    indexEndField = pos.getIndex();
                    secondes = fieldObject.doubleValue();
                    switch (skipSuffix(source, pos, (width2!=0) ? MINUTES_FIELD : PREFIX_FIELD)) {
                        /* -------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES SECONDES
                         * -------------------------------------------------
                         * Un symbole des secondes explicite a �t� trouv�e.
                         * La lecture est donc termin�e.
                         */
                        case SECONDS_FIELD: {
                            break;
                        }
                        /* -------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES SECONDES
                         * -------------------------------------------------
                         * Aucun symbole n'a �t� trouv�e. Attendait-on des secondes? Si oui, les
                         * secondes seront accept�es. Sinon, elles seront retourn�es au buffer.
                         */
                        default: {
                            if (width2!=0) break;
                            // fall through
                        }
                        /* -------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES SECONDES
                         * -------------------------------------------------
                         * Au lieu des degr�s, on a trouv� un symbole des minutes ou des
                         * secondes. On renvoie donc le nombre et son symbole dans le buffer.
                         */
                        case MINUTES_FIELD:
                        case DEGREES_FIELD: {
                            pos.setIndex(indexStartField);
                            secondes=Double.NaN;
                            break;
                        }
                        /* -------------------------------------------------
                         * ANALYSE DU SYMBOLE SUIVANT LES PR�SUM�ES SECONDES
                         * -------------------------------------------------
                         * Apr�s les secondes (qu'on accepte), on a trouv� le pr�fix du prochain
                         * angle � lire. On retourne ce pr�fix dans le buffer et on consid�re la
                         * lecture termin�e.
                         */
                        case PREFIX_FIELD: {
                            pos.setIndex(indexEndField);
                            break BigBoss;
                        }
                    }
                    break;
                }
            }
        }
        ////////////////////////////////////////////////////////////////////
        // BLOC B: Prend en compte l'�ventualit� ou le s�parateur d�cimal //
        //         aurrait �t� absent, puis calcule l'angle en degr�s.    //
        ////////////////////////////////////////////////////////////////////
        if (minutes<0) {
            secondes = -secondes;
        }
        if (degrees<0) {
            minutes = -minutes;
            secondes = -secondes;
        }
        if (!decimalSeparator) {
            final double facteur=XMath.pow10(widthDecimal);
            if (width2!=0) {
                if (suffix1==null && Double.isNaN(secondes)) {
                    if (suffix0==null && Double.isNaN(minutes)) {
                        degrees /= facteur;
                    } else {
                        minutes /= facteur;
                    }
                } else {
                    secondes /= facteur;
                }
            } else if (Double.isNaN(secondes)) {
                if (width1!=0) {
                    if (suffix0==null && Double.isNaN(minutes)) {
                        degrees /= facteur;
                    } else {
                        minutes /= facteur;
                    }
                } else if (Double.isNaN(minutes)) {
                    degrees /= facteur;
                }
            }
        }
        /*
         * S'il n'y a rien qui permet de s�parer les degr�s des minutes (par exemple si
         * le patron est "DDDMMmmm"), alors la variable 'degr�s' englobe � la fois les
         * degr�s, les minutes et d'�ventuelles secondes. On applique une correction ici.
         */
        if (suffix1==null && width2!=0 && Double.isNaN(secondes)) {
            double facteur = XMath.pow10(width2);
            if (suffix0==null && width1!=0 && Double.isNaN(minutes)) {
                ///////////////////
                //// DDDMMSS.s ////
                ///////////////////
                secondes = degrees;
                minutes  = (int) (degrees/facteur); // Arrondie vers 0
                secondes -= minutes*facteur;
                facteur  = XMath.pow10(width1);
                degrees   = (int) (minutes/facteur); // Arrondie vers 0
                minutes -= degrees*facteur;
            } else {
                ////////////////////
                //// DDD�MMSS.s ////
                ////////////////////
                secondes = minutes;
                minutes = (int) (minutes/facteur); // Arrondie vers 0
                secondes -= minutes*facteur;
            }
        } else if (suffix0==null && width1!=0 && Double.isNaN(minutes)) {
            /////////////////
            //// DDDMM.m ////
            /////////////////
            final double facteur = XMath.pow10(width1);
            minutes = degrees;
            degrees = (int) (degrees/facteur); // Arrondie vers 0
            minutes -= degrees*facteur;
        }
        pos.setErrorIndex(-1);
        if ( Double.isNaN(degrees))   degrees=0;
        if (!Double.isNaN(minutes))  degrees += minutes/60;
        if (!Double.isNaN(secondes)) degrees += secondes/3600;
        /////////////////////////////////////////////////////
        // BLOC C: V�rifie maintenant si l'angle ne serait //
        //         pas suivit d'un symbole N, S, E ou W.   //
        /////////////////////////////////////////////////////
        for (int index=pos.getIndex(); index<length; index++) {
            final char c=source.charAt(index);
            switch (Character.toUpperCase(c)) {
                case NORTH: pos.setIndex(index+1); return new Latitude( degrees);
                case SOUTH: pos.setIndex(index+1); return new Latitude(-degrees);
                case EAST : pos.setIndex(index+1); return new Longitude( degrees);
                case WEST : pos.setIndex(index+1); return new Longitude(-degrees);
            }
            if (!Character.isSpaceChar(c)) {
                break;
            }
        }
        return new Angle(degrees);
    }
    
    /**
     * Parse a string as an angle.
     *
     * @param  source The string to parse.
     * @return The parsed string as an {@link Angle}, {@link Latitude}
     *         or {@link Longitude} object.
     * @throws ParseException if the string has not been fully parsed.
     */
    public Angle parse(final String source) throws ParseException {
        final ParsePosition pos = new ParsePosition(0);
        final Angle         ang = parse(source, pos, true);
        checkComplete(source, pos, false);
        return ang;
    }
    
    /**
     * Parse a substring as an angle. Default implementation invokes
     * {@link #parse(String, ParsePosition)}.
     *
     * @param source A String whose beginning should be parsed.
     * @param pos    Position where to start parsing.
     * @return       The parsed string as an {@link Angle},
     *               {@link Latitude} or {@link Longitude} object.
     */
    public Object parseObject(final String source, final ParsePosition pos) {
        return parse(source, pos);
    }
    
    /**
     * Parse a string as an object. Default implementation invokes
     * {@link #parse(String)}.
     *
     * @param  source The string to parse.
     * @return The parsed string as an {@link Angle}, {@link Latitude} or
     *        {@link Longitude} object.
     * @throws ParseException if the string has not been fully parsed.
     */
    public Object parseObject(final String source) throws ParseException {
        return parse(source);
    }
    
    /**
     * Interpr�te une cha�ne de caract�res qui devrait repr�senter un nombre.
     * Cette m�thode est utile pour lire une altitude apr�s les angles.
     *
     * @param  source Cha�ne de caract�res � interpr�ter.
     * @param  pos    Position � partir d'o� commencer l'interpr�tation
     *                de la cha�ne <code>source</code>.
     * @return Le nombre lu comme objet {@link Number}.
     */
    final Number parseNumber(final String source, final ParsePosition pos) {
        return numberFormat.parse(source, pos);
    }
    
    /**
     * V�rifie si l'interpr�tation d'une cha�ne de caract�res a �t� compl�te.
     * Si ce n'�tait pas le cas, lance une exception avec un message d'erreur
     * soulignant les caract�res probl�matiques.
     *
     * @param  source Cha�ne de caract�res qui �tait � interpr�ter.
     * @param  pos Position � laquelle s'est termin�e l'interpr�tation de la
     *         cha�ne <code>source</code>.
     * @param  isCoordinate <code>false</code> si on interpr�tait un angle,
     *         ou <code>true</code> si on interpr�tait une coordonn�e.
     * @throws ParseException Si la cha�ne <code>source</code> n'a pas �t�
     *         interpr�t�e dans sa totalit�.
     */
    static void checkComplete(final String source,
                              final ParsePosition pos,
                              final boolean isCoordinate)
        throws ParseException
    {
        final int length=source.length();
        final int origin=pos.getIndex();
        for (int index=origin; index<length; index++) {
            if (!Character.isWhitespace(source.charAt(index))) {
                index=pos.getErrorIndex(); if (index<0) index=origin;
                int lower=index;
                while (lower<length && Character.isWhitespace(source.charAt(lower))) {
                    lower++;
                }
                int upper=lower;
                while (upper<length && !Character.isWhitespace(source.charAt(upper))) {
                    upper++;
                }
                throw new ParseException(Resources.format(
                            ResourceKeys.ERROR_PARSE_ANGLE_EXCEPTION_$2, source,
                            source.substring(lower, Math.min(lower+10, upper))), index);
            }
        }
    }
    
    /**
     * Returns a "hash value" for this object.
     */
    public synchronized int hashCode() {
        int c = 78236951;
        if (decimalSeparator) c^= 0xFF;
        if (prefix  !=null)   c^=         prefix.hashCode();
        if (suffix0 !=null)   c = c*37 + suffix0.hashCode();
        if (suffix1 !=null)   c^= c*37 + suffix1.hashCode();
        if (suffix2 !=null)   c^= c*37 + suffix2.hashCode();
        return c ^ (((((width0 << 8) ^ width1) << 8) ^ width2) << 8) ^ widthDecimal;
    }
    
    /**
     * Compare this format with the specified object for equality.
     */
    public synchronized boolean equals(final Object obj) {
        // On ne peut pas synchroniser "obj" si on ne veut
        // pas risquer un "deadlock". Voir RFE #4210659.
        if (obj==this) {
            return true;
        }
        if (obj!=null && getClass().equals(obj.getClass())) {
            final  AngleFormat cast = (AngleFormat) obj;
            return width0           == cast.width0            &&
                   width1           == cast.width1            &&
                   width2           == cast.width2            &&
                   widthDecimal     == cast.widthDecimal      &&
                   decimalSeparator == cast.decimalSeparator  &&
                   Utilities.equals(prefix,    cast.prefix )  &&
                   Utilities.equals(suffix0,   cast.suffix0)  &&
                   Utilities.equals(suffix1,   cast.suffix1)  &&
                   Utilities.equals(suffix2,   cast.suffix2)  &&
                   Utilities.equals(numberFormat.getDecimalFormatSymbols(),
                               cast.numberFormat.getDecimalFormatSymbols());
        } 
        return false;
        
    }
    
    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        return Utilities.getShortClassName(this)+'['+toPattern()+']';
    }
}
