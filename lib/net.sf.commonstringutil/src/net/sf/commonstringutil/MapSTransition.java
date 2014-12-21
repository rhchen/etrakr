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
 * @version 1.0.0
 */

final class MapSTransition {

	private transient EntryAutoStatesTrans[] table;
	private transient int size;
	private transient int threshold;
	private transient float loadFactor = 0.75f;
	private static int MAX = Integer.MAX_VALUE;

	public MapSTransition() {
		// TODO Auto-generated constructor stub
		this(16);
	}

	MapSTransition(int initialCapacity) {
		// TODO Auto-generated constructor stub
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal initial capacity: "
					+ initialCapacity);
		}
		if (initialCapacity > MAX) {
			initialCapacity = MAX;
		}
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
			throw new IllegalArgumentException("Illegal load factor: "
					+ loadFactor);
		}

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity) {
			capacity <<= 1;
		}
		threshold = (int) (capacity * loadFactor);
		table = new EntryAutoStatesTrans[capacity];
	}

	SAuto.Trans get(Auto.State.Transition key) {
		if (key == null) {
			return getForNullKey();
		}
		int hash = key.hashCode();
		for (EntryAutoStatesTrans e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			Auto.State.Transition k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				return e.value;
			}
		}
		return null;
	}

	private SAuto.Trans getForNullKey() {
		for (EntryAutoStatesTrans e = table[0]; e != null; e = e.next) {
			if (e.key == null) {
				return e.value;
			}
		}
		return null;
	}


	static int indexFor(final int h, final int length) {
		return h & (length - 1);
	}

	void put(Auto.State.Transition key, SAuto.Trans value) {
		if (key == null) {
			return;
		}
		int hash = key.hashCode();
		int i = indexFor(hash, table.length);
		for (EntryAutoStatesTrans e = table[i]; e != null; e = e.next) {
			Auto.State.Transition k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				e.value = value;
			}
		}
		addEntry(hash, key, value, i);
	}

	private void addEntry(int hash, Auto.State.Transition key,
			SAuto.Trans value, int bucketIndex) {
		EntryAutoStatesTrans e = table[bucketIndex];
		table[bucketIndex] = new EntryAutoStatesTrans(hash, key, value, e);
		if (size++ >= threshold) {
			resize(2 * table.length);
		}
	}

	private void resize(int newCapacity) {
		EntryAutoStatesTrans[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAX) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		EntryAutoStatesTrans[] newTable = new EntryAutoStatesTrans[newCapacity];
		transfer(newTable, oldCapacity, newCapacity);
		table = newTable;
		threshold = (int) (newCapacity * loadFactor);
	}

	private void transfer(final EntryAutoStatesTrans[] newTable,
			int oldCapasity, int newCapacity) {
		EntryAutoStatesTrans[] src = table;
		for (int j = 0; j < oldCapasity; j++) {
			EntryAutoStatesTrans e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					EntryAutoStatesTrans next = e.next;
					int i = indexFor(e.hash, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	public void remove(Auto.State.Transition key) {
		removeEntryForKey(key);
	}

	final void removeEntryForKey(Auto.State.Transition key) {
		int hash = (key == null) ? 0 : key.hashCode();
		int i = indexFor(hash, table.length);
		EntryAutoStatesTrans prev = table[i];
		EntryAutoStatesTrans e = prev;

		while (e != null) {
			EntryAutoStatesTrans next = e.next;
			Auto.State.Transition k;
			if (e.hash == hash
					&& ((k = e.key) == key || (key != null && key.equals(k)))) {
				size--;
				if (prev == e) {
					table[i] = next;
				} else {
					prev.next = next;
				}
				return;
			}
			prev = e;
			e = next;
		}
	}

	static class EntryAutoStatesTrans {

		transient Auto.State.Transition key;
		transient SAuto.Trans value;
		transient EntryAutoStatesTrans next;
		final int hash;

		EntryAutoStatesTrans(final int h, final Auto.State.Transition k,
				final SAuto.Trans v, EntryAutoStatesTrans n) {
			value = v;
			next = n;
			key = k;
			hash = h;
		}

		public final Auto.State.Transition getKey() {
			return key;
		}

		public final SAuto.Trans getValue() {
			return value;
		}

		public final void setValue(SAuto.Trans newValue) {
			value = newValue;
		}

		@Override
		public final boolean equals(Object o) {
			if (!(o instanceof EntryAutoStatesTrans)) {
				return false;
			}
			EntryAutoStatesTrans e = (EntryAutoStatesTrans) o;
			Auto.State.Transition k1 = getKey();
			Auto.State.Transition k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				SAuto.Trans v1 = getValue();
				SAuto.Trans v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return (key == null ? 0 : key.hashCode())
					^ (value == null ? 0 : value.hashCode());
		}

		@Override
		public final String toString() {
			return getKey() + "=" + getValue();
		}
	}
}
