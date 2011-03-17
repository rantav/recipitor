/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor.data.respons;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.recipitor.textextractor.GuessResult;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Receipt {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Receipt.class);
	String id;
	List<GuessResult> extracted_store_names = new LinkedList<GuessResult>();
	List<String> extracted_tokens_list = new LinkedList<String>();

	/**
	 * @return the extracted_store_names
	 */
	public List<GuessResult> getExtracted_store_names() {
		return extracted_store_names;
	}

	/**
	 * @param v the extracted_store_names to set
	 */
	public void setExtracted_store_names(final List<GuessResult> v) {
		extracted_store_names = v;
	}

	/**
	 * @return the extracted_tokens_list
	 */
	public List<String> getExtracted_tokens_list() {
		return extracted_tokens_list;
	}

	/**
	 * @param val the extracted_tokens_list to set
	 */
	public void setExtracted_tokens_list(final List<String> val) {
		extracted_tokens_list = val;
	}

	public Receipt() {
		// must have an empty constructor
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param v the id to set
	 */
	public void setId(final String v) {
		id = v;
	}
}
