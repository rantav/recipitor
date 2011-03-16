/**
 * NAME: ReceiptHandlerTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class ReceiptHandlerTest extends TestCase {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ReceiptHandlerTest.class);

	@Test
	public void test() throws Exception {
		final ReceiptHandler $ = new ReceiptHandler();
		$.setProcessExecutor(new ProcessExecutor());
		$.handle(null);
	}
}
