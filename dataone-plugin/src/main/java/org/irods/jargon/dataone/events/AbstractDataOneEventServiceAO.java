/**
 * 
 */
package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Abstract superclass for the event service that accepts context and
 * configuration information from the member node service
 * 
 * @author mconway
 *
 */
public abstract class AbstractDataOneEventServiceAO extends AbstractDataOnePlugin {

	/**
	 * Default constructor with required values
	 * 
	 * @param irodsAccount
	 *            {@link IRODSAccount} for the current iRODS connection
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration and other
	 *            information
	 */
	public AbstractDataOneEventServiceAO(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/**
	 * Retrieves the log entries matching the given parameters.
	 *
	 * @param fromDate
	 *            the starting date for the search
	 * @param toDate
	 *            the ending date for the search
	 * @param event
	 *            the type of event to search for
	 * @param pidFilter
	 *            the permanent identifier to search for
	 * @param startIdx
	 *            the starting index for paged searches
	 * @param count
	 *            the maximum number of results for paged searches
	 * @return
	 *             the set of matching log entries.
	 */
	public abstract Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx,
			int count);

	/**
	 * Records an event.
	 *
	 * @param eventData
	 *            the event data to record.
	 * @throws InvalidArgumentException
	 *            if the event data is incorrect
	 * @throws JargonException
	 *            if an error occurs while retrieving information from iRODS
	 * @throws ServiceFailure
	 *            if the attempt to record the event fails
	 */
	public abstract void recordEvent(EventData eventData)
			throws InvalidArgumentException, JargonException, ServiceFailure;
}
