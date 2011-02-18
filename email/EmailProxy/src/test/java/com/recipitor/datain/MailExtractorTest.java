/**
 * NAME: MailExtractorTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author ymaman
 * created: Feb 18, 2011
 * Associated Bugs: 
 */
public class MailExtractorTest {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailExtractorTest.class);

	@Test
	public void extractSenderSimple() {
		final MailExtractor _ = new MailExtractor();
		Assert.assertEquals("yonatanm@gmail.com", _.extractSender("yonatanm@gmail.com"));
	}

	@Test
	public void extractSenderLongFormat() {
		final MailExtractor _ = new MailExtractor();
		Assert.assertEquals("yonatanm@gmail.com", _.extractSender("Yonatan Maman <yonatanm@gmail.com>"));
	}

	@Test
	public void extractSenderNotValidMail() {
		final MailExtractor _ = new MailExtractor();
		Assert.assertEquals(null, _.extractSender("yonatanm@gmail"));
	}
}
