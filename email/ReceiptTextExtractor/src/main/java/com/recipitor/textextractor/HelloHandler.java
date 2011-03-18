package com.recipitor.textextractor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.inject.Inject;

public class HelloHandler extends AbstractHandler {

	QueueListener queueListener;

	/**
	 * @param val the queueListener to set
	 * @throws Exception 
	 */
	@Inject
	public void setQueueListener(final QueueListener val) throws Exception {
		queueListener = val;
		queueListener.listen();
	}

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Hello World</h1>");
	}
}