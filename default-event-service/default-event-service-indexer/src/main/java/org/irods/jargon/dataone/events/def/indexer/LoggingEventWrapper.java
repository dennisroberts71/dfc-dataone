/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

/**
 * @author mcc
 *
 */
public class LoggingEventWrapper {

	private LoggingEvent loggingEvent;
	private String eventType;

	/**
	 * 
	 */
	public LoggingEventWrapper() {
	}

	/**
	 * @return the loggingEvent
	 */
	public LoggingEvent getLoggingEvent() {
		return loggingEvent;
	}

	/**
	 * @param loggingEvent
	 *            the loggingEvent to set
	 */
	public void setLoggingEvent(LoggingEvent loggingEvent) {
		this.loggingEvent = loggingEvent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoggingEventWrapper [");
		if (loggingEvent != null) {
			builder.append("loggingEvent=").append(loggingEvent);
		}
		builder.append("]");
		return builder.toString();
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

}
