/**
 * NAME: IMailPoster.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

/**
 * @author ymaman
 * created: Feb 18, 2011
 * Associated Bugs: 
 */
public interface IMailPoster {

	boolean postMail(Mail m) throws Exception;
}
