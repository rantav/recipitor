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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ReceiptHandler implements IReceiptHandler {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(ReceiptHandler.class);
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
		LGR.debug("handling receipt [{}]", b.getReceipt().getId());
		final ExtractedTokens et = ocrExtractor.extract(b);
		final List<GuessResult> $ = brandNameGuesser.guess(et);
		for (final GuessResult gr : $)
			LGR.debug("looking for [{}] found [{}] ", gr.serachTerm, gr.foundTerm.get(0));
		return $;
	}
}
