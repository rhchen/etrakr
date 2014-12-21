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
 */

class SAuto implements Cloneable {

	interface IChangeListener {

		/**
		 * 
		 * @param state
		 */
		void stateAdded(SttPro state);

		/**
		 * 
		 * @param state
		 */
		void stateRemoved(SttPro state);

		/**
		 * 
		 * @param oldStartState
		 * @param newStartState
		 */
		void startStateChanged(SttPro oldStartState, SttPro newStartState);
	}

	final class State implements States, Cloneable {

		final SetString.ISState state;

		/**
		 * 
		 * @param state
		 */
		State(final SetString.ISState state) {
			this.state = state;
		}

		/**
		 * 
		 * @see SetString.ISState
		 */
		public final boolean isFinal() {
			return this.state.isFinal();
		}

		/**
		 * 
		 * @param ch
		 * @return
		 */
		public final State next(final char ch) {
			final Auto.IState nextState = this.state.next(ch);
			return nextState == null ? null : new State(
					(SetString.ISState) nextState);
		}

		@Override
		public final SetState reachState() {
			final SetState result = new SetState();
			final Auto.LinkedState states = this.state.reachableState();
			for (Auto.StateWrap w = states.elements; w != null; w = w.next) {

				SAuPro wrapper = SAuto.this.wrapState.get(w.state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) w.state);
				}
				result.add(wrapper);
			}
			return result;
		}

		@Override
		public final boolean equals(final Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}

		@Override
		public final int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}

		@Override
		public final Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public final String toString() {
			return this.state.toString();
		}
	}

	final class Trans implements SttPro.Tran, Cloneable {

		transient final Auto.State.Transition transition;

		Trans(final Auto.State.Transition transition) {
			this.transition = transition;
			SAuto.this.transition2wrapper.put(transition, Trans.this);
		}

		public final SttPro from() {
			SAuPro wrapper = SAuto.this.wrapState.get(this.transition
					.getFromState());
			if (wrapper == null) {
				wrapper = new SAuPro(
						(SetString.SState) this.transition.getFromState());
			}
			return wrapper;
		}

		final Set getLabels() {
			final Object labels =  this.transition.properties;
			if (labels != null) {
				return (Set)labels;
			}
			return java.util.Collections.EMPTY_SET;
		}

		public final CharSet charSet() {
			return this.transition.getCharSet();
		}

		public final SttPro to() {
			SAuPro wrapper = SAuto.this.wrapState.get(this.transition
					.getToState());
			if (wrapper == null) {
				wrapper = new SAuPro(
						(SetString.SState) this.transition.getToState());
			}
			return wrapper;
		}

		@Override
		public final Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public final boolean equals(final Object obj) {
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
			final StringBuilder buffer = new StringBuilder(48);
			final SetString.SState fromState = (SetString.SState) this.transition
					.getFromState();
			final SetString.SState toState = (SetString.SState) this.transition
					.getToState();

			if (fromState.isFinal()) {
				buffer.append('[').append(fromState.stateNr).append(']');
			} else {
				buffer.append('(').append(fromState.stateNr).append(')');
			}

			if (this.transition.getCharSet() == null) {
				if (this.transition.properties == null) {
					buffer.append(" --> ");
				} else {
					buffer.append(" - ").append(this.transition.properties)
							.append(": -> ");
				}
			} else {
				if (this.transition.properties == null) {
					buffer.append(" - ").append(this.transition.getCharSet())
							.append(" -> ");
				} else {
					buffer.append(" - ").append(this.transition.properties)
							.append(':').append(this.transition.getCharSet())
							.append(" ->");
				}
			}

			if (toState.isFinal()) {
				buffer.append('[').append(toState.stateNr).append(']');
			} else {
				buffer.append('(').append(toState.stateNr).append(')');
			}

			return buffer.toString();
		}
	}

	final class SAuPro implements SttPro, Cloneable {

		final Auto.StateVisitListener stateVisitedListener = new Auto.StateVisitListener() {

			public final void stateVisited(final Auto.State state) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuPro.this.visitListeners.iterator();
				for (int i = SAuPro.this.visitListeners.size(); i > 0; --i) {
					((SttPro.ViewListener) it.next()).look(wrapper);
				}
			}

			public final void stateVisited(final Auto.State state, final char ch) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuPro.this.visitListeners.iterator();
				for (int i = SAuPro.this.visitListeners.size(); i > 0; --i) {
					((SttPro.ViewListener) it.next()).look(wrapper, ch);
				}
			}

			public final void stateUnVisited(final Auto.State state) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuPro.this.visitListeners.iterator();
				for (int i = SAuPro.this.visitListeners.size(); i > 0; --i) {
					((SttPro.ViewListener) it.next()).unvisit(wrapper);
				}
			}
		};
		final Auto.StateChangeListener stateChangedListener = new SetString.ISStateChangedListener() {

			public final void transitionAdded(
					final Auto.State.Transition transition) {
				Tran wrapper = SAuto.this.transition2wrapper.get(transition);
				if (wrapper == null) {
					wrapper = new Trans(transition);
				}

				final Iterator it = SAuPro.this.changeListeners.iterator();
				for (int i = SAuPro.this.changeListeners.size(); i > 0; --i) {
					((SttPro.ChangeListener) it.next()).add(wrapper);
				}
			}

			public final void transitionRemoved(
					final Auto.State.Transition transition) {
				Tran wrapper = SAuto.this.transition2wrapper.get(transition);
				if (wrapper == null) {
					wrapper = new Trans(transition);
				}
				final Iterator it = SAuPro.this.changeListeners.iterator();
				for (int i = SAuPro.this.changeListeners.size(); i > 0; --i) {
					((SttPro.ChangeListener) it.next()).remove(wrapper);
				}
			}

			public final void isFinalChanged(final SetString.SState state,
					final boolean isFinal) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuPro.this.changeListeners.iterator();
				for (int i = SAuPro.this.changeListeners.size(); i > 0; --i) {
					((SttPro.ChangeListener) it.next())
							.change(wrapper, isFinal);
				}
			}
		};
		transient final ArrayList visitListeners = new ArrayList(5);
		transient ArrayList changeListeners = new ArrayList(5);

		public final void addViewListener(final SttPro.ViewListener listener) {
			this.visitListeners.add(listener);
		}

		public final boolean removeViewListener(
				final SttPro.ViewListener listener) {
			final Iterator it = this.visitListeners.iterator();
			for (int i = this.visitListeners.size(); i > 0; --i) {
				if (listener == it.next()) {
					if (this.visitListeners.size() > 1) {
						it.remove();
					} else {
						this.state
								.removeVisitedListener(this.stateVisitedListener);
					}
					return true;
				}
			}
			return false;
		}

		public final void addChangeListener(final SttPro.ChangeListener listener) {
			this.changeListeners.add(listener);
		}

		public final boolean removeChangeListener(
				final SttPro.ChangeListener listener) {
			final Iterator it = this.changeListeners.iterator();
			for (int i = this.changeListeners.size(); i > 0; --i) {
				if (listener == it.next()) {
					if (this.changeListeners.size() > 1) {
						it.remove();
					} else {
						this.state
								.removeChangedListener(this.stateChangedListener);
						this.changeListeners = null;
					}
					return true;
				}
			}
			return false;
		}

		final transient SetString.SState state;

		SAuPro(final SetString.SState state) {
			// if (state == null) {
			// throw new Error("state==null");
			// }
			this.state = state;
			SAuto.this.wrapState.put(state, SAuPro.this);
		}

		// @Override
		// protected final void finalize() {
		// SAuto.this.wrapState.remove(this.state);
		// }

		final SAuto parent() {
			return SAuto.this;
		}

		public final boolean isFinal() {
			return this.state.isFinal();
		}

		public final void setFinal(final boolean isFinal) {
			this.state.setFinal(isFinal);
		}

		public final State look() {
			return new State((SetString.ISState) this.state.visit());
		}

		public final SttPro.Tran addTran(final CharSet charSet,
				final SttPro toState) {
			final SetString.SState.Transition trans = this.state.addTransition(
					null, charSet, ((SAuPro) toState).state);
			Tran wrapper = SAuto.this.transition2wrapper.get(trans);
			if (wrapper == null) {
				wrapper = new Trans(trans);
			}
			return wrapper;
		}

		public final boolean removeTran(final SttPro.Tran transition) {
			return this.state.removeTransition(((Trans) transition).transition);
		}

		public final void removeAll() {
			this.state.removeAll();
		}

		public final SetState reachState() {
			final SetState result = new SetState();
			final Auto.LinkedState states = this.state.reachableState();
			for (Auto.StateWrap w = states.elements; w != null; w = w.next) {
				SAuPro wrapper = SAuto.this.wrapState.get(w.state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) w.state);
				}
				result.add(wrapper);
			}
			return result;
		}

		public final SttPro.Tran[] getTrans() {
			final ArrayList list = new ArrayList(15);
			for (SetString.State.Transition trans = this.state.transitions; trans != null; trans = trans.next) {
				Tran wrapper = SAuto.this.transition2wrapper.get(trans);
				if (wrapper == null) {
					wrapper = new Trans(trans);
				}
				list.add(wrapper);
			}
			return (SttPro.Tran[]) list.toArray(new SttPro.Tran[list.size()]);
		}

		public final SttPro.Tran[] getETrans() {
			final ArrayList list = new ArrayList(15);
			for (SetString.State.Transition trans = this.state.eTransitions; trans != null; trans = trans.next) {
				Tran wrapper = SAuto.this.transition2wrapper.get(trans);
				if (wrapper == null) {
					wrapper = new Trans(trans);
				}
				list.add(wrapper);
			}
			return (SttPro.Tran[]) list.toArray(new SttPro.Tran[list.size()]);
		}

		public final SttPro.Tran[] getAllTrans() {
			final ArrayList list = new ArrayList(16);
			for (SetString.State.Transition trans = this.state.transitions; trans != null; trans = trans.next) {
				Tran wrapper = SAuto.this.transition2wrapper.get(trans);
				if (wrapper == null) {
					wrapper = new Trans(trans);
				}
				list.add(wrapper);
			}
			for (SetString.State.Transition trans = this.state.eTransitions; trans != null; trans = trans.next) {
				Tran wrapper = SAuto.this.transition2wrapper.get(trans);
				if (wrapper == null) {
					wrapper = new Trans(trans);
				}
				list.add(wrapper);
			}
			return (SttPro.Tran[]) list.toArray(new SttPro.Tran[list.size()]);
		}

		@Override
		public final Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public final boolean equals(final Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}

		@Override
		public final int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}

		public final int stateNum() {
			return this.state.stateNr;
		}

		@Override
		public final String toString() {
			if (this.isFinal()) {
				return "[" + this.state.stateNr + "]";
			}
			return "(" + this.state.stateNr + ")";
		}
	}

	private transient MapSState wrapState = null;
	transient MapSTransition transition2wrapper = null;
	transient Auto.ChangedListener automatonChangedListener = null;

	final Auto.ChangedListener getAutomatonChangedListener() {
		if (this.automatonChangedListener != null) {
			return this.automatonChangedListener;
		}

		this.automatonChangedListener = new Auto.ChangedListener() {

			public final void stateAdd(final Auto.State state) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuto.this.listeners.iterator();
				for (int i = SAuto.this.listeners.size(); i > 0; --i) {
					((SAuto.IChangeListener) it.next()).stateAdded(wrapper);
				}
			}

			public final void stateRemove(final Auto.State state) {
				SAuPro wrapper = SAuto.this.wrapState.get(state);
				if (wrapper == null) {
					wrapper = new SAuPro((SetString.SState) state);
				}

				final Iterator it = SAuto.this.listeners.iterator();
				for (int i = SAuto.this.listeners.size(); i > 0; --i) {
					((SAuto.IChangeListener) it.next()).stateRemoved(wrapper);
				}
			}

			public final void firstStateChange(final Auto.State oldStartState,
					final Auto.State newStartState) {
				SAuPro oldWrapper = null;
				if (oldStartState != null) {
					oldWrapper = SAuto.this.wrapState.get(oldStartState);
					if (oldWrapper == null) {
						oldWrapper = new SAuPro(
								(SetString.SState) oldStartState);
					}
				}

				SAuPro newWrapper = null;
				if (newStartState != null) {
					newWrapper = SAuto.this.wrapState.get(newStartState);
					if (newWrapper == null) {
						newWrapper = new SAuPro(
								(SetString.SState) newStartState);
					}
				}

				final Iterator it = SAuto.this.listeners.iterator();
				for (int i = SAuto.this.listeners.size(); i > 0; --i) {
					((SAuto.IChangeListener) it.next()).startStateChanged(
							oldWrapper, newWrapper);
				}
			}
		};

		return this.automatonChangedListener;
	}

	transient ArrayList listeners = null;

	final void addChangeListener(final SAuto.IChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList(5);
			((SetString) this.automaton).addChangedListener(this
					.getAutomatonChangedListener());
		}
		this.listeners.add(listener);
	}

	final boolean removeChangeListener(final SAuto.IChangeListener listener) {
		if (this.listeners != null) {
			final Iterator it = this.listeners.iterator();
			for (int i = this.listeners.size(); i > 0; --i) {
				if (listener == it.next()) {
					if (this.listeners.size() > 1) {
						it.remove();
					} else {
						this.automaton
								.removeChangedListener(this.automatonChangedListener);
						this.automatonChangedListener = null;
						this.listeners = null;
					}
					return true;
				}
			}
		}
		return false;
	}

	transient PatInt automaton;

	SAuto() {
		this(new PatInt());
	}

	SAuto(final Data data) {
		this(new PatInt());
		this.init(data);
	}

	SAuto(final PatInt automaton) {
		this.automaton = automaton;
		this.wrapState = new MapSState(20);
		this.transition2wrapper = new MapSTransition(20);
	}

	final boolean isDeterministic() {
		return this.automaton.isDeterministic();
	}

	final SttPro getStartState() {
		Auto.State startState = ((SetString) this.automaton).getStartState();
		if (startState == null) {
			return null;
		}
		SAuPro wrapper = (SAuPro) this.wrapState.get(startState);
		if (wrapper == null) {
			wrapper = new SAuPro((SetString.SState) startState);
		}
		return wrapper;
	}

	final void setStartState(SttPro state) {
		final SAuPro wrapper = (SAuPro) state;
		this.automaton.setStartState(wrapper.state);
	}

	final SttPro addState() {
		return this.addState(false);
	}

	final SttPro addState(boolean isFinal) {
		final SetString.SState newState = this.automaton.addState(isFinal);
		SAuPro wrapper = SAuto.this.wrapState.get(newState);
		if (wrapper == null) {
			wrapper = new SAuPro((SetString.SState) newState);
		}
		return wrapper;
	}

	final boolean removeState(SttPro state) {
		// if ((state instanceof SAuPro) == false)
		// throw new IllegalArgumentException("state is no state of mine");
		final SAuPro wrapper = (SAuPro) state;
		if (wrapper.parent() != this) {
			throw new IllegalArgumentException("state is no state of mine");
		}
		return this.automaton.removeState(wrapper.state);
	}

	final void clear() {
		this.automaton.clear();
	}

	final void minimize() {
		this.automaton.minimize();
	}

	final SetState getStates() {
		final SetState result = new SetState();
		final Auto.LinkedState states = this.automaton.getStates();
		for (Auto.StateWrap w = states.elements; w != null; w = w.next) {
			SAuPro wrapper = SAuto.this.wrapState.get(w.state);
			if (wrapper == null) {
				wrapper = new SAuPro((SetString.SState) w.state);
			}
			result.add(wrapper);
		}
		return result;
	}

	final void complement() {
		this.automaton.complement();
	}

	final void addAll(SAuto automaton) {
		this.automaton.addAll(automaton.automaton);
	}

	final void retainAll(SAuto automaton) {
		this.automaton.retainAll(automaton.automaton);
	}

	final void removeAll(SAuto automaton) {
		this.automaton.removeAll(automaton.automaton);
	}

	/**
	 * 
	 * @param charSet
	 * @return
	 */
	private String getCharSet(final CharSet charSet) {
		if (charSet == null) {
			return null;
		}
		final StringBuilder buffer = new StringBuilder(charSet.size());
		CharSet.Iterator it_charSet = charSet.iterator();
		for (int i = charSet.size(); i > 0; --i) {
			buffer.append(it_charSet.next());
		}

		CharSet cs = new SetChar(buffer.toString());
		if (cs.equals(charSet) == false) {
			throw new Error(charSet + "   " + cs);
		}

		return buffer.toString();
	}

	final Data toData() {
		Auto.LinkedState xxx = this.automaton.getStates();
		Auto.State[] states = new Auto.State[xxx.size()];
		int x = 0;
		for (Auto.StateWrap w = xxx.elements; w != null; w = w.next, ++x) {
			states[x] = w.state;
		}

		Data.Stt[] data_states = new Data.Stt[states.length];
		for (int i = 0; i < states.length; ++i) {
			ArrayList data_transitions = new ArrayList(16);
			for (Auto.State.Transition trans = states[i].transitions; trans != null; trans = trans.next) {
				// int toStateNr = 0; while (states[toStateNr]!=trans.toState)
				// ++toStateNr;
				data_transitions.add(new Data.Stt.Tran(trans.properties, this
						.getCharSet(trans.charSet) // ,toStateNr
						, trans.toState.stateNr));
			}
			for (Auto.State.Transition trans = states[i].eTransitions; trans != null; trans = trans.next) {
				// int toStateNr = 0; while (states[toStateNr]!=trans.toState)
				// ++toStateNr;
				data_transitions.add(
				// new
				// SAutomatonData.State.Transition(trans.properties,null,toStateNr)
						new Data.Stt.Tran(trans.properties, null,
								trans.toState.stateNr));
			}

			Data.Stt.Tran[] transitions = (Data.Stt.Tran[]) data_transitions
					.toArray(new Data.Stt.Tran[data_transitions.size()]);
			data_states[i] = new Data.Stt(states[i].stateNr, states[i].isFinal,
					transitions, states[i].isDeterministic());
		}

		final Auto.State startState = this.automaton.getStartState();
		if (startState == null) {
			return new Data(data_states, null, this.automaton.isDeterministic());
		}

		Data result = new Data(data_states, new Integer(startState.stateNr),
				this.automaton.isDeterministic());

		return result;
	}

	final void init(final Data a) {
		final HashMap map = new HashMap(14);
		if (a.stts != null) {
			for (int i = 0; i < a.stts.length; ++i) {
				Integer stateNr = new Integer(a.stts[i].number);
				if (map.containsKey(stateNr)) {
					throw new IllegalArgumentException(
							"bad automatonData: state with number " + stateNr
									+ " does already exists");
				}

				SetString.SState state = this.automaton.addState(
						a.stts[i].isFinal, a.stts[i].number);

				map.put(stateNr, state);
			}
			final int sttLen = a.stts.length;
			for (int i = 0; i < sttLen; ++i) {
				Data.Stt stateData = a.stts[i];

				SetString.SState state = (SetString.SState) map
						.get(new Integer(stateData.number));

				if (stateData.transitions != null) {
					final int stLen = stateData.transitions.length;
					for (int t = 0; t < stLen; ++t) {
						Data.Stt.Tran transData = stateData.transitions[t];

						SetChar charSet = (transData.charSet == null) ? null
								: new SetChar(transData.charSet);

						SetString.SState toState = (SetString.SState) map
								.get(new Integer(transData.toSttNumber));
						state.addTransition(transData.properties, charSet,
								toState);
					}
				}
				state.setDeterministic(stateData.transitionsAreDeterministic);
			}
		}

		if (a.startSttNumber != null) {
			SetString.SState startState = (SetString.SState) map
					.get(a.startSttNumber);

			if (startState == null) {
				throw new IllegalArgumentException(
						"bad automatonData: startState " + a.startSttNumber
								+ " does not exists");
			}

			this.automaton.setStartState(startState);
		}

		this.automaton.setDeterministic(a.isDeterministic);
	}

	@Override
	protected final Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
