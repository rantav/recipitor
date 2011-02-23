/**
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.apache.geronimo.mail.util.Base64DecoderStream;
import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Blob;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public class MailExtractor implements IMailExtractor {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailExtractor.class);
	final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4})",
			Pattern.CASE_INSENSITIVE);

	private class ReceiptData {

		/**
		 * @param fv
		 * @param idv
		 * @param sdV
		 * @param sv
		 */
		public ReceiptData(final byte[] d, final String fnv, final String ct) {
			data = d;
			fileName = fnv;
			contentType = ct;
		}

		public final byte[] data;
		public final String fileName;
		public final String contentType;
	}

	/**
	 * @see com.recipitor.datain.IMailExtractor#extract(javax.mail.internet.MimeMessage)
	 */
	@Override
	public List<Mail> extract(final MimeMessage message) throws MessagingException, IOException {
		final List<Mail> $ = new LinkedList<Mail>();
		final String from = message.getFrom() != null && message.getFrom().length > 0 ? message.getFrom()[0].toString()
				: "";
		final Object content = message.getContent();
		//		if (content instanceof String) LGR.info("content is String [" + content +]");
		//		else 
		if (content instanceof Multipart) {
			LGR.info("found Multipart");
			final List<ReceiptData> rd = handleMultipart((Multipart) content);
			for (final ReceiptData r : rd) {
				final Mail m = new Mail();
				m.setFrom(extractSender(from));
				m.setMessageID(message.getMessageID());
				m.setSentDate(message.getSentDate());
				m.setSubject(message.getSubject());
				m.setAttachment(new Blob(r.data));
				m.setMimeType(r.contentType);
				m.setFileName(r.fileName);
				$.add(m);
			}
		}
		LGR.info("extracted " + $.size() + " attachments");
		for (final Mail m : $)
			if (LGR.isInfoEnabled()) LGR.info("email extracted " + m.toString());
		return $;
	}

	/**
	 * @param content
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	private ReceiptData handleEmbeddedImage(final BodyPart bp) throws IOException, MessagingException
	//Base64DecoderStream content) throws IOException 
	{
		LGR.info("handling bas64...");
		final Base64DecoderStream base64DecoderStream = (Base64DecoderStream) bp.getContent();
		final byte[] data = toByteArray(base64DecoderStream);
		//		final byte[] encodeBase64 = Base64.encode(data);
		//		final String s = new String(encodeBase64, "UTF-8");
		final ReceiptData $ = buildReceiptData(bp, data);
		return $;
	}

	private List<ReceiptData> handleMultipart(final Multipart mp) throws MessagingException, IOException {
		final List<ReceiptData> $ = new LinkedList<ReceiptData>();
		final int count = mp.getCount();
		LGR.info("handleMultipart begin - there are [" + count + "] parts ");
		for (int i = 0; i < count; i++) {
			final BodyPart bp = mp.getBodyPart(i);
			final Object content = bp.getContent();
			if (content == null) continue;
			LGR.info("content class is [" + content.getClass().getCanonicalName() + "]");
			if (content instanceof String) LGR.info("content is String [" + content + "]");
			if (content instanceof Base64DecoderStream) $.add(handleEmbeddedImage(bp));
			//			if (content instanceof InputStream) $.add(handleAttachment(bp));
			else if (content instanceof Multipart) {
				LGR.info("found an inner Multipart - recurssive");
				$.addAll(handleMultipart((Multipart) content));
			}
			//			else if (content instanceof Message) {
			//				LGR.info("content is Message");
			//				handleMessage();
			//				}
		}
		LGR.info("handleMultipart end");
		return $;
	}

	//	private ReceiptData handleAttachment(final BodyPart bp) throws IOException, MessagingException {
	//		LGR.info("handling attachment ");
	//		final InputStream is = (InputStream) bp.getContent();
	//		final byte[] data = toByteArray(is);
	//		final ReceiptData $ = buildReceiptData(bp, data);
	//		return $;
	//	}
	/**
	 * @param bp
	 * @param data
	 * @return
	 * @throws MessagingException
	 */
	private ReceiptData buildReceiptData(final BodyPart bp, final byte[] data) throws MessagingException {
		final String ct = extractContentType(bp);
		final ReceiptData $ = new ReceiptData(data, bp.getFileName(), ct);
		return $;
	}

	/**
	 * @param bp
	 * @return
	 * @throws MessagingException
	 */
	private String extractContentType(final BodyPart bp) throws MessagingException {
		String ct = bp.getContentType();
		final String contentType = bp.getContentType();
		final int i = contentType.indexOf(';');
		if (i > 0) ct = contentType.substring(0, i);
		else ct = contentType;
		return ct;
	}

	//	byte[] toByteArray(final InputStream is) throws IOException {
	//		final byte[] buff = new byte[1024];
	//		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//		while (true) {
	//			final int len = is.read(buff);
	//			if (len <= 0) break;
	//			bos.write(buff, 0, len);
	//		}
	//		return bos.toByteArray();
	//	}
	/**
	 * @param string
	 * @return
	 */
	public String extractSender(final String a) {
		final Matcher m = EMAIL_ADDRESS_PATTERN.matcher(a);
		m.find();
		return m.groupCount() > 0 ? m.group(1) : "";
	}

	public static byte[] toByteArray(final InputStream input) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static int copy(final InputStream input, final OutputStream output) throws IOException {
		final long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) return -1;
		return (int) count;
	}

	public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
}
