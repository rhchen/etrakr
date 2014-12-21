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

final class PAuto extends SAuto {
	private static final HashMap AUTOMATON_MAP = new HashMap(25);

	/**
	 * 
	 * @param automaton
	 */
	PAuto(final PatInt automaton) {
		super(automaton);
	}

	PAuto() {
		super(new PatInt());
	}

	/**
	 * 
	 * @param regEx
	 */
	PAuto(final String regEx) {
		super(new PatInt(regEx));
	}

	/**
	 * 
	 * @param data
	 */
	PAuto(final Data data) {
		super(new PatInt());
		this.init(data);
	}

	/**
	 * 
	 * @return automaton
	 */
	final PatInt getAuto() {
		return this.automaton;
	}

	/**
	 * 
	 * @param regEx
	 */
	final void addAll(final String regEx) {
		this.automaton.addAll(regEx);
	}

	/**
	 * 
	 * @param regEx
	 */
	final void retainAll(final String regEx) {
		this.automaton.retainAll(regEx);
	}

	/**
	 * 
	 * @param regEx
	 */
	final void removeAll(final String regEx) {
		this.automaton.removeAll(regEx);
	}

	/**
	 * 
	 * @return automation.regEx
	 */
	final String getRegEx() {
		return this.automaton.regEx;
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
		// TODO Auto-generated method stub
		return super.toString();
	}
}