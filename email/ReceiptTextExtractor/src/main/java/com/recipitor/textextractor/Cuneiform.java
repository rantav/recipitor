/**
 * NAME: Cuneiform.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Cuneiform extends OCRExtractor {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Cuneiform.class);

	@Override
	public ExtractedTokens extract(final Body b) throws Exception {
		final String tkns = processExecutor.runAndGetResltsAsString("scripts/go.sh", b.getReceipt().getId(), b
				.getReceipt().getUrl()
		//						"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg"
		//				"http://oi44.tinypic.com/f087y9.jpg");
				);
		final ExtractedTokens $ = new ExtractedTokens();
		$.addTokens(tkns);
		return $;
	}
}
