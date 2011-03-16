/**
 * NAME: ReceiptHandler.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Inject;
import com.xerox.amazonws.sqs2.Message;

/**
 * @author ymaman
 * created: Mar 16, 2011
 * Associated Bugs: 
 */
public class ReceiptHandler implements IReceiptHandler {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ReceiptHandler.class);
	ProcessExecutor processExecutor;
	ObjectMapper mapper;

	/**
	 * 
	 */
	public ReceiptHandler() {
		mapper = new ObjectMapper();
	}

	/**
	 * @param pe the processExecutor to set
	 */
	@Inject
	public void setProcessExecutor(final ProcessExecutor pe) {
		processExecutor = pe;
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.textextractor.IReceiptHandler#handle(com.xerox.amazonws.sqs2.Message)
	 */
	@Override
	public void handle(final Message msg) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("handling \n" + msg);
		final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
		final List<String> br = processExecutor.runAndGetResltsAsList("scripts/go.sh", msg.getMessageId(), b.getUrl()
		//						"http://rabidpaladin.com/images/rabidpaladin_com/WindowsLiveWriter/ShortShoppingTrip_1067C/receipt_2.jpg"
		//				"http://oi44.tinypic.com/f087y9.jpg");
				);
		final StringBuilder sb = new StringBuilder();
		for (final String l : br)
			sb.append(l + "\n");
		if (LGR.isDebugEnabled()) LGR.debug("\n" + sb.toString());
	}
}
