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

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class ReceiptHandlerTest {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ReceiptHandlerTest.class);

	//	@Test
	public void testAll() throws Exception {
		final ReceiptHandler _ = init();
		final Body b = new ObjectMapper()
				.readValue(
						"{\"receipt\":{\"id\":\"987\",\"url\":\"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg\"}}",
						Body.class);
		_.handle(b);
	}

	@Test
	public void testAll2() throws Exception {
		final ReceiptHandler _ = init();
		final Body b = new ObjectMapper().readValue(
				"{\"receipt\":{\"id\":\"123\",\"url\":\"file:///home/ymaman/my.png\"}}", Body.class);
		_.handle(b);
	}

	/**
	 * @return
	 */
	private ReceiptHandler init() {
		final Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IReceiptHandler.class).to(ReceiptHandler.class);
				bind(OCRExtractor.class).to(Cuneiform.class);
				bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
				bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
			}
		});
		final ReceiptHandler _ = injector.getInstance(ReceiptHandler.class);
		return _;
	}

	//	@Test
	public void testBheaviour() throws Exception {
		final Body b = new ObjectMapper().readValue("{\"receipt\":{\"url\":\"my_url\",\"id\":\"999\"}}", Body.class);
		final ReceiptHandler _ = new ReceiptHandler();
		final OCRExtractor ocre = Mockito.mock(OCRExtractor.class);
		final ExtractedTokens et = new ExtractedTokens();
		Mockito.when(ocre.extract(b)).thenReturn(et);
		et.addTokens("abc\nxyz\n123");
		final IBrandNameGuesser bng = Mockito.mock(IBrandNameGuesser.class);
		final List<GuessResult> l = new LinkedList<GuessResult>();
		Mockito.when(bng.guess(et)).thenReturn(l);
		_.setBrandNameGuesser(bng);
		_.setOcrExtractor(ocre);
		Assert.assertEquals(l, _.handle(b));
		Mockito.verify(ocre).extract(b);
		Mockito.verify(bng).guess(et);
	}
}
