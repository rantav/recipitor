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
 * environemnt is detected by ENVIRONMENT env variable. if set to -DENVIRONMENT=PRODUCTION 
 * acts as in production environemnt otherwise choose developemtn environemnt    
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class Main {

	private static final int RECOVER_DELAY = 5000;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Main.class);

	public static void main(final String[] args) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("TextExtractor started");
		final Injector injector = Guice.createInjector(new TextExtractorModule());
		final QueueListener qs = injector.getInstance(QueueListener.class);
		invokeJetty();
		while (true)
			try {
				qs.listen();
			} catch (final Throwable t) {
				LGR.error("got exceptoin [" + t.getMessage(), t);
				if (LGR.isDebugEnabled()) LGR.debug("wrill try to re-listen to queue in 5 secodns");
				Thread.sleep(RECOVER_DELAY);
			}
	}

	/**
	 * 
	 */
	private static void invokeJetty() {
		//		final Server server = new Server(9999);
		//		final ContextHandler context = new ContextHandler();
		//		context.setContextPath("/hello");
		//		context.setResourceBase(".");
		//		server.setHandler(context);
		//		context.setHandler(ds);
		//		server.start();
		//		server.join();
	}
}
