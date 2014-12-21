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

final class FBS {

	private final transient long[] bits;
	private final transient int sz;
	private final transient long numBits;

	/**
	 * 
	 * @param numBits
	 */
	FBS(final long numBits) {
		this.numBits = numBits;
		this.bits = new long[bits2words(numBits)];
		this.sz = bits.length;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	final boolean get(int index) {
		assert index >= 0 && index < numBits;
		int i = index >> 6;
		int bit = index & 0x3f;
		long bitmask = 1L << bit;
		return (bits[i] & bitmask) != 0;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	final boolean get(long index) {
		assert index >= 0 && index < numBits;
		int i = (int) (index >> 6);
		int bit = (int) index & 0x3f;
		long bitmask = 1L << bit;
		return (bits[i] & bitmask) != 0;
	}

	/**
	 * 
	 * @param index
	 */
	final void set(int index) {
		assert index >= 0 && index < numBits;
		int wordNum = index >> 6;
		int bit = index & 0x3f;
		long bitmask = 1L << bit;
		bits[wordNum] |= bitmask;
	}

	/**
	 * 
	 * @param index
	 */
	final void set(long index) {
		assert index >= 0 && index < numBits;
		int wordNum = (int) (index >> 6);
		int bit = (int) index & 0x3f;
		long bitmask = 1L << bit;
		bits[wordNum] |= bitmask;
	}


	/**
	 * 
	 * @param numBits
	 * @return
	 */
	final static int bits2words(long numBits) {
		return (int) (((numBits - 1) >>> 6) + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FBS)) {
			return false;
		}
		FBS a;
		FBS b = (FBS) o;
		if (b.sz > this.sz) {
			a = b;
			b = this;
		} else {
			a = this;
		}
		for (int i = a.sz - 1; i >= b.sz; i--) {
			if (a.bits[i] != 0) {
				return false;
			}
		}

		for (int i = b.sz - 1; i >= 0; i--) {
			if (a.bits[i] != b.bits[i]) {
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		long h = 0;
		for (int i = bits.length; --i >= 0;) {
			h ^= bits[i];
			h = (h << 1) | (h >>> 63);
		}
		return (int) ((h >> 32) ^ h) + 0x98761234;
	}

}
