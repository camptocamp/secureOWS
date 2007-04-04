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
package org.deegree.model.csct.resources;

// Collections
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A set of object hold by weak references.
 * This class is used to implements caches.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
public class WeakHashSet extends AbstractSet {
    /**
     * A weak reference to an element.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    private final class WeakElement extends WeakReference {
        /**
         * The next entry, or <code>null</code> if there is none.
         */
        WeakElement next;

        /**
         * Index for this element in {@link #table}. This index
         * must be updated at every {@link #rehash} call.
         */
        int index;

        /**
         * Construct a new weak reference.
         */
        WeakElement( final Object obj, final WeakElement next, final int index ) {
            super( obj, referenceQueue );
            this.next = next;
            this.index = index;
        }

        /**
         * Clear the reference.
         */
        public void clear() {
            super.clear();
            remove( this );
        }
    }

    /**
     * Minimal capacity for {@link #table}.
     */
    private static final int MIN_CAPACITY = 7;

    /**
     * Load factor. Control the moment
     * where {@link #table} must be rebuild.
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * List of reference collected by the garbage collector.
     * Those elements must be removed from {@link #table}.
     */
    private static final ReferenceQueue referenceQueue = new ReferenceQueue();

    /**
     * Background thread removing references
     * collected by the garbage collector.
     */
    private static final Thread thread = new Thread( "WeakHashSet" ) {
        public void run() {
            while ( true )
                try {
                    referenceQueue.remove().clear();
                } catch ( InterruptedException exception ) {
                    // Somebody doesn't want to lets
                    // us sleep... Go back to work.
                } catch ( Exception exception ) {
                    exception.printStackTrace();
                }
        }
    };
    static {
        thread.setDaemon( true );
        thread.start();
    }

    /**
     * Table of weak references.
     */
    private WeakElement[] table;

    /**
     * Number of non-nul elements in {@link #table}.
     */
    private int count;

    /**
     * The next size value at which to resize. This value should
     * be <code>{@link #table}.length*{@link #loadFactor}</code>.
     */
    private int threshold;

    /**
     * The timestamp when {@link #table} was last rehashed. This information
     * is used to avoir too early table reduction. When the garbage collector
     * collected a lot of elements, we will wait at least 20 seconds before
     * rehashing {@link #table} in order to avoir to many cycles "reduce",
     * "expand", "reduce", "expand", etc.
     */
    private long lastRehashTime;

    /**
     * Number of millisecond to wait before to rehash
     * the table for reducing its size.
     */
    private static final long HOLD_TIME = 20 * 1000L;

    /**
     * Construct a <code>WeakHashSet</code>.
     */
    public WeakHashSet() {
        table = new WeakElement[MIN_CAPACITY];
        threshold = Math.round( table.length * LOAD_FACTOR );
        lastRehashTime = System.currentTimeMillis();
    }

    /**
     * Invoked by {@link WeakElement} when an element has been collected
     * by the garbage collector. This method will remove the weak reference
     * from {@link #table}.
     */
    private synchronized void remove( final WeakElement toRemove ) {
        final int i = toRemove.index;
        // Index 'i' may not be valid if the reference 'toRemove'
        // has been already removed in a previous rehash.
        if ( i < table.length ) {
            WeakElement prev = null;
            WeakElement e = table[i];
            while ( e != null ) {
                if ( e == toRemove ) {
                    if ( prev != null )
                        prev.next = e.next;
                    else
                        table[i] = e.next;
                    count--;

                    // If the number of elements has dimunished
                    // significatively, rehash the table.
                    if ( count <= threshold / 4 )
                        rehash( false );
                    // We must not continue the loop, since
                    // variable 'e' is no longer valid.
                    return;
                }
                prev = e;
                e = e.next;
            }
        }
        /*
         * If we reach this point, its mean that reference 'toRemove' has not
         * been found. This situation may occurs if 'toRemove' has already been
         * removed in a previous run of {@link #rehash}.
         */
    }

    /**
     * Rehash {@link #table}.
     *
     * @param augmentation <code>true</code> if this method is invoked
     *        for augmenting {@link #table}, or <code>false</code> if
     *        it is invoked for making the table smaller.
     */
    private void rehash( final boolean augmentation ) {
        final long currentTime = System.currentTimeMillis();
        final int capacity = Math.max( Math.round( count / ( LOAD_FACTOR / 2 ) ), count
                                                                                  + MIN_CAPACITY );
        if ( augmentation ? ( capacity <= table.length )
                         : ( capacity >= table.length || currentTime - lastRehashTime < HOLD_TIME ) ) {
            return;
        }
        lastRehashTime = currentTime;
        final WeakElement[] oldTable = table;
        table = new WeakElement[capacity];
        threshold = Math.round( capacity * LOAD_FACTOR );
        for ( int i = 0; i < oldTable.length; i++ ) {
            for ( WeakElement old = oldTable[i]; old != null; ) {
                final WeakElement e = old;
                old = old.next; // On retient 'next' tout de suite car sa valeur va changer...
                final Object obj_e = e.get();
                if ( obj_e != null ) {
                    final int index = ( hashCode( obj_e ) & 0x7FFFFFFF ) % table.length;
                    e.index = index;
                    e.next = table[index];
                    table[index] = e;
                } else
                    count--;
            }
        }
    }

    /**
     * Returns <code>true</code> if this set contains the specified element.
     *
     * @param  obj Object to be checked for containment in this set.
     * @return <code>true</code> if this set contains the specified element.
     */
    public boolean contains( final Object obj ) {
        return obj == null || get( obj ) != null;
    }

    /**
     * Returns an object equals to the specified object, if present. If
     * this set doesn't contains any object equals to <code>obj</code>,
     * then this method returns <code>null</code>.
     */
    public synchronized final Object get( final Object obj ) {
        if ( obj != null ) {
            final int hash = hashCode( obj ) & 0x7FFFFFFF;
            int index = hash % table.length;
            for ( WeakElement e = table[index]; e != null; e = e.next ) {
                final Object e_obj = e.get();
                if ( e_obj != null ) {
                    if ( equals( obj, e_obj ) )
                        return e_obj;
                }
            }
        }
        return obj;
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * If this set already contains the specified element, the call leaves
     * this set unchanged and returns <code>false</code>.
     *
     * @param  obj Element to be added to this set.
     * @return <code>true</code> if this set did not already
     *         contain the specified element.
     */
    public synchronized boolean add( final Object obj ) {
        return intern0( obj ) == null;
    }

    /**
     * Returns an object equals to <code>obj</code> if such an object already
     * exist in this <code>WeakHashSet</code>. Otherwise, add <code>obj</code>
     * to this <code>WeakHashSet</code>. This method is equivalents to the
     * following code:
     *
     * <blockquote><pre>
     * &nbsp;  if (object!=null)
     * &nbsp;  {
     * &nbsp;      final Object current=get(object);
     * &nbsp;      if (current!=null) return current;
     * &nbsp;      else add(object);
     * &nbsp;  }
     * &nbsp;  return object;
     * </pre></blockquote>
     */
    private Object intern0( final Object obj ) {
        if ( obj != null ) {
            /*
             * Check if <code>obj</code> is already contained in this
             * <code>WeakHashSet</code>. If yes, returns the element.
             */
            final int hash = hashCode( obj ) & 0x7FFFFFFF;
            int index = hash % table.length;
            for ( WeakElement e = table[index]; e != null; e = e.next ) {
                final Object e_obj = e.get();
                if ( e_obj != null ) {
                    if ( equals( obj, e_obj ) )
                        return e_obj;
                }
                // Do not remove the null element; lets "remove" do its job
                // (it was a bug to remove element here as an "optimization")
            }
            /*
             * Check if the table need to be rehashed,
             * and add <code>obj</code> to the table.
             */
            if ( count >= threshold ) {
                rehash( true );
                index = hash % table.length;
            }
            table[index] = new WeakElement( obj, table[index], index );
            count++;
        }
        return obj;
    }

    /**
     * Returns an object equals to <code>obj</code> if such an object already
     * exist in this <code>WeakHashSet</code>. Otherwise, add <code>obj</code>
     * to this <code>WeakHashSet</code>. This method is equivalents to the
     * following code:
     *
     * <blockquote><pre>
     * &nbsp;  if (object!=null)
     * &nbsp;  {
     * &nbsp;      final Object current=get(object);
     * &nbsp;      if (current!=null) return current;
     * &nbsp;      else add(object);
     * &nbsp;  }
     * &nbsp;  return object;
     * </pre></blockquote>
     */
    public synchronized final Object intern( final Object object ) {
        return intern0( object );
    }

    /**
     * Iteratively call {@link #intern(Object)} for an array of objects.
     * This method is equivalents to the following code:
     *
     * <blockquote><pre>
     * &nbsp;  for (int i=0; i<objects.length; i++)
     * &nbsp;      objects[i] = intern(objects[i]);
     * </pre></blockquote>
     */
    public synchronized final void intern( final Object[] objects ) {
        for ( int i = 0; i < objects.length; i++ )
            objects[i] = intern0( objects[i] );
    }

    /**
     * Returns the count of element in this set.
     */
    public synchronized final int size() {
        return count;
    }

    /**
     * Removes all of the elements from this set.
     */
    public synchronized final void clear() {
        Arrays.fill( table, null );
        count = 0;
    }

    /**
     * Returns a view of this set as an array. Elements will be in an arbitrary
     * order. Note that this array contains strong reference.  Consequently, no
     * object reclamation will occurs as long as a reference to this array is hold.
     */
    public synchronized final Object[] toArray() {
        final Object[] elements = new Object[count];
        int index = 0;
        for ( int i = 0; i < table.length; i++ ) {
            for ( WeakElement el = table[i]; el != null; el = el.next ) {
                if ( ( elements[index] = el.get() ) != null )
                    index++;
            }
        }
        return XArray.resize( elements, index );
    }

    /**
     * Returns an iterator over the elements contained in this collection.
     * No element from this set will be garbage collected as long as a
     * reference to the iterator is hold.
     */
    public Iterator iterator() {
        return Arrays.asList( toArray() ).iterator();
    }

    /**
     * Returns a hash code value for the specified object.
     * Default implementation returns {@link Object#hashCode}.
     * Override to compute hash code in a different way.
     */
    protected int hashCode( final Object object ) {
        return ( object != null ) ? object.hashCode() : 0;
    }

    /**
     * Check two objects for equality. This method should be overriden
     * if {@link #hashCode(Object)} has been overriden.
     */
    protected boolean equals( final Object object1, final Object object2 ) {
        return object1 == object2 || ( object1 != null && object1.equals( object2 ) );
    }
}
