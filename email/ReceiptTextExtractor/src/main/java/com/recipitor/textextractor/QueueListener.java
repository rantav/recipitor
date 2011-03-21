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
import com.xerox.amazonws.sqs2.SQSException;

/**
 * This sample application retrieves (dequeues) a message from the queue specified by
 * the value of the queuename parameter. If successful, it deletes the message from the queue.
 * On error, it retries a number of times.
 */
public class QueueListener {

	private static final long QUEUE_POLL_PERIOD = 30000;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(QueueListener.class);
	IReceiptHandler receiptHandler;
	ObjectMapper mapper;
	private ThreadPool threadPool;
	private MessageQueue responseQueue;
	private MessageQueue requestQueue;

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

	/**
	 * @param v the requestQueue to set
	 */
	@Inject
	public void setRequestQueue(@Named("request") final MessageQueue v) {
		requestQueue = v;
	}

	/**
	 * @param v the responsetQueue to set
	 */
	@Inject
	public void setResponseQueue(@Named("response") final MessageQueue v) {
		responseQueue = v;
	}

	/**
	 * @param val the threadPool to set
	 */
	@Inject
	public void setThreadPool(final ThreadPool val) {
		threadPool = val;
	}

	public void listen() throws Exception {
		while (true) {
			final Message msg = popOrWait();
			if (msg == null) continue;
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
	}

	/**
	 * @return
	 * @throws SQSException 
	 */
	Message popOrWait() throws SQSException {
		final Message msg = requestQueue.receiveMessage();
		if (msg == null) doWait();
		else if (LGR.isDebugEnabled())
			LGR.debug("request queue size is ~ [" + requestQueue.getApproximateNumberOfMessages() + "]");
		return msg;
	}

	/**
	 * @param msg
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws sonParseException 
	 */
	void handleRequestMessage(final Message msg) throws Exception {
		LGR.info("msg [" + msg.getMessageId() + "]");
		final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
		final List<GuessResult> lst = receiptHandler.handle(b);
		sendResponse(b.getReceipt().getId(), lst);
		requestQueue.deleteMessage(msg);
	}

	/**
	 * @param lst
	 * @throws Exception 
	 */
	private void sendResponse(final String id, final List<GuessResult> lst) throws Exception {
		final com.recipitor.textextractor.data.response.Body rb = buildResponsBody(lst, id);
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		mapper.writeValue(bos, rb);
		final String m = bos.toString();
		if (LGR.isDebugEnabled()) LGR.debug("about to post the message\n" + m);
		responseQueue.sendMessage(m);
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
				wait(QUEUE_POLL_PERIOD);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
