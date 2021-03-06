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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.recipitor.textextractor.data.request.Body;
import com.recipitor.textextractor.data.response.Receipt;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class ReceiptHandlerTest {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(ReceiptHandlerTest.class);

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
	public void testTradeJoe() throws Exception {
		doTest("Trader Joe's", "123", "/tj_1_samll_size.jpg");
	}

	@Test
	public void testWalMart() throws Exception {
		doTest("Walmart", "998", "/rec02.jpg");
	}

	//	@Test
	public void testLargeImage() throws Exception {
		doTest("Trader Joe's", "456", "/tj.jpg");
	}

	//	@Test
	public void testYogurt() throws Exception {
		doTest("Fraiche Yogurt", "abc", "/fraiche_yogurt.jpg");
	}

	@Test
	public void testJoann() throws Exception {
		doTest("Joann", "123", "/joann-img.png.resize.jpg");
	}

	/**
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws Exception
	 */
	private void doTest(final String name, final String id, final String fn) throws IOException, JsonParseException,
			JsonMappingException, Exception {
		final ReceiptHandler _ = init();
		Commons.copyStreamIntoFile(Commons.loadInputStreamFromSourceName(fn), "/tmp/img-test" + id);
		final Body b = new ObjectMapper().readValue("{\"receipt\":{\"id\":\"" + id
				+ "\",\"url\":\"file:///tmp/img-test" + id + "\"}}", Body.class);
		final Receipt $ = _.handle(b);
		Assert.assertEquals(1, $.getExtracted_store_names().size());
		Assert.assertEquals(name, $.getExtracted_store_names().get(0).name);
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

			@SuppressWarnings("unused")
			@Provides
			BrandData provideBrandNamesConfig() throws Exception {
				final BrandData $ = new ObjectMapper().readValue(Commons
						.loadInputStreamFromSourceName("/com/recipitor/textextractor/conf/NotableGroceryStores.json"),
						BrandData.class);
				return $;
			}
		});
		final ReceiptHandler _ = injector.getInstance(ReceiptHandler.class);
		return _;
	}

	@Test
	public void testBheaviour() throws Exception {
		final Body b = new ObjectMapper().readValue("{\"receipt\":{\"url\":\"my_url\",\"id\":\"999\"}}", Body.class);
		final ReceiptHandler _ = new ReceiptHandler();
		final OCRExtractor ocre = Mockito.mock(OCRExtractor.class);
		final ExtractedTokens et = new ExtractedTokens();
		Mockito.when(ocre.extract(b)).thenReturn(et);
		et.addTokens("abc  \nxyz  \n   123   ");
		final IBrandNameGuesser bng = Mockito.mock(IBrandNameGuesser.class);
		final List<GuessResult> l = new LinkedList<GuessResult>();
		final Receipt r = new Receipt();
		Mockito.when(bng.guess(et)).thenReturn(l);
		_.setBrandNameGuesser(bng);
		_.setOcrExtractor(ocre);
		final Receipt $ = _.handle(b);
		final String[] expectedTokens = new String[] { "abc", "xyz", "123" };
		Assert.assertEquals(expectedTokens.length, $.getExtracted_tokens_list().length);
		for (int i = 0; i < expectedTokens.length; i++)
			Assert.assertEquals(expectedTokens[i], $.getExtracted_tokens_list()[i]);
		Mockito.verify(ocre).extract(b);
		Mockito.verify(bng).guess(et);
	}
}
