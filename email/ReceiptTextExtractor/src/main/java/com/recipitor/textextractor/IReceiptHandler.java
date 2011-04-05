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

import com.recipitor.textextractor.data.request.Body;
import com.recipitor.textextractor.data.response.Receipt;

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
	Receipt handle(Body b) throws Exception;
}
