/**
 * 
 */
package org.irods.jargon.dataone.events;

import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;

/**
 * Represents potentially available event data to be logged
 * 
 * @author mcc
 *
 */
public class EventData {

	private Event event;
	private Identifier id;
	private String description = "";
	private String irodsPath = "";
	private String userAgent = "";
	private String subject = "";
	private String ipAddress = "";
	private String nodeIdentifier = "";

	/**
	 * 
	 */
	public EventData() {
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the id
	 */
	public Identifier getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Identifier id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventData [");
		if (event != null) {
			builder.append("event=").append(event).append(", ");
		}
		if (id != null) {
			builder.append("id=").append(id).append(", ");
		}
		if (description != null) {
			builder.append("description=").append(description).append(", ");
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
		if (ipAddress != null) {
			builder.append("ipAddress=").append(ipAddress).append(", ");
		}
		if (nodeIdentifier != null) {
			builder.append("nodeIdentifier=").append(nodeIdentifier);
		}
		builder.append("]");
		return builder.toString();
	}

}
