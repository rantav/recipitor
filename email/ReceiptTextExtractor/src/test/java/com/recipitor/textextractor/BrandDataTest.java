/**
 * NAME: BrandNamesConfigTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 24, 2011
 * Associated Bugs: 
 */
public class BrandDataTest {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(BrandDataTest.class);

	@Test
	@SuppressWarnings("unchecked")
	public void testLoadJson() throws JsonParseException, JsonMappingException, IOException {
		final String s = "{\"a\":[\"1\", \"2\", \"3\", \"4\"], \"b\":[\"99\"]}";
		final ObjectMapper om = new ObjectMapper();
		final Map<String, List<Integer>> m = om.readValue(s, Map.class);
		Assert.assertEquals(2, m.size());
		Assert.assertEquals(1, m.get("b").size());
	}

	@Test
	public void loadConfigFile() throws Exception {
		final BrandData b = new ObjectMapper().readValue(
				Commons.loadInputStreamFromSourceName("/com/recipitor/textextractor/conf/NotableGroceryStores.json"),
				BrandData.class);
		Assert.assertNotNull(b);
	}
}
