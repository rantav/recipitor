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

import java.io.IOException;

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

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final String id = req.getParameter("id");
		if (LGR.isInfoEnabled()) LGR.info("Task Handler will handle ID [" + id + "]");
		handle(id);
	}

	/**
	 * @param id
	 */
	private void handle(final String id) {
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
	 */
	private void postMailToFrontEnd(final Mail m) {
		if (LGR.isDebugEnabled()) LGR.debug("posting mail [" + m.getId() + "] to frontend ");
	}

	@Inject
	public void setEmailDAO(final IMailDAO v) {
		mailDAO = v;
	}
}
