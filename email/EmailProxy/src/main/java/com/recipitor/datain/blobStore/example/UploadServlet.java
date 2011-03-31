/**
 * NAME: UploadServlet.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain.blobStore.example;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * @author ymaman
 * created: Mar 30, 2011
 * Associated Bugs: 
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 3965709259582877792L;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(UploadServlet.class);
	private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	@SuppressWarnings("unchecked")
	public void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException,
			IOException {
		final Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		final BlobKey blobKey = blobs.get("file1");
		//You can save blobkey information by JDO if required.
		final String url = "http://localhost:8888/serve?blob-key=" + blobKey.getKeyString();
		System.out.println(url);
		res.sendRedirect("/upload.jsp?message="
				+ URLEncoder.encode("Uploaded: /serve?blob-key=" + blobKey.getKeyString(), "utf-8"));
		//		res.getOutputStream().print("<html><h1>done " + url + "</hr></html>");
	}
}
