/**
 * NAME: OCRExtractor.java
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
import com.recipitor.textextractor.data.request.Body;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public abstract class OCRExtractor {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(OCRExtractor.class);
	ProcessExecutor processExecutor;

	/**
	 * @param pe the processExecutor to set
	 */
	@Inject
	public void setProcessExecutor(final ProcessExecutor pe) {
		processExecutor = pe;
	}

	public abstract ExtractedTokens extract(final Body b) throws Exception;
}
