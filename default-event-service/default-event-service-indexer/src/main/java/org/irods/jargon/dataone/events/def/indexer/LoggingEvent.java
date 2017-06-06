/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcc
 *
 */
public class LoggingEvent {

	private String entity = "";
	private String path = "";
	private List<Accessor> accessor = new ArrayList<>();

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
	public List<Accessor> getAccessor() {
		return accessor;
	}

	/**
	 * @param accessor
	 *            the accessor to set
	 */
	public void setAccessor(List<Accessor> accessor) {
		this.accessor = accessor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("LoggingEvent [");
		if (entity != null) {
			builder.append("entity=").append(entity).append(", ");
		}
		if (path != null) {
			builder.append("path=").append(path).append(", ");
		}
		if (accessor != null) {
			builder.append("accessor=").append(accessor.subList(0, Math.min(accessor.size(), maxLen)));
		}
		builder.append("]");
		return builder.toString();
	}

}
