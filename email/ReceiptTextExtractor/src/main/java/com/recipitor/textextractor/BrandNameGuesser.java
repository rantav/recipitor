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
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class BrandNameGuesser implements IBrandNameGuesser {

	//	private static final Double MAX_DISTANCE = 0.75;
	//	private static final int MAX_NUM_OF_RESULTS = 5;
	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(BrandNameGuesser.class);
	//	List<String> groceryStores;
	IFuzzyMatcher fuzzyMatcher;
	BrandData conf;

	/**
	 * @param v the conf to set
	 */
	@Inject
	public void setConf(final BrandData v) {
		conf = v;
	}

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
		//		groceryStores = Commons.loadListFromResourceName("/com/recipitor/textextractor/conf/NotableGroceryStores.txt");
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.textextractor.IBrandNameGuesser#guess(com.recipitor.textextractor.ExtractedTokens)
	 */
	@Override
	public List<GuessResult> guess(final ExtractedTokens et) throws Exception {
		final List<GuessResult> lst = new LinkedList<GuessResult>();
		for (final Entry<String, List<String>> en : conf.names())
			for (final String t : en.getValue())
				for (final String token : et.tokens) {
					if (token == null || token.trim().length() == 0) continue;
					final GuessResult gr = fuzzyMatcher.isExist(t.toLowerCase(), token.toLowerCase());
					if (gr == null) continue;
					gr.name = en.getKey();
					lst.add(gr);
				}
		Collections.sort(lst, new Comparator<GuessResult>() {

			@Override
			// comparing  by: first key is num of errors, and then by length of the term 
			public int compare(final GuessResult o1, final GuessResult o2) {
				//				int $ = o1.numOfErrors - o2.numOfErrors;
				//				if ($ != 0) return $;
				//				$ = -(o1.termLength - o2.termLength);
				final int $ = o1.distance.compareTo(o2.distance);
				return $;
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
		if (!lst.isEmpty()) $.add(lst.get(0));
		//		final double th = lst.get(0).distance;
		//		for (final GuessResult gr : lst) {
		//			if ($.size() > MAX_NUM_OF_RESULTS) break;
		//			if (gr.distance <= MAX_DISTANCE && gr.distance <= th) $.add(gr);
		//		}
		return $;
	}
}
