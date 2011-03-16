/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor;

import org.apache.log4j.Logger;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Receipt {

	@SuppressWarnings("unused")
	private static Logger LGR = Logger.getLogger(Receipt.class);
	String img_updated_at;
	Long img_file_size;
	String extracted_store_name;
	String created_at;
	String updated_at;
	String id;
	String user_id;
	String img_file_name;
	String description;
	String img_content_type;

	public Receipt() {
		// must have an empty constructor
	}

	/**
	 * @return the img_updated_at
	 */
	public String getImg_updated_at() {
		return img_updated_at;
	}

	/**
	 * @param img_updated_at the img_updated_at to set
	 */
	public void setImg_updated_at(final String img_updated_at) {
		this.img_updated_at = img_updated_at;
	}

	/**
	 * @return the img_file_size
	 */
	public Long getImg_file_size() {
		return img_file_size;
	}

	/**
	 * @param img_file_size the img_file_size to set
	 */
	public void setImg_file_size(final Long img_file_size) {
		this.img_file_size = img_file_size;
	}

	/**
	 * @return the extracted_store_name
	 */
	public String getExtracted_store_name() {
		return extracted_store_name;
	}

	/**
	 * @param extracted_store_name the extracted_store_name to set
	 */
	public void setExtracted_store_name(final String extracted_store_name) {
		this.extracted_store_name = extracted_store_name;
	}

	/**
	 * @return the created_at
	 */
	public String getCreated_at() {
		return created_at;
	}

	/**
	 * @param created_at the created_at to set
	 */
	public void setCreated_at(final String created_at) {
		this.created_at = created_at;
	}

	/**
	 * @return the updated_at
	 */
	public String getUpdated_at() {
		return updated_at;
	}

	/**
	 * @param updated_at the updated_at to set
	 */
	public void setUpdated_at(final String updated_at) {
		this.updated_at = updated_at;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(final String user_id) {
		this.user_id = user_id;
	}

	/**
	 * @return the img_file_name
	 */
	public String getImg_file_name() {
		return img_file_name;
	}

	/**
	 * @param img_file_name the img_file_name to set
	 */
	public void setImg_file_name(final String img_file_name) {
		this.img_file_name = img_file_name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the img_content_type
	 */
	public String getImg_content_type() {
		return img_content_type;
	}

	/**
	 * @param img_content_type the img_content_type to set
	 */
	public void setImg_content_type(final String img_content_type) {
		this.img_content_type = img_content_type;
	}
}
