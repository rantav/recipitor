/**
 * NAME: IBrandNameGuesser.java
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
public interface IBrandNameGuesser {

	GuessResult guess(final ExtractedTokens et);
}
