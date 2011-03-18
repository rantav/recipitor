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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.recipitor.textextractor.data.request.Body;

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
		final Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IReceiptHandler.class).to(ReceiptHandler.class);
				bind(OCRExtractor.class).to(Cuneiform.class);
				bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
				bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
			}
		});
		final ReceiptHandler $ = injector.getInstance(ReceiptHandler.class);
		final ObjectMapper om = new ObjectMapper();
		final Body b = om
				.readValue(
						"{\"receipt\":{\"id\":\"987\",\"url\":\"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg\"}}",
						Body.class);
		$.handle(b);
	}
}
