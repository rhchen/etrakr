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
 * 
 */

abstract class Auto implements Cloneable {

	private final static short TRUE = 1;
	private final static short FALSE = 0;
	final static short UNKNOWN = -1;
	private static short currentAutomatonNr = 0;

	interface ChangedListener {
		/**
		 * 
		 * @param state
		 */
		void stateAdd(State state);

		/**
		 * 
		 * @param state
		 */
		void stateRemove(State state);

		/**
		 * 
		 * @param oldStartState
		 * @param newStartState
		 */
		void firstStateChange(State oldStartState, State newStartState);
	}

	/** list of listeners **/
	private transient final ArrayList listeners = new ArrayList(4);

	/**
	 * 
	 * @param listener
	 */
	final void addChangedListener(final Auto.ChangedListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @return
	 */
	final boolean removeChangedListener(final Auto.ChangedListener listener) {
		final Iterator it = this.listeners.iterator();
		for (int i = this.listeners.size(); i > 0; --i) {
			if (listener == it.next()) {
				if (this.listeners.size() > 1) {
					it.remove();
				}
				return true;
			}
		}
		return false;
	}

	interface StateVisitListener {
		/**
		 * 
		 * @param state
		 */
		void stateVisited(Auto.State state);

		/**
		 * 
		 * @param state
		 * @param ch
		 */
		void stateVisited(Auto.State state, char ch);

		/**
		 * 
		 * @param state
		 */
		void stateUnVisited(Auto.State state);
	}

	interface StateChangeListener {

		/**
		 * 
		 * @param transition
		 */
		void transitionAdded(Auto.State.Transition transition);

		/**
		 * 
		 * @param transition
		 */
		void transitionRemoved(Auto.State.Transition transition);
	}

	interface TransitVisitListener {

		/**
		 * 
		 * @param transition
		 */
		void transitionVisited(Auto.State.Transition transition);

		/**
		 * 
		 * @param transition
		 * @param ch
		 */
		void transitionVisited(Auto.State.Transition transition, char ch);
	}

	static final class StateWrap implements Cloneable {

		final transient State state;
		transient StateWrap next = null;

		/**
		 * 
		 * @param state
		 */
		StateWrap(final State state) {
			this.state = state;
		}

		@Override
		public final int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}

		@Override
		public final boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}

		@Override
		public final String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
	}

	interface IState extends Cloneable {

		/**
		 * 
		 * @param ch
		 * @return
		 */
		IState next(char ch);

		/**
		 * 
		 * @return
		 */
		LinkedState reachableState();

		/**
		 * 
		 * @return
		 */
		Object clone();
	}

	/**
	 * 
	 *@see IState
	 */
	class State implements IState {

		private final static short TRUE = 1;
		private final static short FALSE = 0;
		private final static short UNKNOWN = -1;
		transient boolean isFinal;
		transient ArrayList visitListeners = new ArrayList(4);
		transient ArrayList changeListeners = new ArrayList(4);

		/**
		 * 
		 * @param listener
		 */
		final void addVisitedListener(final StateVisitListener listener) {
			this.visitListeners.add(listener);
		}

		/**
		 * 
		 * @param listener
		 * @return
		 */
		final boolean removeVisitedListener(final StateVisitListener listener) {
			final Iterator it = this.visitListeners.iterator();
			for (int i = this.visitListeners.size(); i > 0; --i) {
				if (listener == it.next()) {
					if (this.visitListeners.size() > 1) {
						it.remove();
					} else {
						this.visitListeners = null;
					}

					return true;
				}
			}

			return false;
		}

		/**
		 * 
		 * @param listener
		 */
		final void addChangedListener(final StateChangeListener listener) {
			this.changeListeners.add(listener);
		}

		/**
		 * 
		 * @param listener
		 * @return
		 */
		final boolean removeChangedListener(final StateChangeListener listener) {
			final Iterator it = this.changeListeners.iterator();
			for (int i = this.changeListeners.size(); i > 0; --i) {
				if (listener == it.next()) {
					if (this.changeListeners.size() > 1) {
						it.remove();
					} else {
						this.changeListeners = null;
					}

					return true;
				}
			}
			return false;
		}

		final IState visit() {
			if (this.eTransitions == null) {
				if (this.visitListeners != null) {
					final Iterator it = this.visitListeners.iterator();
					for (int i = this.visitListeners.size(); i > 0; --i) {
						((StateVisitListener) it.next()).stateVisited(this);
					}
				}
				return this;
			}

			final LinkedState eClosure = Auto.this.newLinkedState(this);
			for (StateWrap w = eClosure.elements; w != null; w = w.next) {
				if (w.state.visitListeners != null) {
					final Iterator it = w.state.visitListeners.iterator();
					for (int i = w.state.visitListeners.size(); i > 0; --i) {
						((StateVisitListener) it.next()).stateVisited(w.state);
					}
				}

				for (Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					trans.visit(eClosure);
				}
			}

			return eClosure;
		}

		final void unVisit() {
			if (this.visitListeners != null) {
				final Iterator it = this.visitListeners.iterator();
				for (int i = this.visitListeners.size(); i > 0; --i) {
					((StateVisitListener) it.next()).stateUnVisited(this);
				}
			}
		}

		final class Transition {

			transient final ArrayList transitionvisitListeners = new ArrayList(
					8);

			/**
			 * 
			 * @param listener
			 */
			final void addVisitedListener(final TransitVisitListener listener) {
				this.transitionvisitListeners.add(listener);
			}

			/**
			 * 
			 * @param listener
			 */
			final void removeVisitedListener(final TransitVisitListener listener) {
				final Iterator it = this.transitionvisitListeners.iterator();
				for (int i = this.transitionvisitListeners.size(); i > 0; --i) {
					if (listener == it.next()) {
						if (this.transitionvisitListeners.size() > 1) {
							it.remove();
						}
						return;
					}
				}
			}

			final Auto.State visit() {
				final Iterator it = this.transitionvisitListeners.iterator();
				for (int i = this.transitionvisitListeners.size(); i > 0; --i) {
					((TransitVisitListener) it.next()).transitionVisited(this);
				}
				return this.toState;
			}

			private void visit(final LinkedState statesToVisit) {
				final Iterator it = this.transitionvisitListeners.iterator();
				for (int i = this.transitionvisitListeners.size(); i > 0; --i) {
					((TransitVisitListener) it.next()).transitionVisited(this);
				}
				statesToVisit.add(this.toState);
			}

			/**
			 * 
			 * @param ch
			 * @return
			 */
			private Auto.State visit(final char ch) {
				final Iterator it = this.transitionvisitListeners.iterator();
				for (int i = this.transitionvisitListeners.size(); i > 0; --i) {
					((TransitVisitListener) it.next()).transitionVisited(this,
							ch);
				}
				return this.toState;
			}

			/**
			 * 
			 * @param ch
			 * @param states
			 */
			private void visit(final char ch, final LinkedState states) {
				final Iterator it = this.transitionvisitListeners.iterator();
				for (int i = this.transitionvisitListeners.size(); i > 0; --i) {
					((TransitVisitListener) it.next()).transitionVisited(this,
							ch);
				}
				states.add(this.toState);
			}

			final transient CharSet charSet;
			final transient State toState;
			transient Prop properties = null;
			transient Transition next = null;

			/**
			 * 
			 * @param properties
			 * @param charSet
			 * @param toState
			 */
			Transition(final Prop properties, final CharSet charSet,
					final State toState) {
				// if (toState == null)
				// throw new IllegalArgumentException("toState==null");

				this.properties = properties;
				this.charSet = charSet;
				this.toState = toState;
			}

			/**
			 * 
			 * @return State.this
			 */
			final State getFromState() {
				return State.this;
			}

			/**
			 * 
			 * @return toState
			 */
			final State getToState() {
				return this.toState;
			}

			/**
			 * 
			 * @return charSet
			 */
			final CharSet getCharSet() {
				return this.charSet;
			}

			@Override
			public final String toString() {
				final StringBuilder buffer = new StringBuilder(32);
				buffer.append(State.this);
				if (this.charSet == null) {
					if (this.properties == null) {
						buffer.append(" --> ");
					} else {
						buffer.append(" -").append(this.properties)
								.append(": -> ");
					}
				} else {
					if (this.properties == null) {
						buffer.append(" -").append(this.charSet).append("-> ");
					} else {
						buffer.append(" -").append(this.properties).append(':')
								.append(this.charSet).append("-> ");
					}
				}

				buffer.append(this.toState);
				return buffer.toString();
			}
		}

		transient int stateNr = Auto.this.currentStateNr++;
		transient Transition transitions = null;
		transient Transition eTransitions = null;
		private transient short isDeterministic = State.TRUE;

		/** Default constructor **/
		State() {
		}

		/**
		 * 
		 * @return this
		 */
		Auto parent() {
			return Auto.this;
		}

		/**
		 * 
		 * @param properties
		 * @param charSet
		 * @param toState
		 * @return
		 */
		final Transition addTransition(final Prop properties,
				final CharSet charSet, final State toState) {
			final Transition result = new Transition(properties, charSet,
					toState);
			this.addTransition(result);
			return result;
		}

		/**
		 * 
		 * @param trans
		 */
		final void addTransition(final Transition trans) {
			if (trans.charSet == null) {
				trans.next = this.eTransitions;
				this.eTransitions = trans;
				Auto.this.isDeterministic = Auto.FALSE;
			} else {
				trans.next = this.transitions;
				this.transitions = trans;
				if (this.isDeterministic == State.TRUE) {
					this.isDeterministic = State.UNKNOWN;
				}
				if (Auto.this.isDeterministic == Auto.TRUE) {
					Auto.this.isDeterministic = Auto.UNKNOWN;
				}
			}

			if (this.changeListeners != null) {
				final Iterator it = this.changeListeners.iterator();
				for (int i = this.changeListeners.size(); i > 0; --i) {
					((StateChangeListener) it.next()).transitionAdded(trans);
				}
			}
		}

		final boolean removeTransition(final Transition transition) {
			// if (transition.getFromState() != this)
			// throw new IllegalArgumentException(
			// "transition.getFromState()!=this");

			if (transition.charSet == null) {
				for (Transition prevTrans = null, trans = this.eTransitions; trans != null; prevTrans = trans, trans = trans.next) {
					if (trans == transition) {
						if (prevTrans == null) {
							this.eTransitions = trans.next;
						} else {
							prevTrans.next = trans.next;
						}

						if (Auto.this.isDeterministic == Auto.FALSE) {
							Auto.this.isDeterministic = Auto.UNKNOWN;
						}

						if (this.changeListeners != null) {
							final Iterator it = this.changeListeners
									.iterator();
							for (int i = this.changeListeners.size(); i > 0; --i) {
								((StateChangeListener) it.next())
										.transitionRemoved(transition);
							}
						}
						return true;
					}
				}
			} else {
				for (Transition prevTrans = null, trans = this.transitions; trans != null; prevTrans = trans, trans = trans.next) {
					if (trans == transition) {
						if (prevTrans == null) {
							this.transitions = trans.next;
						} else {
							prevTrans.next = trans.next;
						}

						if (this.isDeterministic == State.FALSE) {
							this.isDeterministic = State.UNKNOWN;
						}
						if (Auto.this.isDeterministic == Auto.FALSE) {
							Auto.this.isDeterministic = Auto.UNKNOWN;
						}

						if (this.changeListeners != null) {
							final Iterator it = this.changeListeners
									.iterator();
							for (int i = this.changeListeners.size(); i > 0; --i) {
								((StateChangeListener) it.next())
										.transitionRemoved(transition);
							}
						}
						return true;
					}
				}
			}
			return false;
		}

		final void removeAll() {
			for (Transition trans = this.eTransitions; trans != null; trans = trans.next) {
				this.removeTransition(trans);
			}
			for (Transition trans = this.transitions; trans != null; trans = trans.next) {
				this.removeTransition(trans);
			}
		}

		/**
		 * 
		 * @param isDeterministic
		 */
		final void setDeterministic(final Boolean isDeterministic) {
			if (isDeterministic == null) {
				this.isDeterministic = State.UNKNOWN;
			} else if (isDeterministic.booleanValue()) {
				this.isDeterministic = State.TRUE;
			} else {
				this.isDeterministic = State.FALSE;
			}
		}

		/**
		 * 
		 * @return deterministik
		 */
		final boolean isDeterministic() {
			switch (this.isDeterministic) {
			case State.TRUE:
				return true;
			case State.FALSE:
				return false;
			case State.UNKNOWN: {
				if (this.transitions == null) {
					this.isDeterministic = State.TRUE;
					return true;
				}
				final CharSet charSet = this.transitions.charSet.clone();
				for (Transition trans = this.transitions.next; trans != null; trans = trans.next) {
					int oldSize = charSet.size();
					charSet.addAll(trans.charSet);
					int newSize = charSet.size();
					if (newSize - oldSize < trans.charSet.size()) {
						this.isDeterministic = State.FALSE;
						return false;
					}
				}

				this.isDeterministic = State.TRUE;
				return true;
			}
			default:
				throw new Error("Unknown deterministic state: "
						+ this.isDeterministic);
			}
		}

		/**
		 * 
		 * @param ch
		 * @return
		 */
		public final IState next(final char ch) {
			this.unVisit();
			if (this.isDeterministic()) {
				for (Transition trans = this.transitions; trans != null; trans = trans.next) {
					if (trans.charSet.contains(ch)) {
						final Auto.State toState = trans.visit(ch);
						if (toState.eTransitions == null) {
							if (toState.visitListeners != null) {
								final Iterator it = this.visitListeners
										.iterator();
								for (int i = this.visitListeners.size(); i > 0; --i) {
									((StateVisitListener) it.next())
											.stateVisited(toState, ch);
								}
							}
							return toState;
						} else {
							final LinkedState statesToVisit = Auto.this
									.newLinkedState(toState);
							for (StateWrap w = statesToVisit.elements; w != null; w = w.next) {
								if (w.state.visitListeners != null) {
									final Iterator it = w.state.visitListeners
											.iterator();
									for (int i = w.state.visitListeners
											.size(); i > 0; --i) {
										((StateVisitListener) it.next())
												.stateVisited(w.state, ch);
									}
								}

								for (State.Transition t = w.state.eTransitions; t != null; t = t.next) {
									t.visit(statesToVisit);
								}
							}
							return statesToVisit;
						}
					}
				}
				return null;

			} else {

				final LinkedState statesToVisit = Auto.this.newLinkedState();
				for (Transition trans = this.transitions; trans != null; trans = trans.next) {
					if (trans.charSet.contains(ch)) {
						trans.visit(ch, statesToVisit);
					}
				}

				for (StateWrap w = statesToVisit.elements; w != null; w = w.next) {
					if (w.state.visitListeners != null) {
						final Iterator it = w.state.visitListeners.iterator();
						for (int i = w.state.visitListeners.size(); i > 0; --i) {
							((StateVisitListener) it.next()).stateVisited(
									w.state, ch);
						}
					}

					for (Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
						trans.visit(statesToVisit);
					}
				}

				switch (statesToVisit.size) {
				case 0:
					return null;
				case 1:
					return statesToVisit.elements.state;
				default:
					return statesToVisit;
				}
			}

		}

		final IState getEClosure() {
			if (this.eTransitions == null) {
				return this;
			}
			final LinkedState eClosure = Auto.this.newLinkedState(this);
			for (StateWrap w = eClosure.elements; w != null; w = w.next) {
				for (Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					eClosure.add(trans.toState);
				}
			}
			switch (eClosure.size) {
			case 1:
				return eClosure.elements.state;
			default:
				return eClosure;
			}
		}

		/**
		 * 
		 * @param eClosure
		 */
		final void addEClosure(final LinkedState eClosure) {
			eClosure.add(this);
			for (Transition trans = this.eTransitions; trans != null; trans = trans.next) {
				eClosure.add(trans.toState);
			}
			StateWrap w = eClosure.lastElement;
			for (w = w.next; w != null; w = w.next) {
				for (Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					eClosure.add(trans.toState);
				}
			}
		}

		/**
		 * 
		 * @return reachableState
		 */
		@Override
		public final LinkedState reachableState() {
			final LinkedState states = new LinkedState();

			for (Transition trans = this.eTransitions; trans != null; trans = trans.next) {
				states.add(trans.toState);
			}

			for (Transition trans = this.transitions; trans != null; trans = trans.next) {
				if (trans.charSet.isEmpty() == false) {
					states.add(trans.toState);
				}
			}

			for (StateWrap w = states.elements; w != null; w = w.next) {
				for (Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					states.add(trans.toState);
				}
				for (Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					if (trans.charSet.isEmpty() == false) {
						states.add(trans.toState);
					}
				}
			}

			return states;
		}

		@Override
		public final int hashCode() {
			return super.hashCode();
		}

		@Override
		public final Object clone() {
			return Auto.this.cloneState(this).get(this);
		}

		@Override
		public final boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return Auto.this.automatonNr + ".(" + String.valueOf(this.stateNr)
					+ ')';
		}
	}

	class LinkedState implements IState {

		transient StateWrap elements = null;
		transient StateWrap lastElement = null;
		transient int size = 0;
		transient int hashCode = 0;
		transient boolean hashCodeIsValid = true;

		LinkedState() {
		}

		/**
		 * 
		 * @param state
		 */
		LinkedState(final Auto.State state) {
			this.add(state);
		}

		/**
		 * 
		 * @param state
		 * @return
		 */
		final boolean add(final Auto.State state) {
			if (this.contains(state)) {
				return false;
			}

			if (this.lastElement == null) {
				this.elements = new StateWrap(state);
				this.lastElement = this.elements;
			} else {
				this.lastElement.next = new StateWrap(state);
				this.lastElement = this.lastElement.next;
			}
			this.hashCodeIsValid = false;
			++this.size;
			return true;
		}

		/**
		 * 
		 * @param states
		 */
		final void addAll(final LinkedState states) {
			for (StateWrap wrapper = states.elements; wrapper != null; wrapper = wrapper.next) {
				this.add(wrapper.state);
			}
		}

		/**
		 * 
		 * @param state
		 */
		final void addAll(final IState state) {
			if (state instanceof State) {
				this.add((State) state);
			} else {
				this.addAll((LinkedState) state);
			}
		}

		/**
		 * 
		 * @param state
		 * @return
		 */
		final boolean remove(final Auto.State state) {
			StateWrap prev = null;
			for (StateWrap wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
				if (wrapper.state == state) {
					if (prev == null) {
						this.elements = wrapper.next;
					} else {
						prev.next = wrapper.next;
					}

					if (wrapper == this.lastElement) {
						this.lastElement = prev;
					}

					this.hashCodeIsValid = false;
					--this.size;
					return true;
				}
				prev = wrapper;
			}
			return false;
		}

		/**
		 * 
		 * @param state
		 * @return
		 */
		final boolean contains(final Auto.State state) {
			// performance leak
			// if (this.size != 0
			// && this.elements.state.parent() != state.parent())
			// throw new IllegalArgumentException(
			// "this.elements.state.parent()!=state.parent()");

			for (StateWrap wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
				if (wrapper.state == state) {
					return true;
				}
			}
			return false;
		}

		final void clear() {
			this.elements = null;
			this.lastElement = null;
			this.size = 0;
		}

		/**
		 * 
		 * @return size
		 */
		final int size() {
			return this.size;
		}

		/**
		 * 
		 * @return size==0
		 */
		final boolean isEmpty() {
			return this.size == 0;
		}

		@Override
		public final boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			try {
				return this.equals((LinkedState) obj);
			} catch (ClassCastException e) {
				if ((obj instanceof LinkedState) == false) {
					throw new IllegalArgumentException(
							"obj not instanceof LinkedState");
				}
				throw e;
			}
		}

		/**
		 * 
		 * @param set
		 * @return
		 */
		final boolean equals(LinkedState set) {
			if (this == set) {
				return true;
			}
			try {
				if (this.size != set.size) {
					return false;
				}
				for (StateWrap wrapper = set.elements; wrapper != null; wrapper = wrapper.next) {
					if (this.contains(wrapper.state) == false) {
						return false;
					}
				}
				return true;
			} catch (NullPointerException e) {
				if (set == null) {
					throw new IllegalArgumentException("set==null");
				}
				throw e;
			}
		}

		@Override
		public final int hashCode() {
			if (this.hashCodeIsValid == false) {
				long hash = 0;
				for (StateWrap wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
					hash += wrapper.state.hashCode();
				}
				this.hashCode = (int) (hash % 4294967291L);
			}
			return this.hashCode;
		}

		@Override
		public final Object clone() {
			try {
				final LinkedState clone = (LinkedState) super.clone();
				clone.clear();
				clone.addAll(this);
				return clone;
			} catch (CloneNotSupportedException e) {
				throw new Error();
			}
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder(32);
			result.append('(');
			for (StateWrap wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
				if (wrapper != this.elements) {
					result.append(", ");
				}
				result.append(wrapper.state.toString());
			}
			result.append(')');
			return result.toString();
		}

		@Override
		public final LinkedState reachableState() {
			StateWrap wrapper = this.elements;
			for (int i = this.size; i > 0; --i) {
				for (State.Transition trans = wrapper.state.transitions; trans != null; trans = trans.next) {
					this.add(trans.toState);
				}
				wrapper = wrapper.next;
			}
			for (; wrapper != null; wrapper = wrapper.next) {
				for (State.Transition trans = wrapper.state.eTransitions; trans != null; trans = trans.next) {
					this.add(trans.toState);
				}
				for (State.Transition trans = wrapper.state.transitions; trans != null; trans = trans.next) {
					this.add(trans.toState);
				}
			}
			return this;
		}

		/**
		 * 
		 * @param ch
		 * @return
		 */
		@Override
		public final IState next(final char ch) {
			final LinkedState statesToVisit = Auto.this.newLinkedState();

			for (StateWrap w = this.elements; w != null; w = w.next) {
				w.state.unVisit();
			}

			for (StateWrap w = this.elements; w != null; w = w.next) {
				if (w.state.isDeterministic()) {
					for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
						if (trans.charSet.contains(ch)) {
							trans.visit(ch, statesToVisit);
							break;
						}
					}
				} else {
					for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
						if (trans.charSet.contains(ch)) {
							trans.visit(ch, statesToVisit);
						}
					}
				}
			}

			for (StateWrap w = statesToVisit.elements; w != null; w = w.next) {
				if (w.state.visitListeners != null) {
					final Iterator it = w.state.visitListeners.iterator();
					for (int i = w.state.visitListeners.size(); i > 0; --i) {
						((StateVisitListener) it.next()).stateVisited(w.state,
								ch);
					}
				}

				for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					trans.visit(statesToVisit);
				}
			}
			switch (statesToVisit.size) {
			case 0:
				return null;
			case 1:
				return statesToVisit.elements.state;
			default:
				return statesToVisit;
			}
		}
	}

	transient State startState = null;
	transient LinkedState aStates = new LinkedState();
	transient int isDeterministic = Auto.TRUE;
	transient int automatonNr = Auto.currentAutomatonNr++;
	transient int currentStateNr = 0;

	/**
	 * 
	 * @return
	 */
	abstract LinkedState newLinkedState();

	/**
	 * 
	 * @param state
	 * @return
	 */
	abstract LinkedState newLinkedState(State state);

	State createState() {
		return new State();
	}

	/**
	 * 
	 * @param isDeterministic
	 */
	final void setDeterminstic(final Boolean isDeterministic) {
		if (isDeterministic == null) {
			this.isDeterministic = Auto.UNKNOWN;
		} else if (isDeterministic.booleanValue()) {
			this.isDeterministic = Auto.TRUE;
		} else {
			this.isDeterministic = Auto.FALSE;
		}
	}

	/**
	 * 
	 * @return deterministic
	 */
	final boolean isDeterministic() {
		if (this.startState == null || this.isDeterministic(this.startState)) {
			this.isDeterministic = Auto.TRUE;
			return true;
		} else {
			this.isDeterministic = Auto.FALSE;
			return false;
		}
	}

	/**
	 * 
	 * @param startState
	 * @return
	 */
	final boolean isDeterministic(final State startState) {
		final LinkedState reach = new LinkedState(startState);
		for (StateWrap w = reach.elements; w != null; w = w.next) {
			if (w.state.eTransitions != null
					|| w.state.isDeterministic() == false) {
				return false;
			}

			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				reach.add(trans.toState);
			}
		}
		return true;
	}

	/**
	 * 
	 * @return newState
	 */
	final State addState() {
		final State result = this.createState();
		this.addState(result);
		return result;
	}

	/**
	 * 
	 * @param startState
	 */
	final void setStartState(final State startState) {
		if (startState == this.startState) {
			return;
		}

		// performance leak
		// if (startState != null) {
		// if (startState.parent() != this)
		// throw new IllegalArgumentException("startState.parent()!=this");
		// if (this.aStates.contains(startState) == false)
		// throw new IllegalArgumentException(
		// "this.states.contains(startState=" + startState
		// + ")==false");
		// }

		final State oldStartState = this.startState;
		this.startState = startState;

		this.isDeterministic = Auto.UNKNOWN;

		// inform listener
		if (this.listeners != null) {
			final Iterator it = this.listeners.iterator();
			for (int i = this.listeners.size(); i > 0; --i) {
				((ChangedListener) it.next()).firstStateChange(oldStartState,
						startState);
			}
		}
	}

	/**
	 * 
	 * @return startState
	 */
	State getStartState() {
		return this.startState;
	}

	/**
	 * 
	 * @param state
	 */
	final void addState(final State state) {
		/* performance leak
		* if (removeState.parent() != this)
		 *throw new IllegalArgumentException("removeState.parent()!=this");
			*/
		this.aStates.add(state);

		// inform listener
		if (this.listeners != null) {
			final Iterator it = this.listeners.iterator();
			for (int i = this.listeners.size(); i > 0; --i) {
				((ChangedListener) it.next()).stateAdd(state);
			}
		}
	}

	/**
	 * 
	 * @param removeState
	 * @return
	 */
	final boolean removeState(final State removeState) {
		/* performance leak
		* if (removeState.parent() != this)
		 *throw new IllegalArgumentException("removeState.parent()!=this");
			*/
		if (this.startState == removeState) {
			this.setStartState(null);
		}

		for (StateWrap w = this.aStates.elements; w != null; w = w.next) {
			if (w.state != removeState) {
				for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
					if (trans.toState == removeState) {
						w.state.removeTransition(trans);
					}
				}
				for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
					if (trans.toState == removeState) {
						w.state.removeTransition(trans);
					}
				}
			}
		}

		if (this.aStates.remove(removeState) == false) {
			return false;
		}

		if (this.listeners != null) {
			final Iterator it = this.listeners.iterator();
			for (int i = this.listeners.size(); i > 0; --i) {
				((ChangedListener) it.next()).stateRemove(removeState);
			}
		}

		return true;
	}

	/**
	 * remove all unreach states
	 */
	final void removeUnreachStates() {
		if (this.startState == null) {
			return;
		}

		final LinkedState states = this.startState.reachableState();
		states.add(this.startState);
		for (StateWrap w = this.aStates.elements; w != null; w = w.next) {
			if (states.contains(w.state) == false) {
				this.removeState(w.state);
			}
		}
	}

	/**
	 * 
	 * remove all State
	 * 
	 */
	void clear() {
		for (StateWrap w = this.aStates.elements; w != null; w = w.next) {
			this.removeState(w.state);
		}
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	Map cloneState(final State state) {
		final HashMap map = new HashMap(18);
		final LinkedState states = new LinkedState(state);
		for (StateWrap w = states.elements; w != null; w = w.next) {
			for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
				states.add(trans.toState);
			}

			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				states.add(trans.toState);
			}

			map.put(w.state, this.addState());
		}
		for (StateWrap w = states.elements; w != null; w = w.next) {
			State newState = (State) map.get(w.state);
			for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
				if (trans.properties == null) {
					newState.addTransition(null, null,
							(State) map.get(trans.toState));
				} else {
					newState.addTransition((Prop) trans.properties.clone(),
							null, (State) map.get(trans.toState));
				}
			}
			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				if (trans.properties == null) {
					newState.addTransition(null,
							(CharSet) trans.charSet.clone(),
							(State) map.get(trans.toState));
				} else {
					newState.addTransition((Prop) trans.properties.clone(),
							(CharSet) trans.charSet.clone(),
							(State) map.get(trans.toState));
				}
			}
		}

		return map;
	}

	/**
	 * 
	 * @param states
	 * @return
	 */
	Map cloneStates(final LinkedState states) {
		final HashMap map = new HashMap(18);
		for (StateWrap w = states.elements; w != null; w = w.next) {
			map.put(w.state, this.addState());
		}

		for (StateWrap w = states.elements; w != null; w = w.next) {
			final State newState = (State) map.get(w.state);
			for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
				if (trans.properties == null) {
					newState.addTransition(null, null,
							(State) map.get(trans.toState));
				} else {
					newState.addTransition((Prop) trans.properties.clone(),
							null, (State) map.get(trans.toState));
				}
			}
			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				if (trans.properties == null) {
					newState.addTransition(null,
							(CharSet) trans.charSet.clone(),
							(State) map.get(trans.toState));
				} else {
					newState.addTransition((Prop) trans.properties.clone(),
							(CharSet) trans.charSet.clone(),
							(State) map.get(trans.toState));
				}
			}
		}

		return map;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aStates == null) ? 0 : aStates.hashCode());
		result = prime * result + automatonNr;
		result = prime * result + currentStateNr;
		result = prime * result + isDeterministic;
		result = prime * result
				+ ((listeners == null) ? 0 : listeners.hashCode());
		result = prime * result
				+ ((startState == null) ? 0 : startState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Auto other = (Auto) obj;
		if (aStates == null) {
			if (other.aStates != null)
				return false;
		} else if (!aStates.equals(other.aStates))
			return false;
		if (automatonNr != other.automatonNr)
			return false;
		if (currentStateNr != other.currentStateNr)
			return false;
		if (isDeterministic != other.isDeterministic)
			return false;
		if (listeners == null) {
			if (other.listeners != null)
				return false;
		} else if (!listeners.equals(other.listeners))
			return false;
		if (startState == null) {
			if (other.startState != null)
				return false;
		} else if (!startState.equals(other.startState))
			return false;
		return true;
	}

	@Override
	public final String toString() {
		final StringBuilder buffer = new StringBuilder(64);
		for (StateWrap w = this.aStates.elements; w != null; w = w.next) {
			buffer.append("  \n").append(w.state);

			if (w.state == this.startState) {
				buffer.append('+');
			}

			for (State.Transition trans = w.state.eTransitions; trans != null; trans = trans.next) {
				buffer.append(" \n  -");
				if (trans.properties != null) {
					buffer.append(trans.properties).append(": ");
				}
				buffer.append("-> ").append(trans.toState);
			}

			for (State.Transition trans = w.state.transitions; trans != null; trans = trans.next) {
				buffer.append(" \n  -");
				if (trans.properties != null) {
					buffer.append(trans.properties).append(": ");
				}
				buffer.append(trans.charSet).append("-> ")
						.append(trans.toState);
			}
		}
		return buffer.toString();
	}

	/**
	 * @see java.lang.Cloneable
	 * @return clone
	 */
	@Override
	public Auto clone() {
		try {
			final Auto clone = (Auto) super.clone();
			clone.automatonNr = Auto.currentAutomatonNr++;
			clone.currentStateNr = 0;
			clone.startState = null;
			clone.aStates = clone.newLinkedState();
			final Map map = clone.cloneStates(this.aStates);
			final Set keys = map.entrySet();
			final Iterator it = keys.iterator();
			for (int i = keys.size(); i > 0; --i) {
				Map.Entry entry = (Map.Entry) it.next();
				State oldState = (State) entry.getKey();
				State newState = (State) entry.getValue();
				newState.stateNr = oldState.stateNr;
				if (clone.currentStateNr <= newState.stateNr) {
					clone.currentStateNr = newState.stateNr + 1;
				}
			}

			if (this.startState != null) {
				clone.setStartState((State) map.get(this.startState));
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new Error();
		}
	}
}
