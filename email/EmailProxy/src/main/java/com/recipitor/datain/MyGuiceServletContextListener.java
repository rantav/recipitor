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
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
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
	public static boolean isDev = SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;

	/** 
	 * 
	 * @return either "dev" or "production" based on the runtime platfrom
	 */
	public static String getEnv() {
		return MyGuiceServletContextListener.isDev ? "dev" : "production";
	}

	@Override
	protected Injector getInjector() {
		ServletModule servletModule = null;
		try {
			servletModule = new ServletModule() {

				final Properties conf = new Properties();
				{
					conf.load(ServletModule.class.getResourceAsStream("/com/recipitor/datain/conf/"
							+ MyGuiceServletContextListener.getEnv() + "/conf.properties"));
				}

				@Override
				protected void configureServlets() {
					serve("/_ah/mail/receipt*").with(MailHandlerServlet.class);
					serve("/tasks/post-email*").with(TaskHandlerServlet.class);
					Names.bindProperties(binder(), conf);
				}
			};
		} catch (final IOException e) {
			LGR.fatal("error while creating the ServletModule [" + e.getMessage() + "]", e);
		}
		final AbstractModule businessModule = new AbstractModule() {

			@Override
			protected void configure() {
				bind(IMailExtractor.class).to(MailExtractor.class);
				bind(IMailDAO.class).to(MailDAO.class);
				bind(IMailService.class).to(MailService.class);
				bind(IMailPoster.class).to(MailPoster.class);
			}
		};
		return Guice.createInjector(servletModule, businessModule);
	}
}
