package com.recipitor.datain;

/**
 * NAME: MyGuiceServletContextListener.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public class MyGuiceServletContextListener extends GuiceServletContextListener {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MyGuiceServletContextListener.class);

	@Override
	protected Injector getInjector() {
		final ServletModule servletModule = new ServletModule() {

			@Override
			protected void configureServlets() {
				serve("/_ah/mail/receipt*").with(MailHandlerServlet.class);
				serve("/tasks/post-email*").with(TaskHandlerServlet.class);
			}
		};
		final AbstractModule businessModule = new AbstractModule() {

			@Override
			protected void configure() {
				bind(IMailExtractor.class).to(MailExtractor.class);
				bind(IMailDAO.class).to(MailDAO.class);
				bind(IMailService.class).to(MailService.class);
			}
		};
		return Guice.createInjector(servletModule, businessModule);
	}
}
