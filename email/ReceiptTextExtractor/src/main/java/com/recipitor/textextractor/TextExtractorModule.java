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
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueService;
import com.xerox.amazonws.sqs2.SQSException;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class TextExtractorModule extends AbstractModule {

	/** environment is detected by ENVIRONMENT env variable. if set to -DENVIRONMENT=PRODUCTION 
	* acts as in production environment otherwise choose development environment
	*/
	private static final String ENV_PRODUCTION_NAME = "production";
	private static final String ENV_DEV_NAME = "dev";
	private static final String PRODUCTION = "PRODUCTION";
	private static final String ENVIRONMENT = "ENVIRONMENT";
	private static final int MAX_THREADS = 2;
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

	//
	//	@SuppressWarnings("unused")
	//	@Provides
	//	@Inject
	//	private QueueService proviceQueueService(@Named("aws.accessId") final String aid,
	//			@Named("aws.secretKey") final String sk) {
	//		return new QueueService(aid, sk, true);
	//	}
	@SuppressWarnings("unused")
	@Provides
	@Inject
	@Named("request")
	private MessageQueue provideRequestQueueService(@Named("aws.accessId") final String aid,
			@Named("aws.secretKey") final String sk, @Named("aws.request.queueName") final String qn)
			throws SQSException {
		if (LGR.isDebugEnabled()) LGR.debug("request queue is [" + qn + "]");
		final MessageQueue $ = new QueueService(aid, sk, true).getOrCreateMessageQueue(qn);
		$.setEncoding(false);
		return $;
	}

	@SuppressWarnings("unused")
	@Provides
	@Inject
	@Named("response")
	private MessageQueue provideResponseQueueService(@Named("aws.accessId") final String aid,
			@Named("aws.secretKey") final String sk, @Named("aws.response.queueName") final String qn)
			throws SQSException {
		if (LGR.isDebugEnabled()) LGR.debug("response queue is [" + qn + "]");
		final MessageQueue $ = new QueueService(aid, sk, true).getOrCreateMessageQueue(qn);
		$.setEncoding(false);
		return $;
	}

	@SuppressWarnings("unused")
	@Provides
	private ThreadPool provideThreadPool() {
		return new DefaultThreadPool(MAX_THREADS);
	}

	@Override
	protected void configure() {
		Names.bindProperties(binder(), conf);
		//		bind(QueueListener.class);
		//		bind(MessageQueue.class).annotatedWith(Names.named("request"));
		//		bind(MessageQueue.class).annotatedWith(Names.named("response"));
		bind(IReceiptHandler.class).to(ReceiptHandler.class);
		bind(OCRExtractor.class).to(Cuneiform.class);
		bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
		bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
	}
}
