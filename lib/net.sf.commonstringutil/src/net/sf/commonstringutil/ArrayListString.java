/* Copyright (C) 2011 Muhammad Edwin & Natalino Nugeraha
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The name "FastStringUtility" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission.

 4. Products derived from this software may not be called "FastStringUtility", nor
    may "FastStringUtility" appear in their name, without prior written permission
    from the FastStringUtility Project Management.

 In addition, we request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      FastStringUtil Project (http://www.baculsoft.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many
 individuals on behalf of the FastStringUtil Project and was originally
 created by Natalino Nugeraha Putrama <nugie@baculsoft.com> and
 Muhammad Edwin <edwinkun@gmail.com>.  For more information
 on the FastStringUtil Project, please see <http://www.baculsoft.com/>.
 */

package net.sf.commonstringutil;

/**
 * 
 * @author Natalino Nugeraha
 * @see java.util.ArrayList
 * @version 1.0.0
 */

final class ArrayListString {

	private transient String[] dt;
	private transient int sz;

	/**
	 * 
	 * @param initialCapacity
	 */
	ArrayListString(final int initialCapacity) {
		super();
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		}
		this.dt = new String[initialCapacity];
	}

	/**
	 *  initialCapacity default 20
	 */
	ArrayListString() {
		this(20);
	}

	/**
	 * 
	 * @param data
	 */
	ArrayListString(final String[] data, int size) {
		this.dt = data;
		this.sz = size;
	}

	/**
	 * 
	 * @param minCapacity
	 */
	final void ensureCapacity(final int minCapacity) {
		final int oldCapacity = dt.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			final String[] s = new String[newCapacity];
			System.arraycopy(dt, 0, s, 0, dt.length);
			dt = s;
		}
	}

	/**
	 * 
	 * @return size
	 */
	final int size() {
		return sz;
	}

	/**
	 * 
	 * @return size is zero
	 */
	final boolean isEmpty() {
		return sz == 0;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	final boolean contains(final String o) {
		return indexOf(o) >= 0;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	final int indexOf(final String o) {
		if (o == null) {
			for (int i = 0; i < sz; i++) {
				if (dt[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < sz; i++) {
				if (o.equals(dt[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	final int lastIndexOf(final String o) {
		if (o == null) {
			for (int i = sz - 1; i >= 0; i--) {
				if (dt[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = sz - 1; i >= 0; i--) {
				if (o.equals(dt[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return trimsize of all string array 
	 */
	final String[] toArray() {
		final String[] s = new String[sz];
		System.arraycopy(dt, 0, s, 0, sz);
		return s;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	final String get(final int index) {
		rangeCheck(index);
		return dt[index];
	}

	/**
	 * 
	 * @param index
	 * @param element
	 * @return
	 */
	final String set(final int index, final String element) {
		rangeCheck(index);
		final String oldValue = dt[index];
		dt[index] = element;
		return oldValue;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	final boolean add(final char e) {
		ensureCapacity(sz + 1);
		dt[sz++] = new String(new char[] { e });
		return true;
	}

	/**
	 * 
	 * @param arr
	 * @return
	 */
	final boolean add(final String[] arr) {
		final int len = arr.length;
		final int tmp = sz + len;
		ensureCapacity(tmp);
		System.arraycopy(arr, 0, dt, sz, len);
		sz = tmp;
		return true;
	}
        
        
	final ArrayListString append(final String[] arr) {
		final int len = arr.length;
		final int tmp = sz + len;
		ensureCapacity(tmp);
		System.arraycopy(arr, 0, dt, sz, len);
		sz = tmp;
		return this;
	}
        

	/**
	 * 
	 * @param e
	 * @return
	 */
	final boolean add(final String e) {
		ensureCapacity(sz + 1);
		dt[sz++] = e;
		return true;
	}

	/**
	 * 
	 * @param index
	 * @param element
	 */
	final void add(final int idx, final String element) {
		ensureCapacity(sz + 1); // Increments modCount!!
		System.arraycopy(dt, idx, dt, idx + 1, sz - idx);
		dt[idx] = element;
		sz++;
	}

	/**
	 * 
	 * @param idx
	 * @return
	 */
	final String remove(final int idx) {
		rangeCheck(idx);

		final String oldValue = dt[idx];
		final int numMoved = sz - idx - 1;
		if (numMoved > 0) {
			System.arraycopy(dt, idx + 1, dt, idx, numMoved);
		}
		dt[--sz] = null;
		return oldValue;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	final boolean remove(final String o) {
		if (o == null) {
			for (int idx = 0; idx < sz; idx++) {
				if (dt[idx] == null) {
					fastRemove(idx);
					return true;
				}
			}
		} else {
			for (int idx = 0; idx < sz; idx++) {
				if (o.equals(dt[idx])) {
					fastRemove(idx);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param idx
	 */
	private final void fastRemove(final int idx) {
		int numMoved = sz - idx - 1;
		if (numMoved > 0) {
			System.arraycopy(dt, idx + 1, dt, idx, numMoved);
		}
		dt[--sz] = null;
	}

	/**
	 * clear all string array
	 */
	final void clear() {
		for (int i = 0; i < sz; i++) {
			dt[i] = null;
		}

		sz = 0;
	}

	/**
	 * 
	 * @param fromIndex
	 * @param toIndex
	 */
	final void removeRange(final int fromIndex, final int toIndex) {
		final int numMoved = sz - toIndex;
		System.arraycopy(dt, toIndex, dt, fromIndex, numMoved);

		int newsz = sz - (toIndex - fromIndex);
		while (sz != newsz) {
			dt[--sz] = null;
		}
	}

	/**
	 * 
	 * @param idx
	 */
	private final void rangeCheck(final int idx) {
		if (idx >= sz) {
			throw new IndexOutOfBoundsException("Index: " + idx + ", sz: " + sz);
		}
	}

	/**
	 * 
	 * @return merge all string array
	 */
	final String merge() {
		final StringBuilder builder = new StringBuilder(400);
		for (int i = 0; i < sz; i++) {
			builder.append(dt[i]);
		}
		return builder.toString();
	}

	@Override
	public final int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public final boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public final Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
