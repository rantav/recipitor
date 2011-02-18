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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

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

	/**
	 * @see com.recipitor.datain.IMailExtractor#extract(javax.mail.internet.MimeMessage)
	 */
	@Override
	public Mail extract(final MimeMessage message) throws MessagingException, IOException {
		final Mail mail = new Mail();
		mail.setIsActive(true);
		final String from = message.getFrom() != null && message.getFrom().length > 0 ? message.getFrom()[0].toString()
				: "";
		mail.setFrom(extractSender(from));
		mail.setMessageID(message.getMessageID());
		mail.setSentDate(message.getSentDate());
		mail.setSubject(message.getSubject());
		final Object content = message.getContent();
		if (content instanceof String) LGR.info("content is String [" + content + "]");
		else if (content instanceof Multipart) {
			LGR.info("found Multipart");
			handleMultipart(mail, (Multipart) content);
		}
		if (LGR.isInfoEnabled()) LGR.info("email extracted " + mail.toString());
		return mail;
	}

	private void handleMultipart(final Mail m, final Multipart mp) throws MessagingException, IOException {
		final int count = mp.getCount();
		LGR.info("handleMultipart begin - there are [" + count + "] parts ");
		for (int i = 0; i < count; i++) {
			final BodyPart bp = mp.getBodyPart(i);
			final Object content = bp.getContent();
			if (content == null) continue;
			//LGR.info("content class is [" + content.getClass().getCanonicalName() + "]");
			if (content instanceof String) LGR.info("content is String [" + content + "]");
			if (content instanceof InputStream) {
				LGR.info("content is InputStream probabaly an attachment");
				if (m.getAttachment() != null) LGR.warn("an attachment was identified alreasy. skip this one.");
				handleAttachment(m, bp);
			} else if (content instanceof Multipart) {
				LGR.info("found an inner Multipart - recurssive");
				handleMultipart(m, (Multipart) content);
			}
			//			else if (content instanceof Message) {
			//				LGR.info("content is Message");
			//				handleMessage();
			//				}
		}
		LGR.info("handleMultipart end");
	}

	//	private void handleMessage() {
	//		LGR.info("handling message");
	//	}
	private void handleAttachment(final Mail m, final BodyPart bp) throws IOException, MessagingException {
		LGR.info("handling attachment ");
		m.setFileName(bp.getFileName());
		final InputStream is = (InputStream) bp.getContent();
		m.setMimeType(bp.getContentType());
		final String contentType = bp.getContentType();
		final int i = contentType.indexOf(';');
		if (i > 0) m.setMimeType(contentType.substring(0, i));
		else m.setMimeType(contentType);
		m.setAttachment(new Blob(toByteArray(is)));
	}

	byte[] toByteArray(final InputStream is) throws IOException {
		final byte[] buff = new byte[1024];
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while (true) {
			final int len = is.read(buff);
			if (len <= 0) break;
			bos.write(buff, 0, len);
		}
		return bos.toByteArray();
	}

	/**
	 * @param string
	 * @return
	 */
	public String extractSender(final String a) {
		final Matcher m = EMAIL_ADDRESS_PATTERN.matcher(a);
		m.find();
		return m.groupCount() > 0 ? m.group(1) : "";
	}
}
