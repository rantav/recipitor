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

import java.util.List;

import org.apache.log4j.Logger;

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
		final List<String> tkns = processExecutor.runAndGetResltsAsList("scripts/go.sh", b.getReceipt().id, b.getUrl()
		//						"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg"
		//				"http://oi44.tinypic.com/f087y9.jpg");
				);
		final ExtractedTokens $ = new ExtractedTokens();
		$.setTokens(tkns);
		return $;
	}
}
