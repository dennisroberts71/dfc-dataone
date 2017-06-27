package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;

/**
 * API for accessing event registry for published objects
 * 
 * @author mconway
 *
 */
public interface DataOneEventServiceAO {

	/**
	 * Retrieve logs from the event registry given the parameters
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param event
	 * @param pidFilter
	 * @param startIdx
	 * @param count
	 * @return {@link Log} with the log data
	 */
	public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count);

	/**
	 * Add a given event to the log
	 * 
	 * @param eventData
	 *            {@link EventData} encapsulating all the potential metadata
	 *            about this event
	 * @throws InvalidArgumentException
	 * @throws JargonException
	 * @throws ServiceFailure
	 */
	public void recordEvent(EventData eventData) throws InvalidArgumentException, JargonException, ServiceFailure;

}
