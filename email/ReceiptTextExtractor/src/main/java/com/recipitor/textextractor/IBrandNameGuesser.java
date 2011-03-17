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

import java.util.List;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public interface IBrandNameGuesser {

	List<GuessResult> guess(final ExtractedTokens et) throws Exception;
}
