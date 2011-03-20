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

import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ReceiptHandler implements IReceiptHandler {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ReceiptHandler.class);
	OCRExtractor ocrExtractor;
	IBrandNameGuesser brandNameGuesser;

	/**
	 * @param bng the brandNameGuesser to set
	 */
	@Inject
	public void setBrandNameGuesser(final IBrandNameGuesser bng) {
		brandNameGuesser = bng;
	}

	/**
	 * @param oe the ocerExtractor to set
	 */
	@Inject
	public void setOcrExtractor(final OCRExtractor oe) {
		ocrExtractor = oe;
	}

	/**
	 * @return 
	 * @throws Exception 
	 * @see com.recipitor.textextractor.IReceiptHandler#handle(com.xerox.amazonws.sqs2.Message)
	 */
	@Override
	public List<GuessResult> handle(final Body b) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("handling receipt [" + b.getReceipt().getId() + "]");
		final ExtractedTokens et = ocrExtractor.extract(b);
		final List<GuessResult> $ = brandNameGuesser.guess(et);
		for (final GuessResult gr : $)
			if (LGR.isDebugEnabled())
				LGR.debug("guess result is [" + gr.name + "] at distance [" + gr.distance + "]");
		return $;
	}
}
