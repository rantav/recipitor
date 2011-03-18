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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

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
		final Server server = new Server(9999);
		final ContextHandler context = new ContextHandler();
		context.setContextPath("/hello");
		context.setResourceBase(".");
		server.setHandler(context);
		context.setHandler(ds);
		server.start();
		ds.listen();
		server.join();
	}
}
