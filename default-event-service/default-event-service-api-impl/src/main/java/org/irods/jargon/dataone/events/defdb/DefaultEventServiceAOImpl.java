/**
 *
 */
package org.irods.jargon.dataone.events.defdb;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.AbstractEventServiceAO;
import org.irods.jargon.dataone.events.EventData;
import org.irods.jargon.dataone.events.EventsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence based event service
 *
 * @author mcc
 *
 */
public class DefaultEventServiceAOImpl extends AbstractEventServiceAO {

	private static final Logger log = LoggerFactory.getLogger(DefaultEventServiceAOImpl.class);
	private AccessLogDAO accessLogDAO;

	/**
	 * @param irodsAccount
	 * @param publicationContext
	 */
	public DefaultEventServiceAOImpl(final IRODSAccount irodsAccount, final PublicationContext publicationContext,
			final AccessLogDAO accessLogDAO) {
		super(irodsAccount, publicationContext);
		if (accessLogDAO == null) {
			throw new IllegalArgumentException("null accessLogDAO");
		}
		this.accessLogDAO = accessLogDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.irods.jargon.dataone.events.AbstractEventServiceAO#getLogs(java.util.
	 * Date, java.util.Date, org.irods.jargon.dataone.events.EventsEnum,
	 * java.lang.String, int, int)
	 */
	@Override
	public Log getLogs(final Date start, final Date end, final EventsEnum event, final String pid, final int offset,
			final int limit) {
		return null;
	}

	@Override
	public void recordEvent(final EventData eventData)
			throws InvalidArgumentException, JargonException, ServiceFailure {

		log.info("recordEvent()");

		if (eventData == null) {
			throw new IllegalArgumentException("null eventData");
		}

		log.info("eventData:{}", eventData);

		AccessLog accessLog = new AccessLog();

	}

	/**
	 * @return the accessLogDAO
	 */
	public AccessLogDAO getAccessLogDAO() {
		return accessLogDAO;
	}

}
