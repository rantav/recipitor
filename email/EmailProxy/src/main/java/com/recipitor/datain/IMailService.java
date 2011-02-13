/**
 * NAME: IRequestHandler.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public interface IMailService {

	@SuppressWarnings("unused")
	public void onNewMail(final HttpServletRequest req, final HttpServletResponse resp) throws IOException;

	public void getAllMails(final HttpServletRequest req, final HttpServletResponse resp) throws IOException;

	/**
	 * @param attachmentId
	 * @param resp
	 * @throws IOException 
	 */
	public void sohwAttachment(String attachmentId, HttpServletResponse resp) throws IOException;
}
