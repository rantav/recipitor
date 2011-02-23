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

	//	@Test
	public void extractSenderNotValidMail() {
		final MailExtractor _ = new MailExtractor();
		Assert.assertEquals(null, _.extractSender("yonatanm@gmail"));
	}

	@Test
	public void testBase64() throws Exception {
		//		final Base64DecoderStream base64DecoderStream = new Base64DecoderStream(new FileInputStream("/tmp/a.64.txt"));
		//		final byte[] data = MailExtractor.toByteArray(base64DecoderStream);
		//		System.out.println("data length is " + data.length);
		//		final byte[] arr = data;
		//		final FileOutputStream fos = new FileOutputStream("/tmp/b");
		//		fos.write(arr);
		//		fos.flush();
		//		fos.close();
	}
}
