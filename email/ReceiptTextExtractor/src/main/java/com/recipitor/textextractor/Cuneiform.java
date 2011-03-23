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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Cuneiform extends OCRExtractor {

	public static final String CUNEIFORM_SCRIPT_NAME = "scripts/goCuneiform.sh";
	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(Cuneiform.class);

	@Override
	public ExtractedTokens extract(final Body b) throws Exception {
		LGR.debug("extracting text using cuneiform");
		final String tkns = processExecutor.runAndGetResltsAsString(CUNEIFORM_SCRIPT_NAME, b.getReceipt().getId(), b
				.getReceipt().getUrl());
		final ExtractedTokens $ = new ExtractedTokens();
		$.addTokens(tkns);
		LGR.debug("tokens [" + tkns + "]");
		return $;
	}
}
