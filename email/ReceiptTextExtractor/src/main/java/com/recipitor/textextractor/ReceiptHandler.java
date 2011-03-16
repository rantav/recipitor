/**
 * NAME: ReceiptHandler.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ReceiptHandler implements IReceiptHandler {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ReceiptHandler.class);
	OCRExtractor ocrExtractor;

	/**
	 * @param oe the ocerExtractor to set
	 */
	@Inject
	public void setOcrExtractor(final OCRExtractor oe) {
		ocrExtractor = oe;
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.textextractor.IReceiptHandler#handle(com.xerox.amazonws.sqs2.Message)
	 */
	@Override
	public void handle(final Body b) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("handling \n" + b);
		final ExtractedTokens et = ocrExtractor.extract(b);
		final StringBuilder sb = new StringBuilder();
		for (final String l : et.tokens)
			sb.append(l + "\n");
		if (LGR.isDebugEnabled()) LGR.debug("tokens\n" + sb);
	}
}
