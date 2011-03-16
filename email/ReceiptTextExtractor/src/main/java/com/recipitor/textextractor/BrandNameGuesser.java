/**
 * NAME: BrandNameGuesser.java
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
public class BrandNameGuesser implements IBrandNameGuesser {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(BrandNameGuesser.class);
	List<String> groceryStores;

	/**
	 * @throws Exception 
	 * 
	 */
	public BrandNameGuesser() throws Exception {
		groceryStores = Commons.loadListFromResourceName("/com/receipitor/textextractor/conf/NotableGroceryStores.txt");
	}

	/**
	 * @see com.recipitor.textextractor.IBrandNameGuesser#guess(com.recipitor.textextractor.ExtractedTokens)
	 */
	@Override
	public GuessResult guess(final ExtractedTokens et) {
		final GuessResult $ = null;
		for (final String gn : groceryStores)
			for (final String token : et.tokens) {
			}
		return $;
	}
}
