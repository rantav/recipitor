/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor.data.respons;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Body {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Body.class);
	Receipt receipt;

	public Body() {
		// must have an empty constructor
	}

	/**
	 * @return the receipt
	 */
	public Receipt getReceipt() {
		return receipt;
	}

	/**
	 * @param r the receipt to set
	 */
	public void setReceipt(final Receipt r) {
		this.receipt = r;
	}
}
