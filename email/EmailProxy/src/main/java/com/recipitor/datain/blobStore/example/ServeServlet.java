/**
 * NAME: ServeServlet.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain.blobStore.example;

import java.io.IOException;

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
@SuppressWarnings("serial")
public class ServeServlet extends HttpServlet {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(ServeServlet.class);
	private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
		final BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		blobstoreService.serve(blobKey, res);
	}
}
