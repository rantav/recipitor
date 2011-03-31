/**
 * NAME: GuessResult.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class GuessResult {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(GuessResult.class);
	public String name;
	public Double distance;
	public int numOfErrors;
	public int termLength;
	public String serachTerm;
	public List<String> foundTerm = new ArrayList<String>();
}
