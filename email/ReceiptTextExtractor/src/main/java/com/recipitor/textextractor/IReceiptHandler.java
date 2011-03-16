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

import com.xerox.amazonws.sqs2.Message;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public interface IReceiptHandler {

	/**
	 * @param msg
	 * @throws Exception 
	 */
	void handle(Message msg) throws Exception;
}
