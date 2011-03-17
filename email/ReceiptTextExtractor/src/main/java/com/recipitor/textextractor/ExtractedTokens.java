/**
 * NAME: ExtractedTokens.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class ExtractedTokens {

	/**
	 * @param t the tokens to set
	 */
	public void setTokens(final List<String> t) {
		tokens = t;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	public void addTokens(final String t) {
		tokens.add(t);
	}

	public void addTokens(final List<String> t) {
		tokens.addAll(t);
	}

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ExtractedTokens.class);
	List<String> tokens = new LinkedList<String>();
}
