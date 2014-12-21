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

import java.util.*;
import java.lang.ref.SoftReference;

/**
 * @author Muhammad Edwin
 * @author Natalino Nugeraha
 * @version 1.0.0
 */
final class Pattern implements Cloneable {

	static final HashMap AUTOMATON_MAP = new HashMap(16);

	static PatInt get(final String regEx) {
		synchronized (AUTOMATON_MAP) {
			final SoftReference reference = (SoftReference) AUTOMATON_MAP
					.get(regEx);
			if (reference != null) {
				PatInt automaton = (PatInt) reference.get();
				if (automaton != null) {
					return automaton;
				}
			}

			final PatInt automaton = new PatInt(regEx);

			final Auto.LinkedState states = automaton.getStates();
			for (Auto.StateWrap w = states.elements; w != null; w = w.next) {
				for (Auto.State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					trans.properties = null;
				}
				for (Auto.State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					trans.properties = null;
				}
			}

			automaton.minimize();

			AUTOMATON_MAP.put(regEx, new SoftReference(automaton));
			return automaton;
		}
	}

	PatInt auto;

	Pattern(final PatInt automaton) {
		this.auto = automaton;
	}

	Pattern(final CharSet fullSet) {
		this.auto = new PatInt(fullSet);
	}

	Pattern(final String regEx) {
		this(Pattern.get(regEx));
	}

	final boolean contains(final String s) {
		return this.contains(s, 0, s.length());
	}

	final boolean contains(final String s, final int offset) {
		return this.contains(s, offset, s.length() - offset);
	}

	final String replace(final String s, String rep, int offset, int length) {
		Auto.State state = this.auto.getStartState();
		StringBuilder sb = new StringBuilder(s.length());
		if (state == null) {
			return s;
		}
		loop: for (; length > 0; ++offset, --length) {
			char c = s.charAt(offset);
			for (Auto.State.Transition trans = state.transitions; trans != null; trans = trans.next) {
				if (trans.charSet.contains(c)) {
					state = trans.toState;
					continue loop;
				} else if (state.isFinal) {
					sb.append(rep);
					state = this.auto.getStartState();
				}
			}

			if (state.isFinal) {
				sb.append(rep);
			} else {
				sb.append(c);
				continue loop;
			}
			// state=this.automaton.getStartState();
		}
		if (state.isFinal) {
			sb.append(rep);
		}
		return sb.toString();
	}

	boolean contains(final String s, int offset, int length) {
		Auto.State state = this.auto.getStartState();

		if (state == null) {
			return false;
		}

		loop: for (; length > 0; ++offset, --length) {
			char c = s.charAt(offset);
			for (Auto.State.Transition trans = state.transitions; trans != null; trans = trans.next) {
				if (trans.charSet.contains(c)) {
					state = trans.toState;
					continue loop;
				}
			}
			return false;
		}
		return state.isFinal;
	}

	public boolean contains(final char[] chars) {
		return this.contains(chars, 0, chars.length);
	}

	public boolean contains(final char[] chars, int offset) {
		return this.contains(chars, offset, chars.length - offset);
	}

	public boolean contains(final char[] chars, int offset, int length) {
		Auto.State state = this.auto.getStartState();
		if (state == null) {
			return false;
		}

		loop: for (; length > 0; ++offset, --length) {
			for (Auto.State.Transition trans = state.transitions; trans != null; trans = trans.next) {
				if (trans.charSet.contains(chars[offset])) {
					state = trans.toState;
					continue loop;
				}
			}
			return false;
		}

		return ((SetString.SState) state).isFinal;
	}

	final String getRegEx() {
		return this.auto.regEx;
	}

	@Override
	public final String toString() {
		return this.getRegEx();
	}

	@Override
	public final int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public final Object clone() {
		try {
			Pattern clone = (Pattern) super.clone();
			clone.auto = (PatInt) clone.auto.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new Error("should never happen");
		}
	}
}
