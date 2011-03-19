package com.recipitor.textextractor;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.recipitor.textextractor.data.request.Body;
import com.recipitor.textextractor.data.response.Receipt;
import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueService;

/**
 * This sample application retrieves (dequeues) a message from the queue specified by
 * the value of the queuename parameter. If successful, it deletes the message from the queue.
 * On error, it retries a number of times.
 */
public class QueueListener {

	//	extends AbstractHandler {
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(QueueListener.class);
	QueueService queueService;
	IReceiptHandler receiptHandler;
	ObjectMapper mapper;
	String accessID;
	String secretKey;

	@Inject
	public QueueListener(@Named("aws.accessId") final String aid, @Named("aws.secretKey") final String sk) {
		accessID = aid;
		secretKey = sk;
	}

	/**
	 * @param m the mapper to set
	 */
	@Inject
	public void setMapper(final ObjectMapper m) {
		mapper = m;
	}

	/**
	 * @param rh the receiptHandler to set
	 */
	@Inject
	public void setReceiptHandler(final IReceiptHandler rh) {
		receiptHandler = rh;
	}

	private String requestQueueName;
	private String responseQueueName;

	/**
	 * @param qn the responseQueueName to set
	 */
	@Inject
	public void setResponseQueueName(@Named("aws.response.queueName") final String qn) {
		responseQueueName = qn;
	}

	/**
	 * @param qs the queueService to set
	 */
	@Inject
	public void setQueueService(final QueueService qs) {
		queueService = qs;
	}

	@Inject
	void setRequestQueueName(@Named("aws.request.queueName") final String qn) {
		requestQueueName = qn;
	}

	public void listen() throws Exception {
		final int count = 0;
		//		final String queueName = "extract_store_name_request_prod";
		LGR.debug("queue : " + requestQueueName);
		try {
			// Retrieve the message queue object (by name)
			final MessageQueue requestQueue = queueService.getOrCreateMessageQueue(requestQueueName);
			//			final MessageQueue responsetQueue = SQSUtils.connectToQueue(responseQueueName, accessID, secretKey);
			final MessageQueue responsetQueue = queueService.getOrCreateMessageQueue(responseQueueName);
			responsetQueue.setEncoding(false);
			requestQueue.setEncoding(false);
			// Try to retrieve (dequeue) the message, and then delete it.
			Message msg = null;
			while (true) {
				msg = requestQueue.receiveMessage();
				if (msg == null) {
					if (LGR.isDebugEnabled()) LGR.debug("going to sleep");
					doWait();
					continue;
				}
				LGR.info("msg [" + msg.getMessageId() + "]");
				//				msgQueue.deleteMessage(msg);
				final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
				final List<GuessResult> lst = receiptHandler.handle(b);
				final com.recipitor.textextractor.data.response.Body rb = buildResponsBody(lst, b.getReceipt().getId());
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();
				mapper.writeValue(bos, rb);
				if (LGR.isDebugEnabled()) LGR.debug("about to post the message\n" + bos.toString());
				final String url = responsetQueue.sendMessage(bos.toString());
				if (LGR.isDebugEnabled()) {
					LGR.debug("url is [" + url + "]");
					break;
				}
			}
		} catch (final Exception ex) {
			LGR.error("EXCEPTION, queue : " + requestQueueName, ex);
		}
		LGR.debug("Deleted " + count + " messages");
	}

	/**
	 * @param lst
	 * @param id
	 * @return
	 */
	private com.recipitor.textextractor.data.response.Body buildResponsBody(final List<GuessResult> lst, final String id) {
		final com.recipitor.textextractor.data.response.Body $ = new com.recipitor.textextractor.data.response.Body();
		final Receipt r = new Receipt();
		$.setReceipt(r);
		r.setExtracted_store_names(lst);
		r.setId(id);
		return $;
	}

	/**
	 * 
	 */
	private void doWait() {
		try {
			synchronized (this) {
				wait(1000);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
