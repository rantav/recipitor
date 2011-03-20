package com.recipitor.textextractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.threadpool.ThreadPool;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.recipitor.textextractor.data.request.Body;
import com.recipitor.textextractor.data.response.Receipt;
import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueService;
import com.xerox.amazonws.sqs2.SQSException;

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
	private MessageQueue requestQueue;
	private MessageQueue responsetQueue;
	private ThreadPool threadPool;

	/**
	 * @param val the threadPool to set
	 */
	@Inject
	public void setThreadPool(final ThreadPool val) {
		threadPool = val;
	}

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
		LGR.debug("queue : " + requestQueueName);
		try {
			init();
			while (true) {
				final Message msg = requestQueue.receiveMessage();
				if (msg == null) {
					//					if (LGR.isDebugEnabled()) LGR.debug("going to sleep");
					doWait();
					continue;
				}
				threadPool.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							handleRequestMessage(msg);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (final Exception ex) {
			LGR.error("EXCEPTION, queue : " + requestQueueName, ex);
		}
	}

	/**
	 * @throws SQSException
	 */
	private void init() throws SQSException {
		requestQueue = queueService.getOrCreateMessageQueue(requestQueueName);
		responsetQueue = queueService.getOrCreateMessageQueue(responseQueueName);
		responsetQueue.setEncoding(false);
		requestQueue.setEncoding(false);
	}

	/**
	 * @param msg
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws sonParseException 
	 */
	private void handleRequestMessage(final Message msg) throws Exception {
		LGR.info("msg [" + msg.getMessageId() + "]");
		final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
		final List<GuessResult> lst = receiptHandler.handle(b);
		//		sendResponse(b.getReceipt().getId(), lst);
		//		requestQueue.deleteMessage(msg);
	}

	/**
	 * @param lst
	 * @throws Exception 
	 */
	private void sendResponse(final String id, final List<GuessResult> lst) throws Exception {
		final com.recipitor.textextractor.data.response.Body rb = buildResponsBody(lst, id);
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		mapper.writeValue(bos, rb);
		if (LGR.isDebugEnabled()) LGR.debug("about to post the message\n" + bos.toString());
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
