/**
 * NAME: ExtractedTokensTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.LinkedList;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author ymaman
 * created: Mar 20, 2011
 * Associated Bugs: 
 */
public class ExtractedTokensTest {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ExtractedTokensTest.class);

	@Test
	public void emptyObjecthasNoTokens() {
		final ExtractedTokens _ = new ExtractedTokens();
		Assert.assertTrue(_.getTokens().isEmpty());
	}

	@Test
	public void addingTokenToEmpty() {
		final ExtractedTokens _ = new ExtractedTokens();
		_.addTokens("t1");
		Assert.assertEquals(1, _.getTokens().size());
		Assert.assertEquals("t1", _.getTokens().get(0));
	}

	@SuppressWarnings("serial")
	@Test
	public void settingTokens() {
		final ExtractedTokens _ = new ExtractedTokens();
		_.setTokens(new LinkedList<String>() {

			{
				add("t1");
				add("t2");
			}
		});
		Assert.assertEquals(2, _.getTokens().size());
		Assert.assertEquals("t1", _.getTokens().get(0));
		Assert.assertEquals("t2", _.getTokens().get(1));
	}
}
