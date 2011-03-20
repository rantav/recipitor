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

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ProcessExecutor {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ProcessExecutor.class);

	public InputStream run(final String... args) throws Exception {
		final ProcessBuilder pb = new ProcessBuilder(args);
		final Process p = pb.start();
		p.waitFor();
		return new BufferedInputStream(p.getInputStream());
	}

	public String runAndGetResltsAsString(final String... args) throws Exception {
		return Commons.loadStringFromInputStream(run(args));
	}
}
