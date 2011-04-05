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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
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
	private static final long ONE_MB = 1024 * 1024;
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
			if (LGR.isDebugEnabled()) LGR.debug("onNewMail blobStroe support");
			final MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties(), null),
					req.getInputStream());
			final List<Mail> ems = mailExtractor.extract(message);
			if (MyGuiceServletContextListener.isDev && ems.isEmpty()) {
				final String rn = "ReceiptSwiss.jpg";
				LGR.debug("in debug mode, send a receipt with default for user [yonatanm@gmail.com]");
				final Mail m = new Mail();
				m.setFrom("yonatanm@gmail.com");
				m.setMessageID("123");
				m.setSentDate(new Date());
				m.setSubject("subject");
				final ByteArrayOutputStream bo = loadSampleImage(rn);
				final byte[] content = bo.toByteArray();
				m.setAttachment(new Blob(content));
				m.setSize((long) content.length);
				m.setMimeType("image/jpeg");
				m.setFileName(rn);
				ems.add(m);
			}
			sotreAndQue(ems);
		} catch (final Throwable e) {
			e.printStackTrace();
			LGR.error("got error [" + e.getMessage() + "]", e);
			return;
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private ByteArrayOutputStream loadSampleImage(final String rn) throws IOException {
		final InputStream is = TaskHandlerServlet.class.getClassLoader().getResourceAsStream(rn);
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		final byte[] buff = new byte[1024];
		while (true) {
			final int len = is.read(buff);
			if (len < 0) break;
			bo.write(buff, 0, len);
		}
		is.close();
		return bo;
	}

	/**
	 * @param ems
	 * @throws IOException 
	 */
	private void sotreAndQue(final List<Mail> ems) throws Throwable {
		for (final Mail m : ems) {
			LGR.debug("cp 0");
			reduceImageSize(m);
			LGR.debug("cp 0.5");
			putInBlobstore(m);
			LGR.debug("cp 1");
			mailDAO.addMail(m);
			LGR.debug("cp 2");
			putInQueue(m);
			LGR.debug("cp 3");
		}
	}

	/**
	 * @param m
	 */
	private void reduceImageSize(final Mail m) {
		final byte[] oldDate = m.getAttachment().getBytes();
		final int oldSize = oldDate.length;
		LGR.debug("orig size is " + oldSize);
		if (oldSize < ONE_MB) {
			LGR.debug("less than 1MB -  no need to reduce size");
			return;
		}
		final ImagesService imagesService = ImagesServiceFactory.getImagesService();
		final Image oldImage = ImagesServiceFactory.makeImage(oldDate);
		LGR.debug("orig dimention is " + oldImage.getWidth() + " X " + oldImage.getHeight());
		final OutputSettings os = new OutputSettings(ImagesService.OutputEncoding.JPEG);
		boolean fixed = false;
		for (int i = 0; i < 5; i++) {
			final int q = 100 - 5 * i;
			final double ratio = Math.sqrt((double) (oldSize * (i + 1)) / ONE_MB);
			final Transform resize = ImagesServiceFactory.makeResize((int) (oldImage.getWidth() / ratio),
					(int) (oldImage.getHeight() / ratio));
			LGR.debug("try with quality [" + q + "] and ratio [" + ratio + "]");
			os.setQuality(q);
			final Image newImage = imagesService.applyTransform(resize, oldImage, os);
			final byte[] newImageData = newImage.getImageData();
			final int newImageLength = newImageData.length;
			if (newImageLength <= ONE_MB) {
				LGR.debug("reducing size to " + newImageLength);
				m.setSize((long) newImageData.length);
				m.setAttachment(new Blob(newImageData));
				fixed = true;
				break;
			}
		}
		if (!fixed) throw new RuntimeException("could not reduced image ");
	}

	/**
	 * @param attachment
	 * @throws IOException 
	 */
	private void putInBlobstore(final Mail m) throws Throwable {
		LGR.debug("about to write the attachment into blob strore");
		final FileService fileService = FileServiceFactory.getFileService();
		final AppEngineFile file = fileService.createNewBlobFile(m.getMimeType());
		final String fp = file.getFullPath();
		m.setFilePath(fp);
		putChunks(fp, m.getAttachment().getBytes());
		LGR.debug("done with blobstore");
	}

	/**
	 * @param fp
	 * @param attachment
	 * @throws Throwable 
	 */
	private void putChunks(final String fp, final byte[] data) throws Throwable {
		try {
			LGR.debug("total size is [" + data.length + "]");
			final FileService fileService = FileServiceFactory.getFileService();
			final long dataLen = data.length;
			FileWriteChannel writeChannel = null;
			int pos = 0;
			final AppEngineFile file = new AppEngineFile(fp);
			final boolean lock = true; //hm == dataLen - pos;
			writeChannel = fileService.openWriteChannel(file, lock);
			LGR.debug("lock is [" + lock + "]");
			while (pos < dataLen) {
				final int hm = (int) Math.min(dataLen - pos, 800000);
				LGR.debug("about to store " + hm + " bytes, starting from " + pos + " ");
				final int actual = writeChannel.write(ByteBuffer.wrap(data, pos, hm));
				LGR.debug(actual + " bytes were written actually");
				pos += actual;
			}
			if (writeChannel != null) writeChannel.closeFinally();
		} catch (final Throwable t) {
			t.printStackTrace();
			throw t;
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
		sb.append("<th>path</th>");
		//		sb.append("<th>subject</th>");
		sb.append("<th>size</th>");
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
			sb.append("<td>" + m.getFilePath() + "</td>");
			sb.append("<td>" + m.getSize() + "</td>");
			//			sb.append("<td>" + m.getSubject() + "</td>");
			if (m.getSize() != null) {
				sb.append("<td>" + m.getMimeType() + "</td>");
				sb.append("<td><a href='/_ah/mail/receipt?attachment=" + m.getId() + "'>download</a></th>");
			} else {
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
		final AppEngineFile file = new AppEngineFile(m.getFilePath());
		final FileService fileService = FileServiceFactory.getFileService();
		LGR.debug("size is about to be [" + m.getSize() + "]");
		final BlobKey blobKey = fileService.getBlobKey(file);
		final BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int pos = 0;
		while (pos < m.getSize()) {
			final long chunkSize = Math.min(m.getSize() - pos, 800000);
			final byte[] chunkData = blobStoreService.fetchData(blobKey, pos, pos + chunkSize);
			bos.write(chunkData);
			pos += chunkData.length;
		}
		bos.flush();
		bos.close();
		final byte[] data = bos.toByteArray();
		LGR.debug("data size is " + data.length);
		resp.setContentType(m.getMimeType());
		resp.setContentLength(data.length);
		resp.getOutputStream().write(data);
	}
}
