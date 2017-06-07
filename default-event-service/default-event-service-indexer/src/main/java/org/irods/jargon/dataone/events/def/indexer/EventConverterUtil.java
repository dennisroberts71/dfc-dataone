/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import java.io.IOException;

import org.irods.jargon.dataone.events.EventLoggingException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility for JSON conversion
 * 
 * @author mcc
 *
 */
public class EventConverterUtil {

	ObjectMapper mapper = new ObjectMapper();

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

}
