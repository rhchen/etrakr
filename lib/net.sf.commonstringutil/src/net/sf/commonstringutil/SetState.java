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

final class SetState implements Cloneable {

	final class Iterator implements Cloneable {
		final transient int offset;
		transient Wrapper_State current = null;

		/**
		 * Defatul constructor 
		 * 
		 */
		Iterator() {
			this.offset = 0;			
		}

		/**
		 * 
		 * @param offset
		 */
		Iterator(final int offset) {
			this.offset = offset;
		}

		/**
		 * 
		 * @return current.next
		 * @see SetState#elements
		 */
		SttPro next() {
			if (this.current == null) {
				this.current = SetState.this.elements;
				try {
					for (int i = offset; i > 0; --i)
						this.current = this.current.next;
				} catch (NullPointerException e) {
					if (this.current != null)
						throw e; // else null is returned
				}
			} else
				this.current = this.current.next;
			return (this.current == null) ? null : this.current.state;
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
	}

	static final class Wrapper_State implements Cloneable {
		transient final SttPro state;
		transient Wrapper_State next = null;

		/**
		 * 
		 * @param state
		 */
		Wrapper_State(final SttPro state) {
			this.state = state;
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
	}

	transient Wrapper_State elements = null;
	transient Wrapper_State lastElement = null;
	transient int size = 0;

	SetState() {
	}

	/**
	 * 
	 * @param state
	 */
	SetState(final SttPro state) {
		this.add(state);
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	final boolean add(SttPro state) {
		if (this.contains(state))
			return false;

		if (this.lastElement == null) {
			this.elements = new Wrapper_State(state);
			this.lastElement = this.elements;
		} else {
			this.lastElement.next = new Wrapper_State(state);
			this.lastElement = this.lastElement.next;
		}
		++this.size;
		return true;
	}

	/**
	 * 
	 * @param stateSet
	 * @return
	 */
	final int addAll(final SetState stateSet) {
		int result = 0;
		for (Wrapper_State wrapper = stateSet.elements; wrapper != null; wrapper = wrapper.next) {
			if (this.add(wrapper.state))
				++result;
		}
		return result;
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	final boolean remove(final SttPro state) {
		if (this.contains(state) == false)
			return false;
		Wrapper_State prev = null;
		for (Wrapper_State wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
			if (wrapper.state == state) {
				if (prev == null)
					this.elements = wrapper.next;
				else
					prev.next = wrapper.next;

				if (wrapper == this.lastElement)
					this.lastElement = prev;
				--this.size;
				return true;
			}
			prev = wrapper;
		}
		return false;
	}

	/**
	 * 
	 * @param stateSet
	 * @return
	 */
	final int removeAll(final SetState stateSet) {
		int answer = 0;
		Wrapper_State prev = null;
		for (Wrapper_State wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
			if (stateSet.contains(wrapper.state)) {
				if (prev == null)
					this.elements = wrapper.next;
				else
					prev.next = wrapper.next;

				if (wrapper == this.lastElement)
					this.lastElement = prev;
				--this.size;
				if (++answer == stateSet.size())
					return answer;
			}
			prev = wrapper;
		}
		return answer;
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	final boolean contains(SttPro state) {
		for (Wrapper_State wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
			if (wrapper.state == state)
				return true;
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
	 * size is zero
	 * @return
	 */
	final boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * 
	 * @return {@link SetState#iterator()}
	 */
	final SetState.Iterator iterator() {
		return new Iterator();
	}

	/**
	 * 
	 * @param offset
	 * @return
	 */
	final SetState.Iterator iterator(final int offset) {
		return new Iterator(offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != SetState.class)
			throw new ClassCastException(StringUtil.STR_EMP);

		final SetState set = (SetState) obj;
		if (this.size != set.size)
			return false;
		for (Wrapper_State wrapper = set.elements; wrapper != null; wrapper = wrapper.next) {
			if (this.contains(wrapper.state) == false)
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		long hash = 0;
		for (Wrapper_State wrapper = this.elements; wrapper != null; wrapper = wrapper.next) {
			hash = ((hash << 32) + wrapper.state.hashCode()) % 4294967291L;
		}
		return (int) hash;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}