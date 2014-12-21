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
 * @version 1.0.0
 */

final class Data implements Cloneable {

	/**
	 * 
	 * inner class <FAData.Stt>
	 */
	static final class Stt implements Cloneable {

		/**
		 * 
		 * inner class <FAData.Stt.Transition
		 * 
		 */
		static final class Tran implements Cloneable {

			final transient Prop properties;
			final transient String charSet;
			final transient int toSttNumber;

			/**
			 * 
			 * @param properties
			 * @param charSet
			 * @param toSttNumber
			 */
			Tran(final Prop properties, final String charSet,
					final int toSttNumber) {
				this.properties = properties;
				this.charSet = charSet;
				this.toSttNumber = toSttNumber;
			}

			/**
			 * 
			 * @param charSet
			 * @param toSttNumber
			 */
			Tran(final String charSet, final int toSttNumber) {
				this(null, charSet, toSttNumber);
			}

			@Override
			public final String toString() {
				// TODO Auto-generated method stub
				return super.toString();
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
			public final Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return super.clone();
			}
		}

		final transient int number;
		final transient boolean isFinal;
		final transient Tran[] transitions;
		final transient Boolean transitionsAreDeterministic;

		/**
		 * 
		 * @param number
		 * @param isFinal
		 * @param transitions
		 * @param transitionsAreDeterministic
		 */
		Stt(final int number, final boolean isFinal,
				final Data.Stt.Tran[] transitions,
				final boolean transitionsAreDeterministic) {
			this.number = number;
			this.isFinal = isFinal;
			this.transitions = transitions;
			this.transitionsAreDeterministic = Boolean
					.valueOf(transitionsAreDeterministic);
		}

		Stt(final int number, final boolean isFinal,
				final Data.Stt.Tran[] transitions) {
			this.number = number;
			this.isFinal = isFinal;
			this.transitions = transitions;
			this.transitionsAreDeterministic = null;
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
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
	}

	transient final Stt[] stts;
	transient final Integer startSttNumber;
	transient final Boolean isDeterministic;

	Data(final Data.Stt[] Stts, final Integer startSttNumber,
			final boolean isDeterministic) {
		this.stts = Stts;
		this.startSttNumber = startSttNumber;
		this.isDeterministic = Boolean.valueOf(isDeterministic);
	}

	Data(final Data.Stt[] Stts, final Integer startSttNumber) {
		this.stts = Stts;
		this.startSttNumber = startSttNumber;
		this.isDeterministic = null;
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

	@Override
	public final Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
