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

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.xerox.amazonws.sqs2.QueueService;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class TextExtractorModule extends AbstractModule {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(TextExtractorModule.class);
	final Properties conf = new Properties();
	public static boolean isDev = System.getProperty("PLATFORM") == null
			|| !System.getProperty("PLATFORM").equals("EC2");

	/** 
	 * 
	 * @return either "dev" or "production" based on the runtime platfrom
	 */
	public static String getEnv() {
		return TextExtractorModule.isDev ? "dev" : "production";
	}

	public TextExtractorModule() throws IOException {
		if (LGR.isInfoEnabled()) LGR.info("detetcing environment as [" + getEnv() + "]");
		final InputStream resourceAsStream = Main.class.getResourceAsStream("/com/recipitor/textextractor/conf/"
				+ getEnv() + "/recipitor.aws.properties");
		if (LGR.isDebugEnabled()) LGR.debug("getEnv is " + getEnv());
		conf.load(resourceAsStream);
	}

	@Override
	protected void configure() {
		Names.bindProperties(binder(), conf);
		bind(QueueService.class).to(RecipitorQueueService.class);
		bind(IReceiptHandler.class).to(ReceiptHandler.class);
		bind(OCRExtractor.class).to(Cuneiform.class);
		bind(IBrandNameGuesser.class).to(BrandNameGuesser.class);
		bind(IFuzzyMatcher.class).to(AGrepMatcher.class);
		//		bind(ProcessExecutor.class).to(ProcessExecutor.class);
	}
}
