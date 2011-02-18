/**
 * NAME: MailPoster.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author ymaman
 * created: Feb 18, 2011
 * Associated Bugs: 
 */
public class MailPoster implements IMailPoster {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailPoster.class);
	private final String url;
	final static String lineEnd = "\r\n";
	final static String twoHyphens = "--";
	final static String boundary = "---------------------------42669085015852166671501441328";

	@Inject
	public MailPoster(@Named("frontend.url") final String u) {
		url = u;
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.datain.IMailPoster#postMail(com.recipitor.datain.Mail)
	 */
	@Override
	public boolean postMail(final Mail m) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("posting mail [" + m.getId() + "] to frontend [" + url + "]");
		final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
		final HTTPRequest request = new HTTPRequest(new URL(url), HTTPMethod.POST);
		request.addHeader(new HTTPHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		request.addHeader(new HTTPHeader("Accept-Language", "en-us,en;q=0.5"));
		request.addHeader(new HTTPHeader("Accept-Encoding", "gzip,deflate"));
		request.addHeader(new HTTPHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7"));
		request.addHeader(new HTTPHeader("Keep-Alive", "115"));
		request.addHeader(new HTTPHeader("Connection", "keep-alive"));
		request.addHeader(new HTTPHeader("Content-Type", "multipart/form-data; boundary=" + boundary));
		final ByteArrayOutputStream bb = new ByteArrayOutputStream();
		final DataOutputStream dos = new DataOutputStream(bb);
		final InputStream is = TaskHandlerServlet.class.getClassLoader().getResourceAsStream("img.png");
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		final byte[] buff = new byte[1024];
		while (true) {
			final int len = is.read(buff);
			if (len < 0) break;
			bo.write(buff, 0, len);
		}
		is.close();
		//		addPart("utf8", "V", dos);
		addPart("receipt[description]", m.getSubject(), dos);
		addPart("user_email", m.getFrom(), dos);
		final byte[] b = m.getAttachment() == null ? bo.toByteArray() : m.getAttachment().getBytes();
		addPartFile(m.getFileName(), m.getMimeType(), b, dos);
		addFooter(dos);
		dos.flush();
		dos.close();
		final HTTPResponse resp = urlFetchService.fetch(request);
		final BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resp.getContent())));
		String decodedString;
		final StringBuilder sb = new StringBuilder();
		while ((decodedString = in.readLine()) != null)
			sb.append(decodedString);
		if (LGR.isDebugEnabled()) LGR.debug(sb);
		in.close();
		return true;
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
}
