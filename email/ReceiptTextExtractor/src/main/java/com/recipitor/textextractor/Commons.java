/**
 * NAME: Commons.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Commons {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(Commons.class);

	public static InputStream loadInputStreamFromSourceName(final String rn) {
		return new BufferedInputStream(Commons.class.getResourceAsStream(rn));
	}

	public static Properties loadPropsFromResourceName(final String rn) throws Exception {
		final Properties $ = new Properties();
		$.load(loadInputStreamFromSourceName(rn));
		return $;
	}

	public static List<String> loadListFromInputStream(final InputStream is) throws Exception {
		final List<String> $ = new LinkedList<String>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (true) {
			final String l = br.readLine();
			if (l == null) break;
			$.add(l);
		}
		return $;
	}

	public static List<String> loadListFromResourceName(final String rn) throws Exception {
		return loadListFromInputStream(loadInputStreamFromSourceName(rn));
	}

	/**
	 * @param run
	 * @return
	 * @throws Exception 
	 */
	public static String loadStringFromInputStream(final InputStream is) throws Exception {
		final StringBuilder $ = new StringBuilder();
		boolean ft = true;
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (true) {
			final String l = br.readLine();
			if (l == null) break;
			if (!ft) $.append("\n");
			$.append(l);
			ft = false;
		}
		return $.toString();
	}
}
