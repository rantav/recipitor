/**
 * NAME: IReceiptHandler.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;


/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public interface IReceiptHandler {

	/**
	 * @param b
	 * @throws Exception 
	 */
	void handle(Body b) throws Exception;
}
