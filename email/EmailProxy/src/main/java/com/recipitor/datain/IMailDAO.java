/**
 * NAME: IEmailDAO.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.util.List;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public interface IMailDAO {

	void addMail(Mail contact);

	Mail getMail(Long id);

	List<Mail> listMails();

	/**
	 * @param id
	 */
	void deactivateMail(Long id);
}
