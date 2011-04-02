package com.recipitor.datain;

/**
 * NAME: EmailDAO.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
public class MailDAO implements IMailDAO {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(MailDAO.class);
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	//	public static PersistenceManagerFactory getPersistenceManagerFactory() {
	//		return pmfInstance;
	//	}
	public static PersistenceManager getPersistenceManager() {
		return pmfInstance.getPersistenceManager();
	}

	@Override
	public void addMail(final Mail em) {
		em.setAttachment(null);
		final PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(em);
			if (LGR.isInfoEnabled()) LGR.info("successfully storing mail");
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mail> listMails() {
		final PersistenceManager pm = getPersistenceManager();
		final String query = "select from " + Mail.class.getName();
		return (List<Mail>) pm.newQuery(query).execute();
	}

	/**
	 * @see com.recipitor.datain.IMailDAO#getMail(java.lang.Long)
	 */
	@Override
	public Mail getMail(final Long id) {
		final PersistenceManager pm = getPersistenceManager();
		final Key k = KeyFactory.createKey(Mail.class.getSimpleName(), id);
		final Mail m = pm.getObjectById(Mail.class, k);
		return m;
	}

	/**
	 * @see com.recipitor.datain.IMailDAO#deactivateMail(com.recipitor.datain.Mail)
	 */
	@Override
	public void deactivateMail(final Long id) {
		final PersistenceManager pm = getPersistenceManager();
		try {
			final Key k = KeyFactory.createKey(Mail.class.getSimpleName(), id);
			final Mail m = pm.getObjectById(Mail.class, k);
			m.setIsActive(false);
		} finally {
			pm.close();
		}
	}
}
