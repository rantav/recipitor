/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Body {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Body.class);
	String url;
	Receipt receipt;

	public Body() {
		// must have an empty constructor
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * @return the receipt
	 */
	public Receipt getReceipt() {
		return receipt;
	}

	/**
	 * @param receipt the receipt to set
	 */
	public void setReceipt(final Receipt receipt) {
		this.receipt = receipt;
	}
}
