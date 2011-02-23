/**
 * NAME: IEmailChecker.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public interface IMailExtractor {

	public List<Mail> extract(MimeMessage message) throws MessagingException, IOException;
}
