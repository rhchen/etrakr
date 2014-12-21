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

import java.util.ArrayList;
import java.text.ParsePosition;

/**
 * 
 * @author Natalino Nugeraha
 * @version 1.0.0
 */

final class Scan implements Cloneable {

	static final int UNLIMITED_MAX_LENGTH = Integer.MAX_VALUE;

	private final transient PatInt.TerminalFormat[] terminalFormats;
	private final transient int[] terminalsMaxLength;
	private final transient boolean terminalFormatsAreExclusive;

	/**
	 * 
	 * @param terminalFormats
	 */
	Scan(final PatInt.TerminalFormat[] terminalFormats) {
		this(terminalFormats, false);
	}

	/**
	 * 
	 * @param terminalFormats
	 * @param terminalFormatsAreExclusive
	 */
	Scan(final PatInt.TerminalFormat[] terminalFormats,
			final boolean terminalFormatsAreExclusive) {
		this.terminalFormats = terminalFormats;
		this.terminalFormatsAreExclusive = terminalFormatsAreExclusive;

		final int n = this.terminalFormats.length;
		if (!this.terminalFormatsAreExclusive) {
			for (int i = (n - 1) >> 1; i >= 0; --i) {
				PatInt.TerminalFormat temp = this.terminalFormats[i];
				this.terminalFormats[i] = this.terminalFormats[n - i];
				this.terminalFormats[n - i] = temp;
			}
		}
		this.terminalsMaxLength = new int[n];
		for (int i = 0; i < n; i++) {
			this.terminalsMaxLength[i] = this.terminalFormats[i].maxLength();
		}
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	final ArrayList scan(final String source) {
		return this.scan(source, 0);
	}

	/**
	 * 
	 * @param source
	 * @param startIndex
	 * @return
	 */
	final ArrayList scan(final String source, final int startIndex) {
		if (source == null) {
			String message = "null source specified";
			throw new IllegalArgumentException(message);
		}

		final char[] input = source.toCharArray();

		int firstIndexOfTerminalFormats = -1;
		int lastIndexOfTerminalFormats = -1;

		for (int i = this.terminalFormats.length - 1; i >= 0; i--) {
			if (this.terminalFormats[i] != null) {
				lastIndexOfTerminalFormats = i;
				break;
			}
		}

		if (lastIndexOfTerminalFormats == -1) {
			String message = "no terminal  added";
			throw new NullPointerException(message);
		}

		for (int i = 0; i <= lastIndexOfTerminalFormats; i++) {
			if (this.terminalFormats[i] != null) {
				firstIndexOfTerminalFormats = i;
				break;
			}
		}
		final ArrayList tokenList = new ArrayList(13);
		final int inputLength = input.length;
		final ParsePosition pos = new ParsePosition(startIndex);
		int index = startIndex;
		while (index < inputLength) {
			int longestMatch = -1;
			Object lastToken = null, token;
			for (int i = lastIndexOfTerminalFormats; i >= firstIndexOfTerminalFormats; i--) {
				if (this.terminalsMaxLength[i] >= longestMatch) {
					pos.setIndex(index);
					token = this.terminalFormats[i].parseObject(input, pos);
					final int matchLength = pos.getIndex() - index;
					if (token != null) {
						if (this.terminalFormatsAreExclusive) {
							longestMatch = matchLength;
							lastToken = token;
							break;
						} else {
							if (matchLength >= longestMatch) {
								longestMatch = matchLength;
								lastToken = token;
							}
						}
					}
				}
			}
			if (lastToken != null)
				tokenList.add(lastToken);
			else {
				String message = "can not scan input:"
						+ "\n"
						+ new String(input, startIndex, input.length
								- startIndex) + "\nerrorPosition: " + index
						+ "\n" + new String(input, index, input.length - index);
				throw new RegexException(message);
			}
			index += longestMatch;
		}

		return tokenList;
	}

	@Override
	public final Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public final int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public final String toString() {
		StringBuilder answer = new StringBuilder(64);
		answer.append("Scanner(");
		if (this.terminalFormatsAreExclusive)
			answer.append("exclusive");
		answer.append(")");
		for (int i = 0; i < this.terminalFormats.length; i++)
			if (this.terminalFormats[i] != null)
				answer.append('\n').append(this.terminalFormats[i]);
		return answer.toString();
	}

}