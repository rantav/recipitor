/**
 * NAME: Main.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class Main {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Main.class);

	public static void main(final String[] args) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("TextExtractor started");
		final Injector injector = Guice.createInjector(new TextExtractorModule());
		final QueueListener ds = injector.getInstance(QueueListener.class);
		ds.listen();
	}
}
