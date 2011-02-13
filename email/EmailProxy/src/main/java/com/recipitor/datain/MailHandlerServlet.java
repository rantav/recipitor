/**
 * NAME: MyServlet.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 9761993 
 */
@Singleton
public class MailHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = -5478907260255062979L;
	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailHandlerServlet.class);
	IMailService mailService;

	//	final AbstractModule businessModule = new AbstractModule() {
	//
	//		@Override
	//		protected void configure() {
	//			bind(IMailExtractor.class).to(MailExtractor.class);
	//			bind(IMailDAO.class).to(MailDAO.class);
	//			bind(IMailService.class).to(MailService.class);
	//		}
	//	};
	//	Injector injector = Guice.createInjector(businessModule);
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		if (LGR.isInfoEnabled()) LGR.info("MyServlet.doGet() - got mail ");
		final boolean showList = req.getParameter("list") != null;
		final String id = req.getParameter("attachment");
		//		setMailService(injector.getInstance(IMailService.class));
		if (showList) mailService.getAllMails(req, resp);
		else if (id != null) mailService.sohwAttachment(id, resp);
		else mailService.onNewMail(req, resp);
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}

	@Inject
	public void setMailService(final IMailService v) {
		mailService = v;
	}
}
