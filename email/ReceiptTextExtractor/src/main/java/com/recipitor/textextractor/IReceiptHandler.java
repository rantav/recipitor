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

import java.util.List;

import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public interface IReceiptHandler {

	/**
	 * @param b
	 * @return 
	 * @throws Exception 
	 */
	List<GuessResult> handle(Body b) throws Exception;
}
