/**
 * NAME: TextExtractorModule.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.threadpool.DefaultThreadPool;
import org.apache.commons.threadpool.ThreadPool;
import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.xerox.amazonws.sqs2.QueueService;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class TextExtractorModule extends AbstractModule {

	private static final String ENV_PRODUCTION_NAME = "production";
	private static final String ENV_DEV_NAME = "dev";
	private static final String PRODUCTION = "PRODUCTION";
	private static final String ENVIRONMENT = "ENVIRONMENT";
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(TextExtractorModule.class);
	final Properties conf = new Properties();
	public static boolean isDev = System.getProperty(ENVIRONMENT) == null
			|| !System.getProperty(ENVIRONMENT).equals(PRODUCTION);

	/** 
	 * 
	 * @return either "dev" or "production" based on the environment
	 */
	public static String getEnv() {
		return TextExtractorModule.isDev ? ENV_DEV_NAME : ENV_PRODUCTION_NAME;
	}

	public TextExtractorModule() throws IOException {
		if (LGR.isInfoEnabled()) LGR.info("detetcing environment as [" + getEnv() + "]");
		final InputStream resourceAsStream = Main.class.getResourceAsStream("/com/recipitor/textextractor/conf/"
				+ getEnv() + "/recipitor.aws.properties");
		if (LGR.isDebugEnabled()) LGR.debug("getEnv is " + getEnv());
		conf.load(resourceAsStream);
	}

	@SuppressWarnings("unused")
	@Provides
	@Inject
	private QueueService proviceQueueService(@Named("aws.accessId") final String aid,
			@Named("aws.secretKey") final String sk) {
		return new QueueService(aid, sk, true);
	}

	@SuppressWarnings("unused")
	@Provides
	private ThreadPool provideThreadPool() {
		return new DefaultThreadPool(5);
	}

	@Override
	protected void configure() {
		Names.bindProperties(binder(), conf);
		bind(IReceiptHandler.class).to(ReceiptHandler.class);
		bind(OCRExtractor.class).to(Cuneiform.class);
		bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
		bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
	}
}
