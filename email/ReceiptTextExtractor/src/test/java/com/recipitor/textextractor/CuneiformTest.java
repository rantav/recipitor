/**
 * NAME: CuneiformTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 20, 2011
 * Associated Bugs: 
 */
public class CuneiformTest {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(CuneiformTest.class);
	private static Cuneiform _;

	@BeforeClass
	public static void init() {
		_ = new Cuneiform();
	}

	@Test
	public void testBehaviour() throws Exception {
		final Body b = new ObjectMapper().readValue("{\"receipt\":{\"url\":\"my_url\",\"id\":\"999\"}}", Body.class);
		final ProcessExecutor pe = Mockito.mock(ProcessExecutor.class);
		_.setProcessExecutor(pe);
		_.extract(b);
		Mockito.verify(pe).runAndGetResltsAsString(Cuneiform.CUNEIFORM_SCRIPT_NAME, "999", "my_url", "jpg");
	}

	@Test
	public void checkTokens() throws Exception {
		final Body b = new ObjectMapper().readValue("{\"receipt\":{\"url\":\"my_url\",\"id\":\"999\"}}", Body.class);
		final ProcessExecutor pe = Mockito.mock(ProcessExecutor.class);
		Mockito.when(pe.runAndGetResltsAsString(Cuneiform.CUNEIFORM_SCRIPT_NAME, "999", "my_url", "jpg")).thenReturn(
				"token1\n\token2");
		_.setProcessExecutor(pe);
		final ExtractedTokens $ = _.extract(b);
		Assert.assertEquals(1, $.getTokens().size());
		Assert.assertEquals("token1\n\token2", $.getTokens().get(0));
	}
}
