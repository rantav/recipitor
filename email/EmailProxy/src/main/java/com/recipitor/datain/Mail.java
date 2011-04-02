/**
 * NAME: Email.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.datain;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

//import org.apache.log4j.Logger;
/**
 * @author ymaman
 * created: Feb 11, 2011
 * Associated Bugs: 
 */
@PersistenceCapable()
public class Mail implements Serializable {

	private static final long serialVersionUID = -199868736678252841L;
	//	@SuppressWarnings("unused")
	//	private static Logger LGR = Logger.getLogger(Email.class);
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String from;
	@Persistent
	private String subject;
	@Persistent
	private Date sentDate;
	@Persistent
	private String mimeType;
	private Blob attachment;
	@Persistent
	private String filePath;
	@Persistent
	private String fileName;
	@Persistent
	private String messageID;
	@Persistent
	private Boolean isActive = true;
	@Persistent
	private Long size;

	public Mail(final String fv, final String cs, final Date dv, final String mv, final Blob av, final String midV,
			final Boolean iaV, final String vFn, final String vFp, final Long vSi) {
		super();
		from = fv;
		subject = cs;
		sentDate = dv;
		mimeType = mv;
		attachment = av;
		messageID = midV;
		isActive = iaV;
		fileName = vFn;
		filePath = vFp;
		setSize(vSi);
	}

	public Date getSentDate() {
		return sentDate;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Mail() {
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long v) {
		id = v;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(final String v) {
		from = v;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String v) {
		subject = v;
	}

	public Blob getAttachment() {
		return attachment;
	}

	public void setAttachment(final Blob v) {
		attachment = v;
	}

	public void setSentDate(final Date v) {
		sentDate = v;
	}

	public void setMimeType(final String v) {
		mimeType = v;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(final String v) {
		messageID = v;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(final Boolean v) {
		isActive = v;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String v) {
		fileName = v;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("active [" + isActive + "] ");
		sb.append("id [" + id + "] ");
		sb.append("messageId [" + messageID + "] ");
		sb.append("from [" + from + "] ");
		sb.append("subject [" + subject + "] ");
		sb.append("sentDate[" + sentDate + "] ");
		sb.append("mimeType [" + mimeType + "] ");
		sb.append("hasAttachment [" + (attachment != null) + "]");
		sb.append("fileName [" + fileName + "]");
		sb.append("filePath [" + filePath + "]");
		return sb.toString();
	}

	/**
	 * @param v the filePath to set
	 */
	public void setFilePath(final String v) {
		filePath = v;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param v the size to set
	 */
	public void setSize(final Long v) {
		this.size = v;
	}

	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}
}
