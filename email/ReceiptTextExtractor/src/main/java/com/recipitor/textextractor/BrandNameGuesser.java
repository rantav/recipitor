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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class BrandNameGuesser implements IBrandNameGuesser {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(BrandNameGuesser.class);
	List<String> groceryStores;
	IFuzzyMatcher fuzzyMatcher;

	/**
	 * @param fm the fuzzyMatcher to set
	 */
	@Inject
	public void setFuzzyMatcher(final IFuzzyMatcher fm) {
		fuzzyMatcher = fm;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public BrandNameGuesser() throws Exception {
		groceryStores = Commons.loadListFromResourceName("/com/recipitor/textextractor/conf/NotableGroceryStores.txt");
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.textextractor.IBrandNameGuesser#guess(com.recipitor.textextractor.ExtractedTokens)
	 */
	@Override
	public List<GuessResult> guess(final ExtractedTokens et) throws Exception {
		final List<GuessResult> lst = new LinkedList<GuessResult>();
		for (final String gn : groceryStores)
			for (final String token : et.tokens) {
				final double l = fuzzyMatcher.isExist(gn.toLowerCase(), token.toLowerCase());
				final GuessResult gr = new GuessResult();
				gr.storeName = gn;
				gr.distance = l;
				lst.add(gr);
			}
		Collections.sort(lst, new Comparator<GuessResult>() {

			@Override
			public int compare(final GuessResult o1, final GuessResult o2) {
				return o1.distance.compareTo(o2.distance);
			}
		});
		final List<GuessResult> $ = filterResult(lst);
		return $;
	}

	/**
	 * @param lst
	 * @return
	 */
	private List<GuessResult> filterResult(final List<GuessResult> lst) {
		final List<GuessResult> $ = new LinkedList<GuessResult>();
		final double th = lst.get(0).distance;
		for (final GuessResult gr : lst)
			if (gr.distance <= th) $.add(gr);
		return $;
	}
}
