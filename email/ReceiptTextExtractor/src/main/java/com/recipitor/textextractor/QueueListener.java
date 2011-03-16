package com.recipitor.textextractor;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueService;

/**
 * This sample application retrieves (dequeues) a message from the queue specified by
 * the value of the queuename parameter. If successful, it deletes the message from the queue.
 * On error, it retries a number of times.
 */
public class QueueListener {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(QueueListener.class);
	QueueService queueService;
	IReceiptHandler receiptHandler;
	ObjectMapper mapper;

	/**
	 * @param m the mapper to set
	 */
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

	private String queueName;

	/**
	 * @param qs the queueService to set
	 */
	@Inject
	public void setQueueService(final QueueService qs) {
		queueService = qs;
	}

	@Inject
	void setQueueName(@Named("aws.queueName") final String qn) {
		queueName = qn;
	}

	public void listen() throws Exception {
		final int count = 0;
		//		final String queueName = "extract_store_name_request_prod";
		LGR.debug("queue : " + queueName);
		try {
			// Retrieve the message queue object (by name)
			//			final MessageQueue msgQueue = queueService.getOrCreateMessageQueue(queueName);
			final MessageQueue msgQueue = queueService.getMessageQueue(queueName);
			msgQueue.setEncoding(false);
			// Try to retrieve (dequeue) the message, and then delete it.
			Message msg = null;
			while (true) {
				msg = msgQueue.receiveMessage();
				if (msg == null) {
					doWait();
					continue;
				}
				LGR.info("msg [" + msg.getMessageId() + "]");
				final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
				receiptHandler.handle(b);
			}
		} catch (final Exception ex) {
			LGR.error("EXCEPTION, queue : " + queueName, ex);
		}
		LGR.debug("Deleted " + count + " messages");
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
