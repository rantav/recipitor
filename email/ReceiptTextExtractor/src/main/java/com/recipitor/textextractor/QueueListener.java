package com.recipitor.textextractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final long QUEUE_POLL_PERIOD = 32000; //32 secodns
	protected static final long PERIOD_FOR_JOIN = 60000; //60 seconds
	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(QueueListener.class);
	IReceiptHandler receiptHandler;
	ObjectMapper mapper;
	ExecutorService executorService;
	MessageQueue RES;
	MessageQueue REQ;
	boolean lastPopWasNull;

	public QueueListener() {
		setShoydownHook();
	}

	/**
	 * 
	 */
	private void setShoydownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				onShutDown();
			}
		});
	}

	/**
	 * 
	 */
	void onShutDown() {
		LGR.info("got shoutdown signal, start shuting down");
		setShoutDownFlag();
		try {
			LGR.info("about to join workers thread");
			if (executorService != null) { // might be null -- for some other threads
				executorService.shutdown();
				final boolean s = executorService.awaitTermination(PERIOD_FOR_JOIN, TimeUnit.MILLISECONDS);
				if (s) LGR.info("application was gracefully finished");
				else LGR.error("time period had passed and join was not done");
			}
		} catch (final InterruptedException e) {
			LGR.error("join was interrupted [{}]", e.getMessage(), e);
		}
	}

	Object lock = new Object();
	boolean shoutDownFlag;

	void setShoutDownFlag() {
		synchronized (lock) {
			shoutDownFlag = true;
		}
	}

	boolean shouldContinue() {
		synchronized (lock) {
			return !shoutDownFlag;
		}
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

	/**
	 * @param v the requestQueue to set
	 * @throws SQSException 
	 */
	@Inject
	public void setRequestQueue(@Named("request") final MessageQueue v) throws SQSException {
		REQ = v;
		LGR.debug("REQ getVisibilityTimeout [{}]", REQ.getVisibilityTimeout());
	}

	/**
	 * @param v the responsetQueue to set
	 * @throws SQSException 
	 */
	@Inject
	public void setResponseQueue(@Named("response") final MessageQueue v) throws SQSException {
		RES = v;
		LGR.debug("RES getVisibilityTimeout [{}]", RES.getVisibilityTimeout());
	}

	/**
	 * @param val the threadPool to set
	 */
	@Inject
	public void setExecutorService(final ExecutorService val) {
		executorService = val;
	}

	public void listen() throws Exception {
		while (shouldContinue()) {
			final Message msg = popOrWait();
			if (msg == null) continue;
			executorService.execute(new Runnable() {

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
		LGR.info("stop listen to queue");
	}

	/**
	 * @return
	 * @throws SQSException 
	 */
	Message popOrWait() throws SQSException {
		final Message msg = REQ.receiveMessage();
		if (msg == null) {
			//			if (!lastPopWasNull)
			//				LGR.debug("going to sleep. requesu queue size is ~[{}] ", REQ.getApproximateNumberOfMessages());
			lastPopWasNull = true;
			doWait();
		} else {
			LGR.debug("pop a message from queue. message id is [{}]", msg.getMessageId());
			REQ.deleteMessage(msg);
			LGR.debug("msg war removed from the queue");
			//			if (history.contains(msg.getMessageId())) {
			//				LGR.debug("message is in the hosttory, will skipp it");
			//				doWait();
			//				return null;
			//			}
			lastPopWasNull = false;
		}
		//		else LGR.debug("request queue size is ~ [" + requestQueue.getApproximateNumberOfMessages() + "]");
		return msg;
	}

	/**
	 * @param msg
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws sonParseException 
	 */
	void handleRequestMessage(final Message msg) throws Exception {
		final Body b = mapper.readValue(msg.getMessageBody(), Body.class);
		LGR.debug("the receipt id is [{}]", b.getReceipt().getId());
		final List<GuessResult> lst = receiptHandler.handle(b);
		sendResponse(b.getReceipt().getId(), lst);
		try {
			//			REQ.deleteMessage(msg);
			//			history.add(msg.getMessageId());
		} catch (final Throwable th) {
			LGR.error("got error ", th);
		}
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
		LGR.debug("about to post the message\n{}", m);
		RES.sendMessage(m);
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
