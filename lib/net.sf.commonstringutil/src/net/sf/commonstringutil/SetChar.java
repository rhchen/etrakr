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

import java.util.NoSuchElementException;

/**
 * 
 * @author Natalino Nugeraha
 * @see CharSet
 * @version 1.0.0
 */

public class SetChar implements CharSet {

	interface IAbstract {
		/**
		 * 
		 * @return size
		 */
		abstract int size();

		/**
		 * 
		 * @return size is empty
		 */
		abstract boolean isEmpty();

		abstract void complement();

		/**
		 * 
		 * @param ch
		 * @return
		 */
		abstract boolean contains(char ch);

		/**
		 * 
		 * @param ch
		 * @return
		 */
		abstract boolean add(char ch);

		/**
		 * 
		 * @param ch
		 * @return
		 */
		abstract boolean remove(char ch);

		/**
		 * 
		 * @param set
		 */
		abstract void addAll(IAbstract set);

		/**
		 * 
		 * @param set
		 */
		abstract void removeAll(IAbstract set);

		/**
		 * 
		 * @param set
		 */
		abstract void retainAll(IAbstract set);

		/**
		 * @see CharSet.Iterator
		 * @return
		 */
		abstract CharSet.Iterator iterator();

		/**
		 * 
		 * @param chars
		 * @param offset
		 * @param length
		 */
		abstract void addAll(String chars, int offset, int length);

		/**
		 * 
		 * @param chars
		 * @param offset
		 * @param length
		 */
		abstract void addAll(char[] chars, int offset, int length);

		abstract boolean equals(Object obj);
	}

	static final class Wrapper {
		final transient int offset;
		transient long value;

		/**
		 * 
		 * @param offset
		 * @param value
		 */
		Wrapper(final int offset, final long value) {
			this.offset = offset;
			this.value = value;
		}

		static final long HEXA_MAX = 15L;

		final int size() {
			int answer = 0;
			for (long tmp = this.value; tmp != 0; tmp >>>= 4) {
				switch ((int) (tmp & HEXA_MAX)) {
				case 0:
					answer += 0;
					break;
				case 1:
					answer += 1;
					break;
				case 2:
					answer += 1;
					break;
				case 3:
					answer += 2;
					break;
				case 4:
					answer += 1;
					break;
				case 5:
					answer += 2;
					break;
				case 6:
					answer += 2;
					break;
				case 7:
					answer += 3;
					break;
				case 8:
					answer += 1;
					break;
				case 9:
					answer += 2;
					break;
				case 10:
					answer += 2;
					break;
				case 11:
					answer += 3;
					break;
				case 12:
					answer += 2;
					break;
				case 13:
					answer += 3;
					break;
				case 14:
					answer += 3;
					break;
				case 15:
					answer += 4;
					break;
				default:
					throw new RuntimeException("error: should never happen");
				}
			}
			return answer;
		}
	}

	final static int[] PRIMENUMBERS = new int[] { 3, 5, 7, 11, 13, 17, 19, 23,
			29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
			101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
			167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
			239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311,
			313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389,
			397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
			467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563,
			569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641,
			643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727,
			733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821,
			823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907,
			911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997,
			1009, 1013, 1019, 1021, 1024 };

	final static long[] VALUES = new long[64];
	static {
		VALUES[0] = 1L;
		for (int t = 0, i = 1,len=VALUES.length; i < len; ++i, ++t)
			VALUES[i] = VALUES[t] << 1;
	}

	final class LongMap implements IAbstract {
		transient Wrapper[] sets = null;
		transient int size = 0;

		/**
		 * 
		 * @param set
		 */
		LongMap(final LongMap set) {
			final int length=set.sets.length;
			this.sets = new Wrapper[length];
			for (int i = 0; i < length; ++i)
				if (set.sets[i] != null)
					this.sets[i] = new Wrapper(set.sets[i].offset,
							set.sets[i].value);

			this.size = set.size;
		}

		LongMap() {
			this.sets = new Wrapper[SetChar.PRIMENUMBERS[0]];
		}

		/**
		 * 
		 * @param ch1
		 * @param ch2
		 */
		LongMap(final char ch1, final char ch2) {
			this.sets = new Wrapper[SetChar.PRIMENUMBERS[0]];
			this.add(ch1);
			this.add(ch2);
		}

		@Override
		public final boolean isEmpty() {
			return this.size() == 0;
		}

		@Override
		public final int size() {
			if (this.size >= 0)
				return this.size;
			int answer = 0;
			for (int i = 0; i < this.sets.length; ++i) {
				if (this.sets[i] != null)
					answer += this.sets[i].size();
			}
			this.size = answer;
			return answer;
		}

		@Override
		public final boolean contains(final char ch) {
			final int offset = ch / 64;
			final int index = offset % this.sets.length;
			if (this.sets[index] == null || this.sets[index].offset != offset)
				return false;
			return (this.sets[index].value & (VALUES[ch % 64])) != 0;
		}

		@Override
		public final void complement() {
			this.size = -1;
			final Wrapper[] tmp = this.sets;
			this.sets = new Wrapper[SetChar.PRIMENUMBERS[0]];
			for (int offset = 0; offset < SetChar.max; ++offset) {
				int index = offset % tmp.length;
				long value = -1L;
				if (tmp[index] != null && tmp[index].offset == offset)
					value ^= tmp[index].value;
				if (value != 0)
					this.addAll(offset, value);
			}
		}

		@Override
		public final boolean add(final char ch) {
			if (ch > SetChar.maxChar)
				throw new IllegalArgumentException("ch > maxChar = "
						+ SetChar.maxChar + "(" + (int) SetChar.maxChar + ")");

			final int offset = ch / StringUtil.LLENGTH;
			do {
				int index = offset % this.sets.length;
				if (this.sets[index] == null) {
					this.sets[index] = new Wrapper(offset,
							1L << (ch % StringUtil.LLENGTH));
					if (this.size >= 0)
						++this.size;
					return true;
				}
				if (this.sets[index].offset == offset) {
					long oldValue = this.sets[index].value;
					this.sets[index].value |= VALUES[ch % 64];
					long newValue = this.sets[index].value;

					if (oldValue == newValue)
						return false;

					if (this.size >= 0)
						++this.size;
					return true;
				}

				this.expand();
			} while (true);
		}

		@Override
		public final void addAll(final char[] chars, int offset, int length) {
			for (; length > 0; ++offset, --length)
				this.add(chars[offset]);
		}

		@Override
		public final void addAll(final String chars, int offset, int length) {
			for (; length > 0; ++offset, --length)
				this.add(chars.charAt(offset));
		}
		
		@Override
		public final void addAll(final IAbstract set) {
			this.addAll((LongMap) set);
		}

		/**
		 * 
		 * @param set
		 */
		final void addAll(final LongMap set) {
			if (this.sets.length >= set.sets.length) {
				for (int i = 0; i < set.sets.length; ++i) {
					if (set.sets[i] != null)
						this.addAll(set.sets[i].offset, set.sets[i].value);
				}
			} else {
				final Wrapper[] tmp = this.sets;
				this.sets = new Wrapper[set.sets.length];
				for (int i = 0; i < set.sets.length; ++i)
					this.sets[i] = (set.sets[i] == null) ? null : new Wrapper(
							set.sets[i].offset, set.sets[i].value);
				this.size = set.size;

				for (int i = 0; i < tmp.length; ++i) {
					if (tmp[i] != null)
						this.addAll(tmp[i]);
				}
			}
		}

		/**
		 * 
		 * @param offset
		 * @param value
		 */
		private void addAll(final int offset, final long value) {
			this.size = -1;
			do {
				int index = offset % this.sets.length;
				if (this.sets[index] == null) {
					this.sets[index] = new Wrapper(offset, value);
					return;
				}
				if (this.sets[index].offset == offset) {
					this.sets[index].value |= value;
					return;
				}

				this.expand();
			} while (true);
		}

		/**
		 * 
		 * @param w
		 */
		private void addAll(final Wrapper w) {
			this.size = -1;
			do {
				int index = w.offset % this.sets.length;
				if (this.sets[index] == null) {
					this.sets[index] = w;
					return;
				}
				if (this.sets[index].offset == w.offset) {
					this.sets[index].value |= w.value;
					return;
				}

				this.expand();
			} while (true);
		}

		@Override
		public final boolean remove(final char ch) {
			final int offset = ch / StringUtil.LLENGTH;
			final int index = offset % this.sets.length;
			if (this.sets[index] == null)
				return false;
			if (this.sets[index].offset != offset)
				return false;

			long oldValue = this.sets[index].value;
			this.sets[index].value &= (-1L) ^ (VALUES[ch % 64]);
			long newValue = this.sets[index].value;

			if (oldValue == newValue)
				return false;

			if (this.size > 0)
				--this.size;
			if (newValue == 0)
				this.sets[index] = null;
			return true;
		}

		@Override
		public final void removeAll(final IAbstract set) {
			this.removeAll((LongMap) set);

		}

		/**
		 * 
		 * @param set
		 */
		final void removeAll(final LongMap set) {
			for (int i = 0; i < set.sets.length; ++i) {
				if (set.sets[i] != null)
					this.removeAll(set.sets[i].offset, set.sets[i].value);
			}
		}

		private void removeAll(final int offset, final long value) {
			final int index = offset % this.sets.length;
			if (this.sets[index] == null)
				return;
			if (this.sets[index].offset != offset)
				return;

			this.size = -1;
			this.sets[index].value &= (-1L) ^ value;
			if (this.sets[index].value == 0)
				this.sets[index] = null;
		}

		@Override
		public final void retainAll(final IAbstract set) {
			this.retainAll((LongMap) set);
		}

		/**
		 * 
		 * @param set
		 */
		final void retainAll(final LongMap set) {
			this.size = -1;
			for (int i = 0; i < this.sets.length; ++i) {
				if (this.sets[i] != null) {
					Wrapper w1 = this.sets[i];
					Wrapper w2 = set.sets[w1.offset % set.sets.length];
					if (w2 == null)
						this.sets[i] = null;
					else {
						if (w1.offset != w2.offset)
							this.sets[i] = null;
						else {
							w1.value &= w2.value;
							if (this.sets[i].value == 0)
								this.sets[i] = null;
						}
					}
				}
			}
		}

		private void expand() {
			final Wrapper[] values = this.sets;
			init: do {
				this.sets = new Wrapper[this.nextPrimeNumber()];
				for (int i = 0; i < values.length; ++i) {
					if (values[i] != null) {
						int index = values[i].offset % this.sets.length;
						if (this.sets[index] != null)
							continue init;
						this.sets[index] = values[i];
					}
				}
				return;
			} while (true);
		}

		private int nextPrimeNumber() {
			final int currentPrimeNumber = this.sets.length;
			int i = 0;
			while (SetChar.PRIMENUMBERS[i] != currentPrimeNumber)
				++i;
			return SetChar.PRIMENUMBERS[++i];
		}

		public final CharSet.Iterator iterator() {
			return new CharSet.Iterator() {
				char currentChar = '\u0000';

				public final boolean hasNext() {
					do {
						if (LongMap.this.contains(this.currentChar))
							return true;
					} while (++this.currentChar != '\u0000');
					return false;
				}

				public final char next() {
					do {
						if (LongMap.this.contains(this.currentChar))
							return this.currentChar++;
					} while (++this.currentChar != '\u0000');

					throw new NoSuchElementException(SetChar.this.toString());
				}
			};
		}

		public final boolean equals(final Object obj) {
			if (obj == null)
				return false;
			if (this == obj)
				return true;
			if (LongMap.class != obj.getClass())
				return false;

			LongMap set = (LongMap) obj;
			if (this.size != set.size)
				return false;

			for (int i = 0; i < this.sets.length; ++i) {
				if (this.sets[i] != null) {
					if (this.sets[i].value == 0)
						throw new Error("this.sets[i].value==0");
					Wrapper w1 = this.sets[i];
					Wrapper w2 = set.sets[w1.offset % set.sets.length];
					if (w2 == null)
						return false;
					else {
						if (w1.offset != w2.offset)
							return false;
						else {
							if (w1.value != w2.value)
								return false;
						}
					}
				}
			}
			return true;
		}
	}

	static final int max = 4;
	static final char maxChar = (char) (64 * max - 1);

	SetChar.IAbstract set;

	SetChar(IAbstract set) {
		this.set = set;
	}

	SetChar() {
		this.set = new LongMap();
	}

	SetChar(final char ch) {
		this();
		this.set.add(ch);
	}

	SetChar(final String s) {
		this();
		this.set.addAll(s, 0, s.length());
	}

	public final void complement() {
		this.set.complement();
	}

	public final boolean contains(char ch) {
		return this.set.contains(ch);
	}

	public final boolean isEmpty() {
		return this.set.isEmpty();
	}

	public final int size() {
		return this.set.size();
	}

	public final CharSet.Iterator iterator() {
		return this.set.iterator();
	}

	public final void clear() {
		this.set = new LongMap();
	}

	public final boolean add(final char ch) {
		return this.set.add(ch);
	}

	public final boolean remove(final char ch) {
		return this.set.remove(ch);
	}

	public final void addAll(final String chars) {
		this.addAll(chars, 0, chars.length());
	}

	final void addAll(final String chars, final int offset) {
		this.addAll(chars, offset, chars.length() - offset);
	}

	final void addAll(final String chars, final int offset, final int length) {
		if (length == 0)
			return;
		this.set.addAll(chars, offset, length);
	}

	final void addAll(final char[] chars) {
		this.addAll(chars, 0, chars.length);
	}

	final void addAll(final char[] chars, final int offset) {
		this.addAll(chars, offset, chars.length - offset);
	}

	final void addAll(final char[] chars, final int offset, final int length) {
		if (length == 0)
			return;
		this.set.addAll(chars, offset, length);
	}

	public final void addAll(final CharSet set) {
		if (set instanceof SetChar)
			this.set.addAll(((SetChar) set).set);
		else {
			final CharSet.Iterator it = set.iterator();
			for (int i = set.size(); i > 0; --i)
				this.set.add(it.next());
		}
	}

	public final void removeAll(final CharSet set) {
		if (set instanceof SetChar)
			this.set.removeAll(((SetChar) set).set);
		else {
			final CharSet.Iterator it = set.iterator();
			for (int i = set.size(); i > 0; --i)
				this.set.remove(it.next());
		}
	}

	public final void retainAll(final CharSet set) {
		if (set instanceof SetChar)
			this.set.retainAll(((SetChar) set).set);
		else {
			final SetChar charSet = new SetChar();
			final CharSet.Iterator it = set.iterator();
			for (int i = set.size(); i > 0; --i)
				charSet.add(it.next());
			this.set.retainAll(charSet.set);
		}
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		return this.set.equals(((SetChar) obj).set);
	}

	protected final IAbstract cloneAbstract(final IAbstract set) {
		if (set instanceof LongMap)
			return new LongMap((LongMap) set);
		throw new Error(StringUtil.STR_EMP);
	}

	@Override
	public final int hashCode() {
		return this.set.size();
	}

	@Override
	public final CharSet clone() {
		try {
			SetChar clone = (SetChar) super.clone();
			clone.set = clone.cloneAbstract(set);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new Error("CloneNotSupportedException:\n" + e);
		}
	}

	@Override
	public final String toString() {
		final StringBuilder answer = new StringBuilder(48);

		int from = -1;
		char ch = '\u0000';
		do {
			if (this.contains(ch)) {
				if (from == -1)
					from = ch;
			} else {
				if (from != -1) {
					char to = ch;
					--to;
					if (from == to) {
						if (to == '[' || to == ']' || to == '\\' || to == '-')
							answer.append('\\');
						answer.append(to);
					} else {
						char char_from = (char) from;
						if (char_from == '[' || char_from == ']'
								|| char_from == '\\' || char_from == '-')
							answer.append('\\');
						answer.append((char) from);
						if (to != ++from)
							answer.append("-");
						if (to == '[' || to == ']' || to == '\\' || to == '-')
							answer.append('\\');
						answer.append(to);
					}

					from = -1;
				}
			}
		} while (++ch != '\u0000');

		if (from != -1) {
			char char_from = (char) from;
			if (char_from == '[' || char_from == ']' || char_from == '\\'
					|| char_from == '-')
				answer.append('\\');
			answer.append((char) from);
			if (from != '\ufffe')
				answer.append('-');
			answer.append('\uffff');
		}

		for (int i = answer.length() - 1; i >= 0; --i) {
			if (answer.charAt(i) > '\u00ff')
				answer.setCharAt(i, '.');
		}
		return answer.toString();
	}

}