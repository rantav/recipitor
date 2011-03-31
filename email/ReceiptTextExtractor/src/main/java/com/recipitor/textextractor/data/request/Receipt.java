/**
 * NAME: Receipt.java
 * 
 * DESCRIPTION: 
 *		
 *
 * ---------------------------------------------------------------------------------
 * 
 */
package com.recipitor.textextractor.data.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ymaman
 * created: Mar 17, 2011
 * Associated Bugs: 
 */
public class Receipt {

	@SuppressWarnings("unused")
	private static Logger LGR = LoggerFactory.getLogger(Receipt.class);
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
	String url;
	String extracted_store_name_raw_json;

	/**
	 * @return the extracted_store_name_raw_json
	 */
	public String getExtracted_store_name_raw_json() {
		return extracted_store_name_raw_json;
	}

	/**
	 * @param v the extracted_store_name_raw_json to set
	 */
	public void setExtracted_store_name_raw_json(final String v) {
		this.extracted_store_name_raw_json = v;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param u the url to set
	 */
	public void setUrl(final String u) {
		url = u;
	}

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
	 * @param v the img_updated_at to set
	 */
	public void setImg_updated_at(final String v) {
		img_updated_at = v;
	}

	/**
	 * @return the img_file_size
	 */
	public Long getImg_file_size() {
		return img_file_size;
	}

	/**
	 * @param v the img_file_size to set
	 */
	public void setImg_file_size(final Long v) {
		img_file_size = v;
	}

	/**
	 * @return the extracted_store_name
	 */
	public String getExtracted_store_name() {
		return extracted_store_name;
	}

	/**
	 * @param v the extracted_store_name to set
	 */
	public void setExtracted_store_name(final String v) {
		extracted_store_name = v;
	}

	/**
	 * @return the created_at
	 */
	public String getCreated_at() {
		return created_at;
	}

	/**
	 * @param v the created_at to set
	 */
	public void setCreated_at(final String v) {
		created_at = v;
	}

	/**
	 * @return the updated_at
	 */
	public String getUpdated_at() {
		return updated_at;
	}

	/**
	 * @param v the updated_at to set
	 */
	public void setUpdated_at(final String v) {
		updated_at = v;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param v the id to set
	 */
	public void setId(final String v) {
		id = v;
	}

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param v the user_id to set
	 */
	public void setUser_id(final String v) {
		user_id = v;
	}

	/**
	 * @return the img_file_name
	 */
	public String getImg_file_name() {
		return img_file_name;
	}

	/**
	 * @param v the img_file_name to set
	 */
	public void setImg_file_name(final String v) {
		img_file_name = v;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param v the description to set
	 */
	public void setDescription(final String v) {
		description = v;
	}

	/**
	 * @return the img_content_type
	 */
	public String getImg_content_type() {
		return img_content_type;
	}

	/**
	 * @param v the img_content_type to set
	 */
	public void setImg_content_type(final String v) {
		img_content_type = v;
	}
}
