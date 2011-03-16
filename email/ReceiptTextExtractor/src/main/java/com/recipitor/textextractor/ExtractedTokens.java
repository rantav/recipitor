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

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class ExtractedTokens {

	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(final List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ExtractedTokens.class);
	List<String> tokens;
}
