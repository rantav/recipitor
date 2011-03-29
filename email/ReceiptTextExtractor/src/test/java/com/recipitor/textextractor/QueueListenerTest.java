/**
 * NAME: QueueListenerTest.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.recipitor.textextractor.data.request.Body;
import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;

/**
 * @author ymaman
 * created: Mar 20, 2011
 * Associated Bugs: 
 */
public class QueueListenerTest {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(QueueListenerTest.class);
	private static QueueListener _;

	@BeforeClass
	public static void setup() {
		_ = new QueueListener();
	}

	@Test
	public void testShutDown() throws Exception {
		final Injector injector = Guice.createInjector(new TextExtractorModule());
		final QueueListener __ = injector.getInstance(QueueListener.class);
		final String fn = "/tmp/img-test-large.jpg";
		Commons.copyStreamIntoFile(Commons.loadInputStreamFromSourceName("/rec02.jpg"), fn);
		for (int i = 0; i < 2; i++) {
			final String id = "test_msg_id_" + i;
			final String msg = "{\"receipt\":{\"id\":\"" + id + "\",\"url\":\"file://" + fn + "\"}}";
			__.REQ.sendMessage(msg);
		}
		final Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					__.listen();
				} catch (final Exception e) {
					e.printStackTrace();
					Assert.fail();
				}
			}
		});
		t.start();
		Thread.sleep(3000);
		__.onShutDown();
	}

	@SuppressWarnings("serial")
	@Test
	public void test() throws Exception {
		_.setMapper(new ObjectMapper());
		final ExecutorService es = Mockito.mock(ExecutorService.class);
		Mockito.when(es.awaitTermination(Matchers.anyLong(), Matchers.any(TimeUnit.class))).thenReturn(true);
		_.setExecutorService(es);
		//		_.setReceiptHandler(injector.getInstance(ReceiptHandler.class));
		final ReceiptHandler rh = Mockito.mock(ReceiptHandler.class);
		Mockito.when(rh.handle(Matchers.any(Body.class))).thenReturn(new LinkedList<GuessResult>() {

			{
				add(new GuessResult() {

					{
						name = "ALDI";
						distance = 0.5d;
					}
				});
			}
		});
		_.setReceiptHandler(rh);
		final MessageQueue rq = Mockito.mock(MessageQueue.class);
		final MessageQueue rs = Mockito.mock(MessageQueue.class);
		final Message m1 = Mockito.mock(Message.class);
		Mockito.when(m1.getMessageBody()).thenReturn("{\"receipt\":{\"url\":\"my_url\",\"id\":\"999\"}}");
		Mockito.when(rq.receiveMessage()).thenReturn(m1, (Message) null);
		_.setRequestQueue(rq);
		_.setResponseQueue(rs);
		final Message msg = _.popOrWait();
		_.handleRequestMessage(msg);
		Assert.assertEquals(m1, msg);
		Mockito.verify(rq).deleteMessage(m1);
		Mockito.verify(rs)
				.sendMessage(
						"{\"receipt\":{\"id\":\"999\",\"extracted_store_names\":[{\"name\":\"ALDI\",\"distance\":0.5,\"numOfErrors\":0,\"termLength\":0}],\"extracted_tokens_list\":[]}}");
	}
}
