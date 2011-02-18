/**
 * NAME: TaskHandlerServlet.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author ymaman
 * created: Feb 12, 2011
 * Associated Bugs: 
 */
@Singleton
public class TaskHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = -922779999497398141L;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(TaskHandlerServlet.class);
	private IMailDAO mailDAO;
	private static String DEV_URL = "http://0.0.0.0:3000/emails";
	private String url = DEV_URL;
	final static String lineEnd = "\r\n";
	final static String twoHyphens = "--";
	final static String boundary = "---------------------------42669085015852166671501441328";

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final String id = req.getParameter("id");
		if (LGR.isInfoEnabled()) LGR.info("Task Handler will handle ID [" + id + "]");
		try {
			handle(id);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param id
	 * @throws MalformedURLException 
	 * @throws MessagingException 
	 */
	private void handle(final String id) throws Exception {
		final Mail m = mailDAO.getMail(Long.parseLong(id));
		if (!m.getIsActive()) {
			LGR.error("mail [" + id + "] was supposed to be active...");
			return;
		}
		postMailToFrontEnd(m);
		if (LGR.isDebugEnabled()) LGR.debug("deactiving mail [" + id + "]");
		mailDAO.deactivateMail(m.getId());
	}

	/**
	 * @param m
	 * @throws MessagingException 
	 * @throws MalformedURLException 
	 */
	private void postMailToFrontEnd(final Mail m) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("posting mail [" + m.getId() + "] to frontend ");
		final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
		conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
		conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		conn.setRequestProperty("Keep-Alive", "115");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		final DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		final InputStream is = TaskHandlerServlet.class.getClassLoader().getResourceAsStream("img.png");
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		final byte[] buff = new byte[1024];
		while (true) {
			final int len = is.read(buff);
			if (len < 0) break;
			bo.write(buff, 0, len);
		}
		is.close();
		addPart("utf8", "V", dos);
		addPart("receipt[description]", m.getSubject(), dos);
		addPart("user_email", m.getFrom(), dos);
		final byte[] b = m.getAttachment() == null ? bo.toByteArray() : m.getAttachment().getBytes();
		addPartFile(m.getFileName(), m.getMimeType(), b, dos);
		addFooter(dos);
		dos.flush();
		dos.close();
		final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String decodedString;
		final StringBuilder sb = new StringBuilder();
		while ((decodedString = in.readLine()) != null)
			sb.append(decodedString);
		if (LGR.isDebugEnabled()) LGR.debug(sb);
		in.close();
	}

	/**
	 * @param m
	 * @param dos
	 * @throws IOException
	 */
	private void addPartFile(final String fn, final String mimetype, final byte[] content, final DataOutputStream dos)
			throws IOException {
		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"receipt[img]\";" + " filename=\""
				+ (fn == null ? "myFile.jpg" : fn) + "\"" + lineEnd);
		dos.writeBytes("Content-Type: " + (mimetype == null ? "image/png" : mimetype));
		dos.writeBytes(lineEnd);
		dos.writeBytes(lineEnd);
		dos.write(content);
		dos.writeBytes(lineEnd);
	}

	private void addPart(final String n, final String val, final DataOutputStream dos) throws IOException {
		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"" + n + "\"" + lineEnd);
		dos.writeBytes(lineEnd);
		dos.writeBytes(val);
		dos.writeBytes(lineEnd);
	}

	/**
	 * @param dos
	 * @throws IOException
	 */
	private void addFooter(final DataOutputStream dos) throws IOException {
		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	}

	@Inject
	public void setEmailDAO(final IMailDAO v) {
		mailDAO = v;
	}

	public void setUrl(final String v) {
		url = v;
	}
}
