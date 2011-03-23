/**
 * NAME: IFuzzyMatcher.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public interface IFuzzyMatcher {

	GuessResult isExist(String term, String tokens) throws Exception;
}
