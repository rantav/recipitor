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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

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
	private String apikey = "no_secret";
	final static String lineEnd = "\r\n";
	final static String twoHyphens = "--";
	String boundary = "---------------------------42669085015852166671501441328";

	@Inject
	public MailPoster(@Named("frontend.url") final String u, @Named("frontend.apikey") final String a,
			@Named("boundary") final String b) {
		url = u;
		apikey = a;
		boundary = b;
	}

	/**
	 * @throws Exception 
	 * @see com.recipitor.datain.IMailPoster#postMail(com.recipitor.datain.Mail)
	 */
	@Override
	public boolean postMail(final Mail m) throws Exception {
		if (LGR.isDebugEnabled()) LGR.debug("posting mail [" + m.getId() + "] to frontend [" + url + "]");
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
		addPart("apikey", apikey, dos);
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
		if (LGR.isDebugEnabled()) LGR.debug("got response:\n" + sb);
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
		LGR.info("adding a new file to the post request with name [" + fn + "]");
		//goPhoto.2011.2.21-23_0_19.jpg
		String f = fn;
		if (fn != null) {
			f = fn.replaceAll("\\.", "_");
			final int i = f.lastIndexOf('_');
			if (i >= 1) f = f.substring(0, i) + "." + f.substring(i + 1);
			LGR.info("@@@ new file to the post request with name [" + f + "]");
		} else f = "myFile.jpg";
		final String m = mimetype == null ? "image/png" : mimetype;
		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"receipt[img]\";" + " filename=\"" + f + "\"" + lineEnd);
		dos.writeBytes("Content-Type: " + m);
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
