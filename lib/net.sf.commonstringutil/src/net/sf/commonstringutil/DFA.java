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

/**
 * 
 * @author Natalino Nugeraha
 * @version 1.0.0
 */

final class DFA implements Cloneable {

	static final class State implements Cloneable {

		static final class Transition implements Cloneable {

			transient final SetChar charSet;
			transient final int toState;

			Transition(final SetChar charSet, final int toState) {
				this.charSet = charSet;
				this.toState = toState;
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
				// TODO Auto-generated method stub
				return super.toString();
			}

			@Override
			public final Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return super.clone();
			}
		}

		final boolean isFinal;
		final Transition[] transitions;

		/**
		 * 
		 * @param isFinal
		 * @param transitions
		 */
		State(final boolean isFinal, final Transition[] transitions) {
			this.isFinal = isFinal;
			this.transitions = transitions;
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
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		public final Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
	}

	transient final State[] states;
	transient final Integer startState;

	/**
	 * 
	 * @param states
	 * @param startState
	 */
	DFA(final State[] states, final Integer startState) {
		this.states = states;
		this.startState = startState;
	}

	/**
	 * 
	 * @param automaton
	 */
	DFA(final Data automaton) {
		final State[] newStates = new State[automaton.stts == null ? 0
				: automaton.stts.length];
		final HashMap map = new HashMap(15);
		for (int i = 0; i < newStates.length; ++i) {
			Data.Stt state = automaton.stts[i];
			if (state == null) {
				throw new IllegalArgumentException((i + 1)
						+ ". state of automaton is null");
			}

			final State.Transition[] newTransitions = new State.Transition[state.transitions == null ? 0
					: state.transitions.length];

			for (int t = 0; t < newTransitions.length; ++t) {
				Data.Stt.Tran trans = state.transitions[t];
				if (trans == null) {
					throw new IllegalArgumentException((t + 1)
							+ ". transition of state " + state.number
							+ " is null");
				}
				if (trans.charSet == null) {
					throw new IllegalArgumentException("charSet of " + (t + 1)
							+ ". transition of state " + state.number
							+ " is null");
				}

				SetChar newCharSet = (SetChar) map.get(trans.charSet);
				if (newCharSet == null) {
					newCharSet = new SetChar(trans.charSet);
					map.put(trans.charSet, newCharSet);
				}

				int toStateNr = 0;
				try {
					while (automaton.stts[toStateNr].number != trans.toSttNumber) {
						++toStateNr;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException("toState "
							+ trans.toSttNumber + " of " + (t + 1)
							+ ". transition of state " + state.number
							+ " does not exist");
				}

				newTransitions[t] = new State.Transition(newCharSet, toStateNr);
			}

			newStates[i] = new State(state.isFinal, newTransitions);
		}

		this.states = newStates;

		if (automaton.startSttNumber == null) {
			this.startState = null;
		} else {
			int automatonStartStateNr = automaton.startSttNumber.intValue();
			int startStateNr = 0;
			try {
				while (automaton.stts[startStateNr].number != automatonStartStateNr) {
					++startStateNr;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("startState "
						+ automaton.startSttNumber + " does not exist");
			}
			this.startState = new Integer(startStateNr);
		}
	}

	/**
	 * 
	 * @param chars
	 * @return
	 */
	final boolean contains(final char[] chars) {
		return this.contains(chars, 0, chars.length);
	}

	/**
	 * 
	 * @param chars
	 * @param offset
	 * @return
	 */
	final boolean contains(final char[] chars, final int offset) {
		return this.contains(chars, offset, chars.length - offset);
	}

	/**
	 * 
	 * @param chars
	 * @param offset
	 * @param length
	 * @return
	 */
	final boolean contains(final char[] chars, int offset, int length) {
		if (this.startState == null) {
			return false;
		}
		State state = this.states[this.startState.intValue()];

		loop: for (; length > 0; ++offset, --length) {
			for (int i = 0; i < state.transitions.length; ++i) {
				if (state.transitions[i].charSet.contains(chars[offset])) {
					state = this.states[state.transitions[i].toState];
					continue loop;
				}
			}
			return false;
		}

		return state.isFinal;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	final boolean contains(final String s) {
		return this.contains(s, 0, s.length());
	}

	/**
	 * 
	 * @param s
	 * @param offset
	 * @return
	 */
	final boolean contains(final String s, final int offset) {
		return this.contains(s, offset, s.length() - offset);
	}

	/**
	 * 
	 * @param s
	 * @param offset
	 * @param length
	 * @return
	 */
	final boolean contains(final String s, int offset, int length) {
		if (this.startState == null) {
			return false;
		}
		State state = this.states[this.startState.intValue()];

		loop: for (; length > 0; ++offset, --length) {
			for (int i = 0; i < state.transitions.length; ++i) {
				if (state.transitions[i].charSet.contains(s.charAt(offset))) {
					state = this.states[state.transitions[i].toState];
					continue loop;
				}
			}
			return false;
		}

		return state.isFinal;
	}

	@Override
	public final boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
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
	public final Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
