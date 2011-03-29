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
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;

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
	private static Logger LGR = LoggerFactory.getLogger(TextExtractorModule.class);
	final Properties conf = new Properties();
	private final Properties secret = new Properties();
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
		LGR.info("detetcing environment as [{}]", getEnv());
		conf.load(Commons.loadInputStreamFromSourceName("/com/recipitor/textextractor/conf/" + getEnv()
				+ "/recipitor.aws.properties"));
		secret.load(Commons
				.loadInputStreamFromSourceName("/com/recipitor/textextractor/conf/recipitor.aws.secret.properties"));
	}

	@SuppressWarnings("unused")
	@Provides
	@Inject
	@Named("request")
	private MessageQueue provideRequestQueueService(@Named("aws.accessId") final String aid,
			@Named("aws.secretKey") final String sk, @Named("aws.request.queueName") final String qn)
			throws SQSException {
		LGR.debug("request queue is [{}]", qn);
		final MessageQueue $ = createQueue(aid, sk, qn);
		return $;
	}

	/**
	 * @param aid
	 * @param sk
	 * @param qn
	 * @return
	 * @throws SQSException
	 */
	private MessageQueue createQueue(final String aid, final String sk, final String qn) throws SQSException {
		final MessageQueue $ = SQSUtils.connectToQueue(qn, aid, sk);
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
		LGR.debug("response queue is [{}]", qn);
		final MessageQueue $ = createQueue(aid, sk, qn);
		return $;
	}

	@SuppressWarnings("unused")
	@Provides
	private ExecutorService provideThreadPool() {
		return Executors.newFixedThreadPool(MAX_THREADS);
	}

	@Provides
	BrandData provideBrandNamesConfig() throws Exception {
		final BrandData $ = new ObjectMapper().readValue(
				Commons.loadInputStreamFromSourceName("/com/recipitor/textextractor/conf/NotableGroceryStores.json"),
				BrandData.class);
		return $;
	}

	@Override
	protected void configure() {
		Names.bindProperties(binder(), conf);
		Names.bindProperties(binder(), secret);
		//		bind(QueueListener.class);
		//		bind(MessageQueue.class).annotatedWith(Names.named("request"));
		//		bind(MessageQueue.class).annotatedWith(Names.named("response"));
		bind(IReceiptHandler.class).to(ReceiptHandler.class);
		bind(OCRExtractor.class).to(Cuneiform.class);
		bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
		bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
	}
}
