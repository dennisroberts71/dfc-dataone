/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * 
 * @author mcc
 *
 */
@Entity
@Table(name = "access_log")
public class AccessLog {

	@Id
	@SequenceGenerator(name = "access_log_id_seq", sequenceName = "access_log_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_log_id_seq")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "file_key", length = 1024, nullable = false)
	private String fileKey;

	@Column(name = "event_id", length = 30, nullable = true)
	private String eventId;

	@Column(name = "event_detail", length = 200, nullable = true)
	private String eventDetail;

	@Column(name = "access_principal", length = 50, nullable = true)
	private String accessPrincipal;

	@Column(name = "date_added")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date dateAdded;

	/**
	 * 
	 */
	public AccessLog() {
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the fileKey
	 */
	public String getFileKey() {
		return fileKey;
	}

	/**
	 * @param fileKey
	 *            the fileKey to set
	 */
	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 *            the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eventDetail
	 */
	public String getEventDetail() {
		return eventDetail;
	}

	/**
	 * @param eventDetail
	 *            the eventDetail to set
	 */
	public void setEventDetail(String eventDetail) {
		this.eventDetail = eventDetail;
	}

	/**
	 * @return the accessPrincipal
	 */
	public String getAccessPrincipal() {
		return accessPrincipal;
	}

	/**
	 * @param accessPrincipal
	 *            the accessPrincipal to set
	 */
	public void setAccessPrincipal(String accessPrincipal) {
		this.accessPrincipal = accessPrincipal;
	}

	/**
	 * @return the dateAdded
	 */
	public Date getDateAdded() {
		return dateAdded;
	}

	/**
	 * @param dateAdded
	 *            the dateAdded to set
	 */
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccessLog [");
		if (id != null) {
			builder.append("id=").append(id).append(", ");
		}
		if (fileKey != null) {
			builder.append("fileKey=").append(fileKey).append(", ");
		}
		if (eventId != null) {
			builder.append("eventId=").append(eventId).append(", ");
		}
		if (eventDetail != null) {
			builder.append("eventDetail=").append(eventDetail).append(", ");
		}
		if (accessPrincipal != null) {
			builder.append("accessPrincipal=").append(accessPrincipal).append(", ");
		}
		if (dateAdded != null) {
			builder.append("dateAdded=").append(dateAdded);
		}
		builder.append("]");
		return builder.toString();
	}

}
