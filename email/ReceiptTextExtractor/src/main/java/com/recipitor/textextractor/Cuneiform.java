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
	public static String EXTRACTED_TEST_PREFIX = "Cuneiform for Linux 0.7.0";
	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(Cuneiform.class);

	@Override
	public ExtractedTokens extract(final Body b) throws Exception {
		LGR.debug("extracting text using cuneiform");
		final String tkns = removePrefix(processExecutor.runAndGetResltsAsString(CUNEIFORM_SCRIPT_NAME, b.getReceipt()
				.getId(), b.getReceipt().getUrl()));
		final ExtractedTokens $ = new ExtractedTokens();
		$.addTokens(tkns);
		LGR.debug("tokens [" + tkns + "]");
		return $;
	}

	/**
	 * @param s
	 * @return
	 */
	private String removePrefix(final String s) {
		String $ = s;
		if (s == null) return $;
		final int pl = EXTRACTED_TEST_PREFIX.length();
		if (s.length() < pl) return $;
		if (s.startsWith(EXTRACTED_TEST_PREFIX)) $ = s.substring(pl);
		return $;
	}
}
