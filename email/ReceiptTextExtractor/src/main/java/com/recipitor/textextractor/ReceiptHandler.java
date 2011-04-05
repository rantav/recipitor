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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.recipitor.textextractor.data.request.Body;
import com.recipitor.textextractor.data.response.Receipt;

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
	public Receipt handle(final Body b) throws Exception {
		final Receipt $ = new Receipt();
		LGR.debug("handling receipt [{}]", b.getReceipt().getId());
		final ExtractedTokens et = ocrExtractor.extract(b);
		final List<GuessResult> grs = brandNameGuesser.guess(et);
		for (final GuessResult gr : grs)
			LGR.debug("looking for [{}] found [{}] ", gr.serachTerm, gr.foundTerm.get(0));
		$.setExtracted_store_names(grs);
		$.setId(b.getReceipt().getId());
		if (!et.getTokens().isEmpty()) {
			final String[] kns = et.getTokens().get(0).split("\\W");
			final ArrayList<String> cleanTokens = new ArrayList<String>();
			for (String t : kns) {
				t = t.trim();
				if (t.isEmpty()) continue;
				cleanTokens.add(t);
			}
			final String[] ar = new String[cleanTokens.size()];
			cleanTokens.toArray(ar);
			$.setExtracted_tokens_list(ar);
		}
		return $;
	}
}
