/**
 * NAME: ProcessExecutor.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ProcessExecutor {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ProcessExecutor.class);

	public BufferedReader run(final String... args) throws Exception {
		final ProcessBuilder pb = new ProcessBuilder(args);
		final Process p = pb.start();
		p.waitFor();
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	public List<String> runAndGetResltsAsList(final String... args) throws Exception {
		final List<String> $ = new LinkedList<String>();
		final BufferedReader br = run(args);
		while (true) {
			final String l = br.readLine();
			if (l == null) break;
			$.add(l);
		}
		return $;
	}
}
