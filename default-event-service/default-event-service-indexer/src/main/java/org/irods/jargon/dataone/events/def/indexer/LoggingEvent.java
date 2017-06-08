/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

/**
 * @author mcc
 *
 */
public class LoggingEvent {

	private String entity = "";
	private String path = "";
	private Accessor accessor;
	private String timestamp;

	/**
	 * 
	 */
	public LoggingEvent() {
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the accessor
	 */
	public Accessor getAccessor() {
		return accessor;
	}

	/**
	 * @param accessor
	 *            the accessor to set
	 */
	public void setAccessor(Accessor accessor) {
		this.accessor = accessor;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set.
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoggingEvent [");
		if (entity != null) {
			builder.append("entity=").append(entity).append(", ");
		}
		if (path != null) {
			builder.append("path=").append(path).append(", ");
		}
		if (accessor != null) {
			builder.append("accessor=").append(accessor).append(", ");
		}
		if (timestamp != null) {
			builder.append("timestamp=").append(timestamp);
		}
		builder.append("]");
		return builder.toString();
	}

}
