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
 * @author Muhammad Edwin
 * @version 1.0.0
 */

interface SttPro {

	interface Tran {

		SttPro from();

		CharSet charSet();

		SttPro to();
	}

	interface ViewListener {

		/**
		 * 
		 * @param state
		 */
		void look(final SttPro state);

		/**
		 * 
		 * @param state
		 * @param ch
		 */
		void look(final SttPro state, final char ch);

		/**
		 * 
		 * @param state
		 */
		void unvisit(final SttPro state);
	}

	interface ChangeListener {

		/**
		 * 
		 * @param transition
		 */
		void add(final Tran transition);

		/**
		 * 
		 * @param transition
		 */
		void remove(final Tran transition);

		/**
		 * 
		 * @param state
		 * @param isFinal
		 */
		void change(final SttPro state, final boolean isFinal);
	}

	/**
	 * 
	 * @param listener
	 */
	void addViewListener(final ViewListener listener);

	/**
	 * 
	 * @param listener
	 * @return
	 */
	boolean removeViewListener(final ViewListener listener);

	/**
	 * 
	 * @param listener
	 */
	void addChangeListener(final ChangeListener listener);

	/**
	 * 
	 * @param listener
	 * @return
	 */
	boolean removeChangeListener(final ChangeListener listener);

	/**
	 * 
	 * @return is final  state
	 */
	boolean isFinal();

	/**
	 * 
	 * @param isFinal
	 */
	void setFinal(boolean isFinal);

	/**
	 * 
	 * @param charSet
	 * @param toState
	 * @return
	 */
	Tran addTran(final CharSet charSet, final SttPro toState);

	/**
	 * 
	 * @param transition
	 * @return
	 */
	boolean removeTran(final Tran transition);

	void removeAll();

	Tran[] getTrans();

	Tran[] getETrans();

	Tran[] getAllTrans();

	SetState reachState();

	States look();

	int stateNum();
}
