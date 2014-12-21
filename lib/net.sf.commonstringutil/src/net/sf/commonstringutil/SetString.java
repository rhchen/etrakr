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
 * @author Muhammad Edwin
 * @version 1.0.0
 * 
 */

class SetString extends Auto implements Cloneable {

	static final CharSet FULLSET = new SetChar();

	static {
		SetString.FULLSET.complement();
	}

	interface ISStateChangedListener extends StateChangeListener {

		/**
		 * 
		 * @param state
		 * @param isFinal
		 */
		public void isFinalChanged(SState state, boolean isFinal);
	}

	interface ISState extends Auto.IState {

		/**
		 * 
		 * @return final
		 */
		boolean isFinal();
	}

	final class SState extends Auto.State implements ISState {

		SState(final boolean isFinal) {
			this.isFinal = isFinal;
		}

		@Override
		final Auto parent() {
			return SetString.this;
		}

		/**
		 * {@link Auto.State#isFinal}
		 */
		public final boolean isFinal() {
			return this.isFinal;
		}

		/**
		 * 
		 * @param isFinal
		 */
		final void setFinal(final boolean isFinal) {
			if (this.isFinal == isFinal) {
				return;
			}
			this.isFinal = isFinal;
			if (this.changeListeners != null) {
				final Iterator it = this.changeListeners.iterator();
				for (int i = this.changeListeners.size(); i > 0; --i) {
					((ISStateChangedListener) it.next()).isFinalChanged(this,
							isFinal);
				}
			}
		}

		@Override
		public final String toString() {
			if (this.isFinal) {
				return SetString.this.automatonNr + ".["
						+ String.valueOf(this.stateNr) + ']';
			} else {
				return super.toString();
			}
		}
	}

	class LinkedSetSState extends Auto.LinkedState implements ISState {

		LinkedSetSState() {
			super();
		}

		LinkedSetSState(final SState state) {
			super(state);
		}

		public final boolean isFinal() {
			for (StateWrap w = this.elements; w != null; w = w.next) {
				if (((SState) w.state).isFinal) {
					return true;
				}
			}
			return false;
		}

		@Override
		public final String toString() {
			final StringBuilder result = new StringBuilder(32);
			result.append(this.isFinal() ? '[' : '(');
			for (StateWrap wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
				if (wrapper != this.elements) {
					result.append(", ");
				}
				result.append(wrapper.state.toString());
			}
			result.append(this.isFinal() ? ']' : ')');
			return result.toString();
		}
	}

	transient final CharSet fullSet;

	SetString(final CharSet fullSet) {
		super();
		this.fullSet = fullSet;
	}

	@Override
	Auto.State getStartState() {
		return SetString.this.startState;
	}

	SetString() {
		super();
		this.fullSet = SetString.FULLSET;
	}

	@Override
	LinkedState newLinkedState() {
		return new LinkedSetSState();
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	LinkedState newLinkedState(final State state) {
		return new LinkedSetSState((SState) state);
	}

	/**
	 * 
	 * @return state(false)
	 */
	@Override
	State createState() {
		return new SState(false);
	}

	/**
	 * 
	 * @param isFinal
	 * @return new SState
	 */
	SState createState(final boolean isFinal) {
		return new SState(isFinal);
	}

	/**
	 * 
	 * @param isFinal
	 * @return
	 */
	SState addState(final boolean isFinal) {
		final SState result = this.createState(isFinal);
		this.addState(result);
		return result;
	}

	/**
	 * 
	 * @param isFinal
	 * @param stateNr
	 * @return
	 */
	SState addState(final boolean isFinal, final int stateNr) {
		this.currentStateNr = stateNr;
		return this.addState(isFinal);
	}

	/**
	 * 
	 * @param isDeterministic
	 */
	final void setDeterministic(Boolean isDeterministic) {
		super.setDeterminstic(isDeterministic);
	}

	final LinkedState getStates() {
		return this.aStates;
	}

	final State complement(State state) {
		if (state == null) {
			SState totalFinalState = this.addState(true);
			totalFinalState.addTransition(null, (CharSet) this.fullSet.clone(),
					totalFinalState);
			return totalFinalState;
		}

		if (this.isDeterministic(state) == false) {
			// remove all properties
			LinkedState states = new LinkedState(state);
			for (StateWrap w = states.elements; w != null; w = w.next) {
				for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					states.add(trans.toState);
					trans.properties = null;
				}
				for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					states.add(trans.toState);
					trans.properties = null;
				}
			}

			state = this.makeDeterministic(state);
		}

		SState totalFinalState = null;

		LinkedState reachableStates = new LinkedState(state);
		for (StateWrap w = reachableStates.elements; w != null; w = w.next) {
			CharSet charSet = (CharSet) this.fullSet.clone();
			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				reachableStates.add(trans.toState);
				charSet.removeAll(trans.charSet);
			}

			SState sstate = (SState) w.state;
			if (charSet.isEmpty() == false) {
				if (totalFinalState == null) {
					totalFinalState = this.addState(true);
					totalFinalState.addTransition(null,
							(CharSet) this.fullSet.clone(), totalFinalState);
				}

				sstate.addTransition(null, charSet, totalFinalState);
			}
			sstate.setFinal(!sstate.isFinal);
		}

		return state;
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	final SState optional(final SState state) {
		if (state.isFinal) {
			return state;
		}
		final SState newState = this.addState(true);
		newState.addTransition(null, null, state);
		return newState;
	}

	/**
	 * 
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	final SState concat(final SState state_A, final SState state_B) {
		final LinkedState states = new LinkedState(state_A);
		for (StateWrap w = states.elements; w != null; w = w.next) {
			for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
				states.add(trans.toState);
			}
			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				states.add(trans.toState);
			}

			SState sState = (SState) w.state;
			if (sState.isFinal) {
				sState.setFinal(false);
				sState.addTransition(null, null, state_B);
			}
		}
		return state_A;
	}

	/**
	 * 
	 * @param element
	 * @param minTimes
	 * @param maxTimes
	 * @return
	 */
	SState repeat(SState element, int minTimes, final int maxTimes) {
		SState locStartState = element;

		if (minTimes == 0) {
			locStartState = this.optional(element);
			minTimes = 1;
		} else {
			for (int i = minTimes - 1; i > 0; --i) {
				SState newState = (SState) element.clone();
				locStartState = (SState) this.concat(newState, locStartState);
			}
		}

		if (maxTimes == 0) {
			final LinkedState states = new LinkedState(element);

			for (StateWrap w = states.elements; w != null; w = w.next) {
				for (Auto.State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					states.add(trans.toState);
				}

				for (Auto.State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					states.add(trans.toState);
				}

				if (((SState) w.state).isFinal) {
					((SState) w.state).addTransition(null, null, element);
				}
			}
		} else {
			for (int i = maxTimes - minTimes; i > 0; --i) {
				SState newState = (SState) element.clone();

				LinkedState states = element.reachableState();
				states.add(element);

				for (StateWrap w = states.elements; w != null; w = w.next) {
					if (((SState) w.state).isFinal) {
						((SState) w.state).addTransition(null, null, newState);
					}
				}

				element = newState;
			}
		}

		return locStartState;
	}

	/**
	 * 
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	final SState union(final State state_A, final State state_B) {
		final SState newState = this.addState(false);
		newState.addTransition(null, null, state_A);
		newState.addTransition(null, null, state_B);
		return newState;
	}

	/**
	 * 
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	final State intersect(final State state_A, final State state_B) {
		// A & B = !(!A + !B)
		return this.complement(this.union(this.complement(state_A),
				this.complement(state_B)));
	}

	/**
	 * 
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	final State minus(final State state_A, final State state_B) {
		// A \ B = A & !B = !(!A + !!B) = !(!A + B)
		return this.complement(this.union(this.complement(state_A), state_B));
	}

	/**
	 * 
	 * @param state
	 */
	final void addAll(final SState state) {
		if (this.startState == null) {
			this.setStartState(state);
		} else {
			this.setStartState(this.union((SState) this.startState, state));
		}
	}

	/**
	 * 
	 * @param state
	 */
	final void retainAll(final SState state) {
		if (this.startState == null) {
			return;
		}
		this.setStartState(this.intersect((SState) this.startState, state));
	}

	/**
	 * 
	 * @param state
	 */
	final void removeAll(final SState state) {
		if (this.startState == null) {
			return;
		}
		this.setStartState(this.minus((SState) this.startState, state));
	}

	/**
	 * 
	 * @param state
	 */
	final void concatAll(final SState state) {
		if (this.startState == null) {
			return;
		}
		this.setStartState(this.concat((SState) this.startState, state));
	}

	final void removeUselessStates() {
		// if (5==6) {
		// this.removeUnreachableStates();
		// return;
		// }
		final LinkedState usefullStates = new LinkedState();
		if (this.startState != null) {
			final LinkedState uselessStates = this.startState.reachableState();
			uselessStates.add(this.startState);
			for (StateWrap w = uselessStates.elements; w != null; w = w.next) {
				if (((SState) w.state).isFinal) {
					if (uselessStates.remove(w.state) == false) {
						throw new Error();
					}
					/*
					 * if (prev==null) uselessStates.elements = w.next; else
					 * prev.next = w.next;
					 */
					if (usefullStates.add(w.state) == false) {
						throw new Error();
					}
				}
			}
			// System.out.println(uselessStates);
			for (boolean flag = true; flag;) {
				flag = false;
				loop: for (StateWrap w = uselessStates.elements; w != null; w = w.next) {
					for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
						if (usefullStates.contains(trans.toState)) {
							if (uselessStates.remove(w.state) == false) {
								throw new Error();
							}
							if (usefullStates.add(w.state) == false) {
								throw new Error();
							}
							flag = true;
							continue loop;
						}
					}
					for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
						if (trans.charSet.isEmpty() == false
								&& usefullStates.contains(trans.toState)) {
							if (uselessStates.remove(w.state) == false) {
								throw new Error();
							}
							if (usefullStates.add(w.state) == false) {
								throw new Error();
							}
							flag = true;
							continue loop;
						}
					}
				}
			}
		}

		for (StateWrap w = this.aStates.elements; w != null; w = w.next) {
			if (usefullStates.contains(w.state) == false) {
				if (this.removeState(w.state) == false) {
					throw new Error();
				}
				// System.out.println("####"+w.state.stateNr+"####");
			}
		}
	}

	void complement() {
		this.setStartState(this.complement((SState) this.startState));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param automaton
	 */
	void addAll(final SetString automaton) {
		if (automaton.startState == null) {
			return;
		}
		Map map = this.cloneState(automaton.startState);
		this.addAll((SState) map.get(automaton.startState));
	}

	/**
	 * 
	 * @param automaton
	 */
	void retainAll(final SetString automaton) {
		if (automaton.startState == null) {
			return;
		}

		Map map = this.cloneState(automaton.startState);
		this.retainAll((SState) map.get(automaton.startState));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param automaton
	 */
	void removeAll(final SetString automaton) {
		if (automaton.startState == null) {
			return;
		}
		final Map map = this.cloneState(automaton.startState);
		this.removeAll((SState) map.get(automaton.startState));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param automaton
	 */
	final void concatAll(final SetString automaton) {
		if (automaton.startState == null) {
			return;
		}
		Map map = this.cloneState(automaton.startState);
		this.concatAll((SState) map.get(automaton.startState));
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	final Map cloneState(final State state) {
		final Map map = super.cloneState(state);
		final Set en = map.entrySet();
		final Iterator it = en.iterator();
		for (int i = en.size(); i > 0; --i) {
			Map.Entry entry = (Map.Entry) it.next();
			SState oldState = (SState) entry.getKey();
			SState newState = (SState) entry.getValue();
			newState.setFinal(oldState.isFinal);
		}
		return map;
	}

	/**
	 * 
	 * @param states
	 * @return
	 */
	@Override
	final Map cloneStates(final LinkedState states) {
		final Map map = super.cloneStates(states);
		final Set en = map.entrySet();
		final Iterator it = en.iterator();
		for (int i = en.size(); i > 0; --i) {
			Map.Entry entry = (Map.Entry) it.next();
			SState oldState = (SState) entry.getKey();
			SState newState = (SState) entry.getValue();
			newState.setFinal(oldState.isFinal);
		}
		return map;
	}

	private static final class EClosure implements Cloneable {

		private static final class Transition {

			private final CharSet charSet;
			private final EClosure toEClosure;
			private Prop properties = null;
			private Transition next = null;

			/**
			 * 
			 * @param properties
			 * @param charSet
			 * @param toEClosure
			 */
			private Transition(final Prop properties, CharSet charSet,
					EClosure toEClosure) {
				this.properties = properties;
				this.charSet = charSet;
				this.toEClosure = toEClosure;
			}
		}

		private final transient LinkedSetSState states;
		private transient Transition transitions = null;
		private transient SState state = null;

		/**
		 * 
		 * @param eStates
		 */
		private EClosure(final LinkedSetSState eStates) {
			this.states = eStates;
		}

		/**
		 * 
		 * @param properties
		 * @param charSet
		 * @param toEClosure
		 * @return
		 */
		private EClosure.Transition addTransition(final Prop properties,
				final CharSet charSet, final EClosure toEClosure) {
			EClosure.Transition newTrans = new EClosure.Transition(properties,
					charSet, toEClosure);
			newTrans.next = this.transitions;
			this.transitions = newTrans;
			return newTrans;
		}

		/**
		 * 
		 * @param transition
		 * @return
		 */
		private boolean removeTransition(final EClosure.Transition transition) {
			for (Transition prevTrans = null, trans = this.transitions; trans != null; prevTrans = trans, trans = trans.next) {
				if (trans == transition) {
					if (prevTrans == null) {
						this.transitions = trans.next;
					} else {
						prevTrans.next = trans.next;
					}

					return true;
				}
			}
			return false;
		}

		@Override
		public final boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			return this.states.equals(((EClosure) obj).states);
		}

		@Override
		public final int hashCode() {
			return this.states.hashCode();
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

	private static final class EClosureSet {

		final transient EClosure eClosure;
		transient EClosureSet next = null;

		/**
		 * 
		 * @param eClosure
		 */
		EClosureSet(final EClosure eClosure) {
			this.eClosure = eClosure;
		}

		/**
		 * 
		 * @param eClosure
		 * @return
		 */
		final boolean add(final EClosure eClosure) {
			EClosureSet prev = null;

			for (EClosureSet eCS = this; eCS != null; prev = eCS, eCS = eCS.next) {
				if (eCS.eClosure == eClosure) {
					return false;
				}
			}

			prev.next = new EClosureSet(eClosure);
			return true;
		}
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	private State makeDeterministic(final State state) {
		if (SetString.this.isDeterministic(state)) {
			return state;
		}

		final HashMap eStates2EClosure = new HashMap(14);

		IState istate = state.getEClosure();
		LinkedSetSState startEStates = (istate instanceof SState) ? (LinkedSetSState) this
				.newLinkedState((SState) istate) : (LinkedSetSState) istate;

		final EClosure startEClosure = new EClosure(startEStates);
		eStates2EClosure.put(startEStates, startEClosure);

		final EClosureSet eClosureSet = new EClosureSet(startEClosure);
		for (EClosureSet eCS = eClosureSet; eCS != null; eCS = eCS.next) {
			final EClosure eClosure = eCS.eClosure;

			for (StateWrap w = eClosure.states.elements; w != null; w = w.next) {
				loop: for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					CharSet trans_charSet = trans.charSet;
					istate = ((SState) trans.toState).getEClosure();
					LinkedSetSState trans_toEStates = (istate instanceof SState) ? (LinkedSetSState) this
							.newLinkedState(trans.toState)
							: (LinkedSetSState) istate;

					inner: for (EClosure.Transition newTrans = eClosure.transitions; newTrans != null; newTrans = newTrans.next) {
						CharSet intersection = (CharSet) newTrans.charSet
								.clone();
						intersection.retainAll(trans_charSet);

						if (intersection.isEmpty() == false) {

							if (trans_toEStates
									.equals(newTrans.toEClosure.states) == false) {
								if (newTrans.charSet.size() == intersection
										.size()) {
									eClosure.removeTransition(newTrans);
								} else {
									newTrans.charSet.removeAll(intersection);
								}

								LinkedSetSState tmpEStates = ((LinkedSetSState) trans_toEStates
										.clone());
								tmpEStates.addAll(newTrans.toEClosure.states);

								EClosure newToEClosure = (EClosure) eStates2EClosure
										.get(tmpEStates);
								if (newToEClosure == null) {
									newToEClosure = new EClosure(tmpEStates);
									eStates2EClosure.put(tmpEStates,
											newToEClosure);
								}

								if (trans.properties == null) {
									eClosure.addTransition(null, intersection,
											newToEClosure);
								} else {
									eClosure.addTransition(
											(Prop) trans.properties.clone(),
											intersection, newToEClosure);
								}
							}

							if (trans_charSet.size() == intersection.size()) {
								continue loop;
							}

							if (trans_charSet == trans.charSet) {
								trans_charSet = (CharSet) trans.charSet.clone();
							}
							trans_charSet.removeAll(intersection);
						}
					}

					if (trans_charSet.isEmpty() == false) {
						EClosure toEClosure = (EClosure) eStates2EClosure
								.get(trans_toEStates);
						if (toEClosure != null) {
							for (EClosure.Transition newTrans = eClosure.transitions; newTrans != null; newTrans = newTrans.next) {
								if (newTrans.toEClosure == toEClosure) {
									if (newTrans.properties == null
											&& trans.properties == null
											|| newTrans.properties != null
											&& newTrans.properties
													.equals(trans.properties)) {
										newTrans.charSet.addAll(trans.charSet);
										continue loop;
									}
								}
							}
						} else {
							toEClosure = new EClosure(trans_toEStates);
							eStates2EClosure.put(trans_toEStates, toEClosure);
						}

						if (trans_charSet == trans.charSet) {
							trans_charSet = (CharSet) trans.charSet.clone();
						}
						if (trans.properties == null) {
							eClosure.addTransition(null, trans_charSet,
									toEClosure);
						} else {
							eClosure.addTransition(
									(Prop) trans.properties.clone(),
									trans_charSet, toEClosure);
						}
					}
				}
			}

			if (eClosure.state == null) {
				eClosure.state = this.addState(eClosure.states.isFinal());
			}
			for (EClosure.Transition trans = eClosure.transitions; trans != null; trans = trans.next) {
				if (trans.toEClosure.state == null) {
					trans.toEClosure.state = this
							.addState(trans.toEClosure.states.isFinal());
				}

				eClosure.state.addTransition(trans.properties, trans.charSet,
						trans.toEClosure.state);

				eClosureSet.add(trans.toEClosure);
			}
		}
		this.isDeterministic = Auto.UNKNOWN;
		return startEClosure.state;
	}

	final void minimize() {
		if (this.startState == null) {
			return;
		}
		State state = this.startState;

		int states = this.aStates.size();
		this.setStartState(this.minimize(state));

		this.removeUnreachStates();
		if (this.aStates.size() > states) {
			throw new Error("more states(" + this.aStates.size()
					+ ") after minimzing than before (" + states + ")");
		}
	}

	static final class Tpl implements Cloneable {

		private final transient SState a;
		private final transient SState b;
		private final transient int hashCode;
		private transient Tpl next = null;

		/**
		 * 
		 * @param a
		 * @param b
		 */
		private Tpl(final SState a, final SState b) {
			if (a == b) {
				throw new Error("a==b");
			}
			this.a = a;
			this.b = b;
			this.hashCode = (int) ((((long) a.hashCode()) + ((long) b
					.hashCode())) % 4294967291L);
		}

		@Override
		public final boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			final Tpl tupel = (Tpl) obj;
			if (this.a != tupel.a && this.a != tupel.b) {
				return false;
			}
			if (this.b != tupel.a && this.b != tupel.b) {
				return false;
			}
			return true;
		}

		@Override
		public final Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public final String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		public final int hashCode() {
			return this.hashCode;
		}
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	private State minimize(final State state) {

		final State newStartState = this.makeDeterministic(state);
		if (this.aStates.contains(newStartState) == false) {
			throw new Error("this.states.contains(newStartState)==false");
		}

		final LinkedState states = newStartState.reachableState();
		states.add(newStartState);

		final SState totalState = this.addState(false);
		states.add(totalState);

		final HashSet tupelList_ne = new HashSet(15);
		Tpl tupelList = null;
		for (StateWrap w1 = states.elements; w1 != null; w1 = w1.next) {
			CharSet rest = (CharSet) this.fullSet.clone();
			for (State.Transition trans = w1.state.transitions; trans != null; trans = trans.next) {
				rest.removeAll(trans.charSet);
			}
			if (rest.isEmpty() == false) {
				w1.state.addTransition(null, rest, totalState);
			}

			for (StateWrap w2 = w1.next; w2 != null; w2 = w2.next) {
				Tpl tupel = new Tpl((SState) w1.state, (SState) w2.state);
				if (tupel.a.isFinal ^ tupel.b.isFinal) {
					tupelList_ne.add(tupel);
				} else {
					tupel.next = tupelList;
					tupelList = tupel;
				}
			}
		}

		boolean flag = true;
		while (flag) {
			flag = false;
			loop: for (Tpl tupel = tupelList, prev = null; tupel != null; tupel = tupel.next) {
				for (State.Transition trans_a = tupel.a.transitions; trans_a != null; trans_a = trans_a.next) {
					for (State.Transition trans_b = tupel.b.transitions; trans_b != null; trans_b = trans_b.next) {
						if (trans_a.toState != trans_b.toState) {
							Tpl newTupel = new Tpl((SState) trans_a.toState,
									(SState) trans_b.toState);
							if (tupelList_ne.contains(newTupel)) {
								CharSet intersection = (CharSet) trans_a.charSet
										.clone();
								intersection.retainAll(trans_b.charSet);
								if (intersection.isEmpty() == false) {
									if (prev == null) {
										tupelList = tupel.next;
									} else {
										prev.next = tupel.next;
									}

									tupelList_ne.add(tupel);

									flag = true;
									continue loop;
								}
							}
						}
					}
				}
				prev = tupel;
			}
		}

		final HashMap map = new HashMap(16);
		for (Tpl tupel = tupelList; tupel != null; tupel = tupel.next) {
			SState eqState = (SState) map.get(tupel.a);
			if (eqState != null) {
				map.put(tupel.b, eqState);
			} else {
				eqState = (SState) map.get(tupel.b);
				if (eqState != null) {
					map.put(tupel.a, eqState);
				} else if (tupel.b != totalState) {
					map.put(tupel.a, tupel.b);
				} else {
					map.put(tupel.b, tupel.a);
				}
			}
		}
		this.removeState(totalState);

		for (StateWrap w = states.elements; w != null; w = w.next) {
			SState newState = (SState) map.get(w.state);
			if (newState == null) {
				for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					SState newToState = (SState) map.get(trans.toState);
					if (newToState != null) {
						((SState) w.state).removeTransition(trans);

						for (State.Transition tmp = w.state.transitions; tmp != null; tmp = tmp.next) {
							if (tmp.toState == newToState) {
								if (tmp.properties == null
										&& trans.properties == null
										|| tmp.properties != null
										&& tmp.properties
												.equals(trans.properties)) {
									((SState) w.state).removeTransition(tmp);
									trans.charSet.addAll(tmp.charSet);
									break;
								}
							}
						}

						((SState) w.state).addTransition(trans.properties,
								trans.charSet, newToState);
					}
				}
			} else {
				loop: for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					SState newToState = (SState) map.get(trans.toState);
					if (newToState == null) {
						newToState = (SState) trans.toState;
					}
					((SState) w.state).removeTransition(trans);

					for (State.Transition tmp = newState.transitions; tmp != null; tmp = tmp.next) {
						if (tmp.toState == newToState) {
							if (tmp.properties == null
									&& trans.properties == null
									|| tmp.properties != null
									&& tmp.properties.equals(trans.properties)) {
								continue loop;
							}
						}
					}
					newState.addTransition(trans.properties, trans.charSet,
							newToState);
				}
			}
		}

		final Iterator it = map.keySet().iterator();
		for (int i = map.size(); i > 0; --i) {
			this.removeState((SState) it.next());
		}

		final SState newNewStartState = (SState) map.get(newStartState);
		if (newNewStartState != null) {
			return newNewStartState;
		}
		return newStartState;
	}
}
