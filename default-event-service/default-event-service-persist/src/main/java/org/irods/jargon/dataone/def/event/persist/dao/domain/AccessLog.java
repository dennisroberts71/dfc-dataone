/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * 
 * @author mcc
 *
 */
@Entity
@Table(name = "access_log")
public class AccessLog {

	@Id
	@GeneratedValue(generator = "entityIdGenerator")
	@GenericGenerator(name = "entityIdGenerator", strategy = "uuid2")
	@Column(columnDefinition = "entry_id", updatable = false)
	@Type(type = "pg-uuid")
	private UUID entryId;

	@Column(columnDefinition = "permanent_id", nullable = true)
	@Type(type = "pg-uuid")
	private UUID permanentId;

	@Column(columnDefinition = "ip_address", nullable = true)
	private String ipAddress;

	@Column(columnDefinition = "user_agent", nullable = true)
	private String userAgent;

	@Column(columnDefinition = "subject", nullable = true)
	private String subject;

	@Column(columnDefinition = "event_type", nullable = false)
	private String eventType;

	@Column(name = "date_logged")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date dateAdded;

	@Column(columnDefinition = "node_identifier", nullable = false)
	private String nodeIdentifier;

	/**
	 * 
	 */
	public AccessLog() {
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

	/**
	 * @return the entryId
	 */
	public UUID getEntryId() {
		return entryId;
	}

	/**
	 * @param entryId
	 *            the entryId to set
	 */
	public void setEntryId(UUID entryId) {
		this.entryId = entryId;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the nodeIdentifier
	 */
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	/**
	 * @param nodeIdentifier
	 *            the nodeIdentifier to set
	 */
	public void setNodeIdentifier(String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	/**
	 * @return the permanentId
	 */
	public UUID getPermanentId() {
		return permanentId;
	}

	/**
	 * @param permanentId
	 *            the permanentId to set
	 */
	public void setPermanentId(UUID permanentId) {
		this.permanentId = permanentId;
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
		if (entryId != null) {
			builder.append("entryId=").append(entryId).append(", ");
		}
		if (permanentId != null) {
			builder.append("permanentId=").append(permanentId).append(", ");
		}
		if (ipAddress != null) {
			builder.append("ipAddress=").append(ipAddress).append(", ");
		}
		if (userAgent != null) {
			builder.append("userAgent=").append(userAgent).append(", ");
		}
		if (subject != null) {
			builder.append("subject=").append(subject).append(", ");
		}
		if (eventType != null) {
			builder.append("eventType=").append(eventType).append(", ");
		}
		if (dateAdded != null) {
			builder.append("dateAdded=").append(dateAdded).append(", ");
		}
		if (nodeIdentifier != null) {
			builder.append("nodeIdentifier=").append(nodeIdentifier);
		}
		builder.append("]");
		return builder.toString();
	}

}
