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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Wrapps the agrep http://www.tgries.de/agrep/ utility using process Executor
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class AGrepMatcher implements IFuzzyMatcher {

	public static final String AGREP_SCRIPT_WRAPPER = "scripts/goAgrep.sh";
	private static final int MAX_NUM_OF_ERRORS = 3;
	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(AGrepMatcher.class);
	ProcessExecutor processExecutor;

	/**
	 * @param pe the processExecutor to set
	 */
	@Inject
	public void setProcessExecutor(final ProcessExecutor pe) {
		processExecutor = pe;
	}

	@Override
	public GuessResult isExist(final String term, final String tokens) throws Exception {
		GuessResult $ = null;
		for (int ne = 0; ne < MAX_NUM_OF_ERRORS; ne++) {
			final String r = processExecutor.runAndGetResltsAsString(AGREP_SCRIPT_WRAPPER, term, String.valueOf(ne),
					tokens);
			if (r == null || r.isEmpty()) continue;
			final String[] lines = r.split("\n");
			if (lines.length == 0) continue;
			LGR.debug("found term [{}] with [{}] erros", term, ne);
			$ = new GuessResult();
			$.termLength = term.trim().length();
			$.numOfErrors = ne;
			$.distance = (double) $.numOfErrors / $.termLength;
			$.serachTerm = term;
			for (final String l : lines)
				$.foundTerm.add(l.trim());
			break;
		}
		return $;
	}
}
