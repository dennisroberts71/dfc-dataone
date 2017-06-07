/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * 
 * @author mcc
 *
 */
@Entity
@Table(name = "event_log")
public class AccessLog {

	@Id
	// @GeneratedValue(generator = "entityIdGenerator")
	// @GenericGenerator(name = "entityIdGenerator", strategy = "uuid2")
	@Column(columnDefinition = "entry_id", nullable = false)
	private String entryId;

	@Column(columnDefinition = "permanent_id", nullable = true)
	private String permanentId;

	@Column(columnDefinition = "ip_address", nullable = true)
	private String ipAddress;

	@Column(columnDefinition = "irods_path", nullable = true)
	private String irodsPath;

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
	 * 
	 * 
	 * /**
	 * 
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
	 * @return the entryId
	 */
	public String getEntryId() {
		return entryId;
	}

	/**
	 * @param entryId
	 *            the entryId to set
	 */
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	/**
	 * @return the permanentId
	 */
	public String getPermanentId() {
		return permanentId;
	}

	/**
	 * @param permanentId
	 *            the permanentId to set
	 */
	public void setPermanentId(String permanentId) {
		this.permanentId = permanentId;
	}

	/**
	 * @return the irodsPath
	 */
	public String getIrodsPath() {
		return irodsPath;
	}

	/**
	 * @param irodsPath
	 *            the irodsPath to set
	 */
	public void setIrodsPath(String irodsPath) {
		this.irodsPath = irodsPath;
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
		if (irodsPath != null) {
			builder.append("irodsPath=").append(irodsPath).append(", ");
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
