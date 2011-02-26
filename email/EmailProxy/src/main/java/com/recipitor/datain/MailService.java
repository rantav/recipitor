/**
 * NAME: RequestHandler.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public class MailService implements IMailService {

	private static final String QUEUE_NAME = "post-email-queue";
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailService.class);
	private IMailExtractor mailExtractor;
	private IMailDAO mailDAO;

	/**
	 * @see com.recipitor.datain.IMailService#onNewMail(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void onNewMail(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		try {
			if (LGR.isDebugEnabled()) LGR.debug("onNewMail");
			final MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties(), null),
					req.getInputStream());
			final List<Mail> ems = mailExtractor.extract(message);
			if (MyGuiceServletContextListener.isDev && ems.isEmpty()) {
				LGR.debug("in debug mode, send a receipt with default for user [yonatanm@gmail.com]");
				final Mail m = new Mail();
				m.setFrom("yonatanm@gmail.com");
				m.setMessageID("123");
				m.setSentDate(new Date());
				m.setSubject("subject");
				m.setAttachment(null);
				m.setMimeType(null);
				m.setFileName(null);
				ems.add(m);
			}
			sotreAndQue(ems);
		} catch (final MessagingException e) {
			LGR.error("got error [" + e.getMessage() + "]", e);
			return;
		}
	}

	/**
	 * @param ems
	 */
	private void sotreAndQue(final List<Mail> ems) {
		for (final Mail m : ems) {
			mailDAO.addMail(m);
			putInQueue(m);
		}
	}

	/**
	 * @param em
	 */
	private void putInQueue(final Mail em) {
		final Queue queue = QueueFactory.getQueue(QUEUE_NAME);
		final TaskOptions to = TaskOptions.Builder.withUrl("/tasks/post-email").param("id", em.getId().toString())
				.method(Method.GET);
		queue.add(to);
	}

	/**
	 * @see com.recipitor.datain.IMailService#getAllMails(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void getAllMails(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		if (LGR.isDebugEnabled()) LGR.debug("listing all mails");
		final StringBuilder sb = new StringBuilder();
		final List<Mail> ls = mailDAO.listMails();
		sb.append("<h3> There are " + ls.size() + " emails stored</h3>");
		sb.append("<table border='1'>");
		sb.append("<tr>");
		sb.append("<th>db ID</th>");
		sb.append("<th>message ID</th>");
		sb.append("<th>sent Date</th>");
		sb.append("<th>from</th>");
		//		sb.append("<th>subject</th>");
		sb.append("<th>length</th>");
		sb.append("<th>mime type</th>");
		sb.append("<th>attachment</th>");
		sb.append("</tr>");
		for (final Mail m : ls) {
			sb.append("<tr>");
			sb.append("<td>");
			if (!m.getIsActive()) sb.append("<strike>");
			sb.append(m.getId());
			if (!m.getIsActive()) sb.append("</strike>");
			sb.append("</td>");
			sb.append("<td>" + chop(encode(m.getMessageID())) + "</td>");
			sb.append("<td>" + m.getSentDate() + "</td>");
			sb.append("<td>" + encode(m.getFrom()) + "</td>");
			//			sb.append("<td>" + m.getSubject() + "</td>");
			if (m.getAttachment() != null) {
				sb.append("<td>" + m.getAttachment().getBytes().length + "</td>");
				sb.append("<td>" + m.getMimeType() + "</td>");
				sb.append("<td><a href='/_ah/mail/receipt?attachment=" + m.getId() + "'>download</a></th>");
			} else {
				sb.append("<td>NA</td>");
				sb.append("<td>NA</td>");
				sb.append("<td>NA</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		resp.setContentLength(sb.length());
		resp.getWriter().write(sb.toString().toCharArray());
	}

	/**
	 * @param s
	 * @return
	 */
	private String chop(final String s) {
		if (s.length() > 10) return s.substring(0, 10) + "...";
		return s;
	}

	/**
	 * @param messageID
	 * @return
	 */
	private String encode(final String s) {
		if (s == null) return "";
		return s.replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;");
	}

	@Inject
	public void setMailExtractor(final IMailExtractor v) {
		mailExtractor = v;
	}

	@Inject
	public void setEmailDAO(final IMailDAO v) {
		mailDAO = v;
	}

	/**
	 * @throws IOException 
	 * @see com.recipitor.datain.IMailService#sohwAttachment(java.lang.String, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void sohwAttachment(final String id, final HttpServletResponse resp) throws IOException {
		final Mail m = mailDAO.getMail(Long.parseLong(id));
		final byte[] attach = m.getAttachment().getBytes();
		resp.setContentType(m.getMimeType());
		resp.setContentLength(attach.length);
		resp.getOutputStream().write(attach);
	}
}
