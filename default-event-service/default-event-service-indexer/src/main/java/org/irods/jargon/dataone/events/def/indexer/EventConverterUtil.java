/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import java.io.IOException;
import java.util.Date;

import org.irods.jargon.dataone.events.EventLoggingException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility for JSON conversion
 * 
 * @author mcc
 *
 */
public class EventConverterUtil {

	private ObjectMapper mapper = new ObjectMapper();
	private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd.HH:mm:ss");

	/**
	 * 
	 */
	public EventConverterUtil() {
	}

	public LoggingEvent loggingEventFromPayload(final byte[] payload) throws EventLoggingException {
		String jsonRaw = new String(payload);
		LoggingEvent event;
		try {
			event = mapper.readValue(jsonRaw, LoggingEvent.class);
		} catch (IOException e) {
			throw new EventLoggingException("unable to translate json", e);
		}
		return event;

	}

	public Date dateFromTimestamp(final String timestamp) throws EventLoggingException {

		/*
		 * If no date is supplied, generate one from the 'now' time
		 */

		if (timestamp == null || timestamp.isEmpty()) {
			return new Date();
		}

		DateTime result;
		try {
			result = fmt.parseDateTime(timestamp);
		} catch (IllegalArgumentException e) {
			throw new EventLoggingException("unable to parse timestamp: " + timestamp, e);
		}
		return result.toDate();
	}
}
