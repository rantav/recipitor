/**
 * NAME: FuzzyMatcher.java
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
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class AGrepMatcher implements IFuzzyMatcher {

	private static final int MAX_NUM_OF_ERRORS = 3;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(AGrepMatcher.class);
	ProcessExecutor processExecutor;

	/**
	 * @param pe the processExecutor to set
	 */
	@Inject
	public void setProcessExecutor(final ProcessExecutor pe) {
		processExecutor = pe;
	}

	@Override
	public Double isExist(final String term, final String tokens) throws Exception {
		double $ = Double.MAX_VALUE;
		for (int ne = 0; ne < MAX_NUM_OF_ERRORS; ne++) {
			final String r = processExecutor.runAndGetResltsAsString("scripts/goAgrep.sh", term, String.valueOf(ne),
					tokens);
			if (r != null) {
				final int n = Integer.parseInt(r.trim());
				if (n > 0) {
					$ = (double) ne / term.trim().length();
					break;
				}
			}
		}
		return $;
	}
}
