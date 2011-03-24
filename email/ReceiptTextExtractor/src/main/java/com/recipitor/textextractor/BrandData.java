/**
 * NAME: BrandNamesConfig.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 24, 2011
 * Associated Bugs: 
 */
public class BrandData {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(BrandData.class);
	public Map<String, List<String>> map = new HashMap<String, List<String>>();

	public Set<Entry<String, List<String>>> names() {
		return map.entrySet();
	}

	public BrandData() { // def constuctor
	}
}
