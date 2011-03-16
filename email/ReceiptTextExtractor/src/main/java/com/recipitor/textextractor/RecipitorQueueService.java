/**
 * NAME: RecipitorQueueService.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xerox.amazonws.sqs2.QueueService;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class RecipitorQueueService extends QueueService {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(RecipitorQueueService.class);

	/**
	 * @param awsAccessId
	 * @param awsSecretKey
	 * @param isSecure
	 */
	@Inject
	public RecipitorQueueService(@Named("aws.accessId") final String aid, @Named("aws.secretKey") final String sk) {
		super(aid, sk, true);
	}
}
