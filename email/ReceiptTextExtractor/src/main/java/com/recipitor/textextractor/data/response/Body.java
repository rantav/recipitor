/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor.data.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Body {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(Body.class);
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
		receipt = r;
	}
}
