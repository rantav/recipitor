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
import org.codehaus.jackson.map.ObjectMapper;
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
		final OCRExtractor oe = new Cuneiform();
		oe.setProcessExecutor(new ProcessExecutor());
		final ObjectMapper om = new ObjectMapper();
		final Body b = om
				.readValue(
						"{\"url\":\"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg\", \"receipt\":{\"id\":\"987\"}}",
						Body.class);
		$.setOcrExtractor(oe);
		$.handle(b);
	}
}
