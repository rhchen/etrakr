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

import java.text.*;
import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Natalino Nugeraha
 * @version 1.0.0
 */

final class PatInt extends SetString {

	private transient Scan scanner = this.newScanner();

	/**
	 * @see net.sf.commonstringutil.SetString.LinkedSetSState
	 * @see net.sf.commonstringutil.SetString.ISState
	 */
	final class LinkedSetPState extends SetString.LinkedSetSState implements
			ISState {

		LinkedSetPState() {
			super();
		}

		/**
		 * 
		 * @param state
		 */
		LinkedSetPState(final SetString.SState state) {
			super(state);
		}
	}

	transient String regEx = null;

	/**
	 * 
	 * @param fullSet
	 */
	PatInt(final CharSet fullSet) {
		super(fullSet);
	}

	PatInt() {
		super();
		this.regEx = StringUtil.STR_EMP;
	}

	/**
	 * 
	 * @param regEx
	 */
	PatInt(final String regEx) {
		super();
		this.regEx = StringUtil.STR_EMP;
		this.addAll(regEx);
	}

	/**
	 * @see net.sf.commonstringutil.SetString#getStartState()
	 * @return
	 */
	@Override
	final Auto.State getStartState() {
		return super.getStartState();
	}

	/**
	 * @see net.sf.commonstringutil.SetString.SState
	 * @return
	 */

	@Override
	final State createState() {
		return new SetString.SState(false);
	}

	/**
	 * @see net.sf.commonstringutil.SetString.SState
	 * @param isFinal
	 * @return
	 */
	@Override
	final SState createState(final boolean isFinal) {
		return new SetString.SState(isFinal);
	}

	/**
	 * 
	 * @see net.sf.commonstringutil.PatInt.LinkedSetPState
	 * @return new LinkedSetPState
	 */
	@Override
	final LinkedState newLinkedState() {
		return new LinkedSetPState();
	}

	/**
	 * @see net.sf.commonstringutil.PatInt.LinkedSetPState
	 * @param state
	 * @return
	 */
	@Override
	final LinkedState newLinkedState(final State state) {
		return new LinkedSetPState((SetString.SState) state);
	}

	/**
	 * 
	 * @param state
	 */
	final void setStartState(SState state) {
		super.setStartState(state);
	}

	/**
	 * @see net.sf.commonstringutil.SetString#addState(boolean)
	 * @param isFinal
	 * @return statusAdd
	 */
	@Override
	final SState addState(boolean isFinal) {
		return super.addState(isFinal);
	}

	/**
	 * @see net.sf.commonstringutil.SetString#removeState(SetString.SState)
	 * @param removeState
	 * @return statusRemove
	 */
	final boolean removeState(SetString.SState removeState) {
		return super.removeState(removeState);
	}

	/**
	 * @see net.sf.commonstringutil.SetString#clear()
	 */
	@Override
	final void clear() {
		super.clear();
		this.regEx = StringUtil.STR_EMP;
	}

	/**
	 * 
	 * @see net.sf.commonstringutil.SetString#repeat(net.sf.commonstringutil.SetString.SState,
	 *      int, int)
	 * @param state
	 * @param minTimes
	 * @param maxTimes
	 * @return repeatStates
	 */
	@Override
	final SState repeat(final SState state, final int minTimes,
			final int maxTimes) {
		return super.repeat(state, minTimes, maxTimes);
	}

	/**
	 * 
	 * @see net.sf.commonstringutil.SetString#union(net.sf.commonstringutil.Auto.State,
	 *      net.sf.commonstringutil.Auto.State)
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	final SState union(final SState state_A, final SState state_B) {
		return super.union(state_A, state_B);
	}

	/**
	 * 
	 * @see net.sf.commonstringutil.SetString#intersect(net.sf.commonstringutil.Auto.State,
	 *      net.sf.commonstringutil.Auto.State)
	 * @param state_A
	 * @param state_B
	 * @return
	 */
	State intersect(SState state_A, SState state_B) {
		return super.intersect(state_A, state_B);
	}

	@Override
	final void complement() {
		super.complement();
		if (this.regEx == null) {
			return;
		}
		if (this.regEx == StringUtil.STR_EMP) {
			this.regEx = ".*";
		} else {
			this.regEx = "!(" + this.regEx + ")";
		}
	}

	/**
	 * 
	 * @param regEx
	 */
	final void addAll(final String regEx) {
		if (this.regEx == null) {
			return;
		}
		if (this.regEx == StringUtil.STR_EMP) {
			this.regEx = regEx;
		} else {
			this.regEx = new StringBuilder(this.regEx.length() + regEx.length()
					+ 5).append('(').append(this.regEx).append(')').append('|')
					.append('(').append(regEx).append(')').toString();
		}

		this.addAll(this.parseRegEx(regEx));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param regEx
	 */
	final void retainAll(final String regEx) {
		if (this.regEx == null) {
			return;
		}
		if (this.regEx.length() == 0 || regEx.length() == 0) {
			this.regEx = StringUtil.STR_EMP;
		} else {
			this.regEx = new StringBuilder(this.regEx.length() + regEx.length()
					+ 5).append('(').append(this.regEx).append(')').append('&')
					.append('(').append(regEx).append(')').toString();
		}

		this.retainAll(this.parseRegEx(regEx));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param regEx
	 */
	final void removeAll(final String regEx) {
		if (this.regEx == null) {
			return;
		}
		if (this.regEx.length() == 0) {
			this.regEx = StringUtil.STR_EMP;
		} else {
			this.regEx = new StringBuilder(this.regEx.length() + regEx.length()
					+ 6).append('(').append(this.regEx).append(')')
					.append("&!").append('(').append(regEx).append(')')
					.toString();
		}

		this.removeAll(this.parseRegEx(regEx));
		this.removeUselessStates();
	}

	/**
	 * 
	 * @param automaton
	 */
	@Override
	final void addAll(final SetString automaton) {
		super.addAll(automaton);
		final PatInt pAutomaton = (PatInt) automaton;
		if (this.regEx == null || pAutomaton.regEx == null) {
			return;
		}
		if (this.regEx.length() == 0) {
			this.regEx = pAutomaton.regEx;
		} else {
			this.regEx = new StringBuilder(this.regEx.length()
					+ pAutomaton.regEx.length() + 5).append('(')
					.append(this.regEx).append(')').append('|').append('(')
					.append(pAutomaton.regEx).append(')').toString();
		}
	}

	/**
	 * 
	 * @param automaton
	 */
	@Override
	final void retainAll(final SetString automaton) {
		super.retainAll(automaton);

		final PatInt pAutomaton = (PatInt) automaton;

		if (this.regEx == null || pAutomaton.regEx == null) {
			return;
		}
		if (this.regEx.length() == 0 || pAutomaton.regEx.length() == 0) {
			this.regEx = StringUtil.STR_EMP;
		} else {
			this.regEx = new StringBuilder(this.regEx.length()
					+ pAutomaton.regEx.length() + 5).append('(')
					.append(this.regEx).append(')').append('&').append('(')
					.append(pAutomaton.regEx).append(')').toString();
		}
	}

	/**
	 * 
	 * @param automaton
	 */
	@Override
	final void removeAll(final SetString automaton) {
		super.removeAll(automaton);

		final PatInt pAutomaton = (PatInt) automaton;

		if (this.regEx == null || pAutomaton.regEx == null) {
			return;
		}
		if (this.regEx.length() == 0) {
			this.regEx = StringUtil.STR_EMP;
		} else if (pAutomaton.regEx.length() != 0) {
			this.regEx = new StringBuilder(this.regEx.length()
					+ pAutomaton.regEx.length() + 6).append('(')
					.append(this.regEx).append(')').append("&!").append('(')
					.append(pAutomaton.regEx).append(')').toString();
		}
	}

	@Override
	public final PatInt clone() {
		final PatInt clone = (PatInt) super.clone();
		clone.scanner = clone.newScanner();
		return clone;
	}

	private static final int ERROR = -2, SHIFT = -3, REDUCE = -4, ACCEPT = -5,
			RE = 0, TERM = 1, ELEMENT = 2, notOp = 3, andOp = 4, orOp = 5,
			groupBegin = 6, groupEnd = 7, repetition = 8, label = 9,
			regExp = 10, EOF = 11;
	private static final int[][][] ACTIONTABLE = {
			// state RE TERM ELEMENT notOp andOp orOp groupBegin groupEnd
			// repetition label regExp EOF
			/* 0 */{ { SHIFT, 2 }, { SHIFT, 7 }, { SHIFT, 5 }, { SHIFT, 11 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 14 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 16 }, { ERROR, 0 } },
			/* 1 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 3 },
					{ REDUCE, 3 }, { REDUCE, 3 }, { REDUCE, 3 }, { REDUCE, 3 },
					{ REDUCE, 3 }, { REDUCE, 3 }, { REDUCE, 3 }, { REDUCE, 3 } },
			/* 2 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ACCEPT, 0 } },
			/* 3 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 1 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 1 } },
			/* 4 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { SHIFT, 13 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 } },
			/* 5 */{ { ERROR, 0 }, { SHIFT, 8 }, { SHIFT, 5 }, { SHIFT, 11 },
					{ SHIFT, 10 }, { REDUCE, 6 }, { SHIFT, 14 }, { REDUCE, 6 },
					{ SHIFT, 1 }, { SHIFT, 12 }, { SHIFT, 16 }, { REDUCE, 6 } },
			/* 6 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { REDUCE, 9 }, { ERROR, 0 }, { REDUCE, 9 },
					{ SHIFT, 1 }, { SHIFT, 12 }, { ERROR, 0 }, { REDUCE, 9 } },
			/* 7 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { SHIFT, 15 }, { ERROR, 0 }, { REDUCE, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 0 } },
			/* 8 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { REDUCE, 7 }, { ERROR, 0 }, { REDUCE, 7 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 7 } },
			/* 9 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 },
					{ ERROR, 0 }, { REDUCE, 8 }, { ERROR, 0 }, { REDUCE, 8 },
					{ ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 8 } },
			/* 10 */{ { ERROR, 0 }, { SHIFT, 9 }, { SHIFT, 5 }, { SHIFT, 11 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 14 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 16 }, { ERROR, 0 } },
			/* 11 */{ { ERROR, 0 }, { ERROR, 0 }, { SHIFT, 6 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 14 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 16 }, { ERROR, 0 } },
			/* 12 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 4 },
					{ REDUCE, 4 }, { REDUCE, 4 }, { REDUCE, 4 }, { REDUCE, 4 },
					{ REDUCE, 4 }, { REDUCE, 4 }, { REDUCE, 4 }, { REDUCE, 4 } },
			/* 13 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 2 },
					{ REDUCE, 2 }, { REDUCE, 2 }, { REDUCE, 2 }, { REDUCE, 2 },
					{ REDUCE, 2 }, { REDUCE, 2 }, { REDUCE, 2 }, { REDUCE, 2 } },
			/* 14 */{ { SHIFT, 4 }, { SHIFT, 7 }, { SHIFT, 5 }, { SHIFT, 11 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 14 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 16 }, { ERROR, 0 } },
			/* 15 */{ { SHIFT, 3 }, { SHIFT, 7 }, { SHIFT, 5 }, { SHIFT, 11 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 14 }, { ERROR, 0 },
					{ ERROR, 0 }, { ERROR, 0 }, { SHIFT, 16 }, { ERROR, 0 } },
			/* 16 */{ { ERROR, 0 }, { ERROR, 0 }, { ERROR, 0 }, { REDUCE, 5 },
					{ REDUCE, 5 }, { REDUCE, 5 }, { REDUCE, 5 }, { REDUCE, 5 },
					{ REDUCE, 5 }, { REDUCE, 5 }, { REDUCE, 5 }, { REDUCE, 5 } } };
	private static final Integer[] INTEGERS = new Integer[ACTIONTABLE.length];

	static {
		for (int i = 0; i < INTEGERS.length; i++) {
			INTEGERS[i] = Integer.valueOf(i);
		}
	}

	/**
	 * 
	 * @param regEx
	 * @return
	 * @throws PatternException
	 */
	final SState parseRegEx(final String regEx) throws PatternException {
		final List tokenList = this.scanner.scan(regEx);
		final Object[] extdTokenList = tokenList.toArray(new Object[tokenList
				.size() + 1]);
		extdTokenList[extdTokenList.length - 1] = EF.INSTANCE;

		final Stack symbolStack = new Stack();
		final Stack stateStack = new Stack();

		int extdTokenListIndex = 0;
		Object token = extdTokenList[extdTokenListIndex];

		int stateNr = 0, tokenSymbol = -1, action = PatInt.ERROR;
		do {
			if (tokenSymbol == -1) {
				if (token instanceof SState) {
					tokenSymbol = PatInt.regExp;
				} else if (token instanceof Repeat) {
					tokenSymbol = PatInt.repetition;
				} else if (token instanceof Begin) {
					tokenSymbol = PatInt.groupBegin;
				} else if (token instanceof End) {
					tokenSymbol = PatInt.groupEnd;
				} else if (token instanceof String) {
					tokenSymbol = PatInt.label;
				} else if (token instanceof Or) {
					tokenSymbol = PatInt.orOp;
				} else if (token instanceof Regx) {
					tokenSymbol = PatInt.regExp;
				} else if (token instanceof And) {
					tokenSymbol = PatInt.andOp;
				} else if (token instanceof Not) {
					tokenSymbol = PatInt.notOp;
				} else if (token instanceof EF) {
					tokenSymbol = PatInt.EOF;
				} else {
					String message = "Unknown symbol/token: " + token;
					message += "\n(check Parser or Scanner for this symbol/token)";
					throw new RuntimeException(message);
				}
			}

			action = PatInt.ACTIONTABLE[stateNr][tokenSymbol][0];
			switch (action) {
			case PatInt.SHIFT:
				stateStack.push(PatInt.INTEGERS[stateNr]);
				symbolStack.push(token);
				stateNr = PatInt.ACTIONTABLE[stateNr][tokenSymbol][1];
				++extdTokenListIndex;
				token = extdTokenList[extdTokenListIndex];
				tokenSymbol = -1;
				break;

			case PatInt.REDUCE:
				final int ruleNr = PatInt.ACTIONTABLE[stateNr][tokenSymbol][1];

				Object node = null;
				int nodeSymbol = -1;
				switch (ruleNr) {
				case 0: // RE ::= TERM
				{
					node = symbolStack.pop();
					nodeSymbol = PatInt.RE;
					break;
				}
				case 1: // RE ::= TERM orOp RE
				{
					SetString.SState re = (SetString.SState) symbolStack.pop();
					/* Terminal_OrOp = */symbolStack.pop();
					SetString.SState term = (SetString.SState) symbolStack
							.pop();

					node = this.union(term, re);
					nodeSymbol = PatInt.RE;
					break;
				}
				case 2: // ELEMENT ::= groupBegin RE groupEnd
				{
					End end = (End) symbolStack.pop();
					node = symbolStack.pop();
					Begin begin = (Begin) symbolStack.pop();
					if (begin.nm == null && end.name != null
							|| begin.nm != null
							&& begin.nm.equals(end.name) == false) {
						throw new IllegalArgumentException(
								"endtag exspected for " + begin
										+ " but found: " + end);
					}

					nodeSymbol = PatInt.ELEMENT;
					break;
				}
				case 3: // ELEMENT ::= ELEMENT repetition
				{
					Repeat locRep = (Repeat) symbolStack.pop();
					SetString.SState element = (SetString.SState) symbolStack
							.pop();

					node = locRep.to == Repeat.UNLIMITED ? this.repeat(element,
							locRep.from, 0) : this.repeat(element, locRep.from,
							locRep.to);

					nodeSymbol = PatInt.ELEMENT;
					break;
				}

				case 4: // ELEMENT ::= ELEMENT label
				{
					// String label = (String)symbolStack.pop();
					// String labelDot = null;
					SetString.SState element = (SetString.SState) symbolStack
							.pop();

					node = element;
					nodeSymbol = PatInt.ELEMENT;
					break;
				}
				case 5: // ELEMENT ::= regExp
				{
					node = symbolStack.pop();
					if (node instanceof Regx) { // or instanceOf
												// Terminal_RuntimeValue
					// Automaton_Pattern preDefAutomaton;
					// if (this.preDefinedAutomatons==null) preDefAutomaton =
					// null;
					// else {
					// preDefAutomaton =
					// (Automaton_Pattern)this.preDefinedAutomatons.get(((Terminal_RegExp)node).name);
					// }
					// if (preDefAutomaton==null)
						throw new IllegalArgumentException(((Regx) node).name
								+ " is not defined");

						// final Automaton.State startState =
						// preDefAutomaton.getStartState();
						// if (startState==null) {
						// node = this.addState(false);
						// } else {
						// java.util.Map map = this.cloneState(startState);
						// node = (Automaton_Pattern.PState)map.get(startState);
						// }
					}
					nodeSymbol = PatInt.ELEMENT;
					break;
				}
				case 6: // TERM ::= ELEMENT
				{
					node = symbolStack.pop();
					nodeSymbol = PatInt.TERM;
					break;
				}
				case 7: // TERM ::= ELEMENT TERM
				{
					SetString.SState term = (SetString.SState) symbolStack
							.pop();
					SetString.SState element = (SetString.SState) symbolStack
							.pop();

					node = this.concat(element, term);

					nodeSymbol = PatInt.TERM;
					break;
				}
				case 8: // TERM ::= ELEMENT andOp TERM
				{
					SetString.SState term = (SetString.SState) symbolStack
							.pop();
					symbolStack.pop();
					SetString.SState element = (SetString.SState) symbolStack
							.pop();

					node = this.intersect(element, term);
					nodeSymbol = PatInt.TERM;
					break;
				}
				case 9: // TERM ::= notOp ELEMENT
				{
					SetString.SState element = (SetString.SState) symbolStack
							.pop();
					symbolStack.pop();

					node = this.complement(element);
					nodeSymbol = PatInt.TERM;
					break;
				}
				default:
					String message = "\nProgramming error in RE-Parser:"
							+ "\nACTIONTABLE contains wrong ruleNr " + ruleNr
							+ "\nor case " + ruleNr + " statement missing";
					throw new RuntimeException(message);
				} // end switch(rule)

				for (int i = stateStack.size() - symbolStack.size(); i > 1; i--) {
					stateStack.pop();
				}
				stateNr = ((Integer) stateStack.peek()).intValue();
				symbolStack.push(node);
				stateNr = PatInt.ACTIONTABLE[stateNr][nodeSymbol][1];
				break;
			}
		} while (action != PatInt.ACCEPT && action != PatInt.ERROR);

		if (action == PatInt.ERROR) {
			System.out.print("parsed:");
			for (int i = 0; i < extdTokenListIndex; ++i) {
				System.out.print(" " + extdTokenList[i]);
			}
			for (int i = extdTokenListIndex; i < extdTokenList.length - 1; ++i) {
				System.out.print(" " + extdTokenList[i]);
			}

			// for (int i=0; i<Automaton_Pattern.ACTIONTABLE[stateNr].length;
			// ++i) {
			// if
			// (Automaton_Pattern.ACTIONTABLE[stateNr][i][0]!=Automaton_Pattern.ERROR)
			// {
			// System.out.println(
			// }
			// }
			// System.out.println([stateNr][0];
			throw new Error();
		}

		// String expression = ""; int tokenPosition=-1;
		// for (int i=0; i<tokenList.size(); i++) {
		// if (i==extdTokenListIndex) tokenPosition=expression.length();
		// expression+= String.valueOf(tokenList.get(i));
		// }
		// throw new InvalidExpression(
		// expression,
		// String.valueOf( extdTokenList[extdTokenListIndex] ),
		// tokenPosition
		// );
		// }
		// return (SState)this.minimize(((SState)symbolStack.peek()));
		// return (SState)this.makeDeterministic(((SState)symbolStack.peek()));
		return (SState) symbolStack.peek();
	}

	interface TerminalFormat {

		public Object parseObject(char[] source, ParsePosition status);

		public int maxLength();
	}

	/*
	 * final class TerminalFormat_SPECIALLITERALS implements TerminalFormat {
	 * 
	 * public TerminalFormat_SPECIALLITERALS() {};
	 * 
	 * public Object parseObject(char[] source, ParsePosition status) { final
	 * int index = status.getIndex();
	 * 
	 * switch (source[index]) { case '|' : status.setIndex(index+1); return
	 * Terminal_OrOp.INSTANCE; case '(' : status.setIndex(index+1); return
	 * Terminal_GroupBegin.INSTANCE; case ')' : status.setIndex(index+1); return
	 * Terminal_GroupEnd.INSTANCE; case '*' : status.setIndex(index+1); return
	 * new Terminal_Repetition(0,Terminal_Repetition.UNLIMITED); case '+' :
	 * status.setIndex(index+1); return new
	 * Terminal_Repetition(1,Terminal_Repetition.UNLIMITED); case '?' :
	 * status.setIndex(index+1); return new Terminal_Repetition(0,1); case '.' :
	 * status.setIndex(index+1); return Det_AnyLiteral.INSTANCE; default :
	 * return null; // throw new ParseException } }
	 * 
	 * public int maxLength() {return 1;}
	 * 
	 * }
	 */
	final class TerminalFormat_LITERAL implements TerminalFormat {

		public TerminalFormat_LITERAL() {
		}

		;

		public Object parseObject(char[] source, ParsePosition status) {
			int index = status.getIndex();

			switch (source[index]) {
			case '\\': {
				++index;
				if (index == source.length) {
					return null;
				}
				status.setIndex(index + 1);

				final SetString.SState startState = PatInt.this.addState(false);
				startState.addTransition(null, new SetChar(source[index]),
						PatInt.this.addState(true));
				return startState;
			}

			case '|':
				status.setIndex(index + 1);
				return Or.INSTANCE;
			case '&':
				status.setIndex(index + 1);
				return And.INSTANCE;
			case '!':
				status.setIndex(index + 1);
				return Not.INSTANCE;
			case '(':
				status.setIndex(index + 1);
				return Begin.INSTANCE;
			case ')':
				status.setIndex(index + 1);
				return End.INSTANCE;
			case '*':
				status.setIndex(index + 1);
				return new Repeat(0, Repeat.UNLIMITED);
			case '+':
				status.setIndex(index + 1);
				return new Repeat(1, Repeat.UNLIMITED);
			case '?':
				status.setIndex(index + 1);
				return new Repeat(0, 1);
			case '.': {
				status.setIndex(index + 1);
				CharSet charSet = new SetChar();
				charSet.complement();
				final SetString.SState startState = PatInt.this.addState(false);
				startState.addTransition(null, charSet,
						PatInt.this.addState(true));
				return startState;
			}
			case '{':
			case '}':
			case '[':
			case ']':
			case '<':
			case '>':
				return null;

			default: {
				status.setIndex(index + 1);
				final SetString.SState startState = PatInt.this.addState(false);
				startState.addTransition(null, new SetChar(source[index]),
						PatInt.this.addState(true));
				return startState;
			}
			}
		}

		public int maxLength() {
			return 2;
		}
	}

	final class TerminalFormat_LITERALSET implements TerminalFormat {

		private static final int START = 0;
		private static final int FIRSTCHAR = 1;
		private static final int NORMAL = 2;
		private static final int ESCAPED = 3;

		public TerminalFormat_LITERALSET() {
			// this.automaton = automaton;
			// startState = automaton.addState(false);
			// automaton.addTransition(new
			// CharSet('.'),automaton.addState(true));
		}

		;

		public Object parseObject(char[] source, ParsePosition status) {
			int index = status.getIndex();
			final int sourceLength = source.length;

			CharSet charSet = new SetChar();
			StringBuilder chars = new StringBuilder(48);
			boolean complement = false;
			boolean intervall = false;
			int state = START;
			while (index < sourceLength) {
				char ch = source[index];
				switch (state) {
				case START:
					switch (ch) {
					case '[':
						state = FIRSTCHAR;
						break;
					default:
						return null;
					}
					break;
				case FIRSTCHAR:
					switch (ch) {
					case ']':
						return null;
					case '\\':
						state = ESCAPED;
						break;
					case '^':
						complement = true;
						state = NORMAL;
						break;
					default:
						chars.append(ch);
						state = NORMAL;
					}
					break;
				case NORMAL:
					switch (ch) {
					case '\\':
						state = ESCAPED;
						break;
					case ']': { // END
						index++;
						status.setIndex(index);

						charSet.addAll(chars.toString());
						if (complement) {
							charSet.complement();
						}

						final SetString.SState startState = PatInt.this
								.addState(false);
						startState.addTransition(null, charSet,
								PatInt.this.addState(true));
						return startState;
					}
					default:
						if (intervall) {
							char from = chars.charAt(chars.length() - 1);
							if (from > ch) {
								return null;
							}
							for (char c = ++from; c <= ch; c++) {
								charSet.add(c);
							}
							intervall = false;
						} else {
							if (ch == '-') {
								if (chars.length() == 0) {
									return null;
								}
								intervall = true;
							} else {
								chars.append(ch);
							}
						}
						// STATE = NORMAL; (not necessary because state is
						// NORMAL)
					}
					break;
				case ESCAPED:
					switch (ch) {
					default:
						if (intervall) {
							char from = (char) (((int) chars.charAt(chars
									.length() - 1)) + 1);
							for (char c = from; c <= ch; c++) {
								charSet.add(c);
							}
							intervall = false;
						} else {
							chars.append(ch);
						}
						state = NORMAL;
					}
					break;
				default:
					String message = "unknown state " + state;
					throw new RuntimeException(message);
				}

				index++;
			}

			return null;
		}

		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	private static class TerminalFormat_GroupBegin implements TerminalFormat {

		private TerminalFormat_GroupBegin() {
		}

		public Object parseObject(char[] source, ParsePosition status) {
			final int sourceLength = source.length;
			int index = status.getIndex();
			if (index >= sourceLength) {
				String message = StringUtil.STR_EMP;
				throw new ArrayIndexOutOfBoundsException(message);
			}

			if (source[index] != '<') {
				return null;
			}

			index++;
			final int startIndex = index;
			while (index < sourceLength && source[index] != '>'
					&& source[index] != '.') {
				index++;
			}
			if (index == sourceLength) {
				return null;
			}
			if (source[index] == '.') {
				return null;
			}
			status.setIndex(index + 1);
			return new Begin(new String(source, startIndex, index
					- startIndex));
		}

		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	private static class TerminalFormat_GroupEnd implements TerminalFormat {

		private TerminalFormat_GroupEnd() {
		}

		public Object parseObject(char[] source, ParsePosition status) {
			final int sourceLength = source.length;
			int index = status.getIndex();
			if (index >= sourceLength) {
				String message = StringUtil.STR_EMP;
				throw new ArrayIndexOutOfBoundsException(message);
			}

			if (source[index] != '<') {
				return null;
			}
			index++;
			if (source[index] != '/') {
				return null;
			}

			index++;
			final int startIndex = index;
			while (index < sourceLength && source[index] != '>') {
				index++;
			}
			if (index == sourceLength) {
				return null;
			}
			status.setIndex(index + 1);
			return new End(
					new String(source, startIndex, index - startIndex));
		}

		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	private static class TerminalFormat_REPETITION implements TerminalFormat {

		private final static int START = 0;
		private final static int FROM_FIRSTCHAR = 1;
		private final static int FROM_NORMAL = 2;
		private final static int TO_FIRSTCHAR = 3;
		private final static int TO_NORMAL = 4;

		private TerminalFormat_REPETITION() {
		}

		;

		public Object parseObject(char[] source, ParsePosition status) {
			int index = status.getIndex();
			final int sourceLength = source.length;
			StringBuilder chars = new StringBuilder(32);
			int from = 0;
			int state = START;
			while (index < sourceLength) {
				char ch = source[index];
				switch (state) {
				case START:
					switch (ch) {
					case '{':
						state = FROM_FIRSTCHAR;
						break;
					default:
						return null;
					}
					break;
				case FROM_FIRSTCHAR:
					switch (ch) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						chars.append(ch);
						state = FROM_NORMAL;
						break;
					default:
						return null;
					}
					break;
				case FROM_NORMAL:
					switch (ch) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						chars.append(ch);
						// state = NORMAL; // not necessary because state is
						// NORMAL
						break;
					case ',':
						from = Integer.parseInt(chars.toString());
						chars.setLength(0);
						state = TO_FIRSTCHAR;
						break;
					case '}': // END
						index++;
						status.setIndex(index);
						final int count = Integer.parseInt(chars.toString());
						return new Repeat(count, count);
					default:
						return null;
					}
					break;
				case TO_FIRSTCHAR:
					switch (ch) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						chars.append(ch);
						state = TO_NORMAL;
						break;
					case '*': // may be END
						index++;
						if (index == sourceLength) {
							return null;
						}
						if (source[index] != '}') {
							return null;
						}
						index++;
						status.setIndex(index);
						return new Repeat(from, Repeat.UNLIMITED);
					default:
						return null;
					}
					break;
				case TO_NORMAL:
					switch (ch) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						chars.append(ch);
						state = TO_NORMAL;
						break;
					case '}': // END
						index++;
						status.setIndex(index);
						final int to = Integer.parseInt(chars.toString());
						return new Repeat(from, to);
					default:
						return null;
					}
					break;
				}

				index++;
			}
			return null;
		}

		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	private static class TerminalFormat_LABEL implements TerminalFormat {

		private TerminalFormat_LABEL() {
		}

		/**
		 * 
		 * @param source
		 * @param status
		 * @return
		 */
		public final Object parseObject(final char[] source,
				final ParsePosition status) {
			int startIndex = status.getIndex();
			int index = startIndex;
			if (source[index++] != '{') {
				return null;
			}
			if (source[index++] != '=') {
				return null;
			}

			while (index < source.length
					&& ('A' <= source[index] && source[index] <= 'Z'
							|| 'a' <= source[index] && source[index] <= 'z' || '0' <= source[index]
							&& source[index] <= '9')) {
				++index;
			}

			if (index == source.length) {
				return null;
			}
			if (source[index] != '}') {
				return null;
			}

			status.setIndex(index + 1);
			return new String(source, startIndex + 2, index - startIndex - 2);
		}

		/**
		 * 
		 * @return Scan.UNLIMITED_MAX_LENGTH
		 */
		@Override
		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	private static final class TerminalFormat_RegExp implements TerminalFormat {

		private TerminalFormat_RegExp() {
		}

		/**
		 * 
		 * @param source
		 * @param status
		 * @return
		 */
		public final Object parseObject(final char[] source,
				final ParsePosition status) {
			int startIndex = status.getIndex();
			int index = startIndex;
			if (source[index++] != '{') {
				return null;
			}

			if (('A' <= source[index] && source[index] <= 'Z' || 'a' <= source[index]
					&& source[index] <= 'z') == false) {
				return null;
			}
			++index;
			while (index < source.length
					&& ('A' <= source[index] && source[index] <= 'Z'
							|| 'a' <= source[index] && source[index] <= 'z'
							|| '0' <= source[index] && source[index] <= '9'
							|| source[index] == '_' || source[index] == '/' || source[index] == '-')) {
				++index;
			}

			if (index == source.length) {
				return null;
			}
			if (source[index] != '}') {
				return null;
			}

			status.setIndex(index + 1);
			return new Regx(new String(source, startIndex + 1, index
					- startIndex - 1));
		}

		/**
		 * 
		 * @return Scan.UNLIMITED_MAX_LENGTH
		 */
		public int maxLength() {
			return Scan.UNLIMITED_MAX_LENGTH;
		}
	}

	/**
	 * 
	 * @return newScanner
	 */
	final Scan newScanner() {
		return new Scan(new TerminalFormat[] { new TerminalFormat_LITERALSET(),
				new TerminalFormat_REPETITION(), new TerminalFormat_LABEL(),
				new TerminalFormat_GroupBegin(), new TerminalFormat_GroupEnd(),
				new TerminalFormat_LITERAL(), new TerminalFormat_RegExp() },
				true);
	}
}
