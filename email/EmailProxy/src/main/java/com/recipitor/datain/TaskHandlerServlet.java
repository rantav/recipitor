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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
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
	private IMailPoster mailPoster;

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
		deleteFile(m.getFilePath());
	}

	/**
	 * @param filePath
	 */
	private void deleteFile(final String filePath) {
		LGR.debug("about to delete file [" + filePath + "]");
		final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		final AppEngineFile file = new AppEngineFile(filePath);
		final FileService fileService = FileServiceFactory.getFileService();
		// Now read from the file using the Blobstore API
		final BlobKey blobKey = fileService.getBlobKey(file);
		blobstoreService.delete(blobKey);
		LGR.debug("delete -- done");
	}

	/**
	 * @param m
	 * @throws MessagingException 
	 * @throws MalformedURLException 
	 */
	private void postMailToFrontEnd(final Mail m) throws Exception {
		LGR.debug("about to featch attachment [" + m.getFilePath() + "]");
		final AppEngineFile file = new AppEngineFile(m.getFilePath());
		final FileService fileService = FileServiceFactory.getFileService();
		LGR.debug("size is about to be [" + m.getSize() + "]");
		// Now read from the file using the Blobstore API
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
		final byte[] data = bos.toByteArray();
		m.setAttachment(new Blob(data));
		LGR.debug("done with blob stuff going to post its size is  " + data.length + " (" + m.getSize() + ")");
		mailPoster.postMail(m);
	}

	@Inject
	public void setMailPoster(final IMailPoster v) {
		mailPoster = v;
	}

	@Inject
	public void setEmailDAO(final IMailDAO v) {
		mailDAO = v;
	}
}
