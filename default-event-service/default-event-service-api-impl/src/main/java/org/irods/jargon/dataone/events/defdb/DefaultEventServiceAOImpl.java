/**
 *
 */
package org.irods.jargon.dataone.events.defdb;

import java.util.Date;
import java.util.List;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.LogEntry;
import org.dataone.service.types.v1.NodeReference;
import org.dataone.service.types.v1.Subject;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceAO;
import org.irods.jargon.dataone.events.EventData;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.irods.jargon.dataone.events.EventsEnum;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence based event service
 *
 * @author mcc
 *
 */
public class DefaultEventServiceAOImpl extends AbstractDataOneEventServiceAO {

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

		// see
		// https://github.com/DICE-UNC/dfc-dataone/blob/master/src/main/java/org/irods/jargon/dataone/events/EventLogAOElasticSearchImpl.java

		log.info("getLogs()");
		Log logResult = new Log();
		LogEntry logEntry;
		List<AccessLog> queryResult;
		try {
			queryResult = accessLogDAO.find(start, end, event, pid, offset, limit);
		} catch (EventLoggingException e) {
			log.error("error querying logs", e);
			throw new JargonRuntimeException(e.getMessage());
		}
		for (AccessLog resultEntry : queryResult) {
			log.info("resultEntry:{}", resultEntry);
			logEntry = new LogEntry();
			logEntry.setDateLogged(resultEntry.getDateAdded());
			logEntry.setEntryId(resultEntry.getId().toString());
			logEntry.setEvent(Event.valueOf(resultEntry.getEvent().toString()));
			Identifier identifier = new Identifier();
			identifier.setValue(resultEntry.getPermanentId().toString());
			logEntry.setIdentifier(identifier);
			logEntry.setIpAddress(resultEntry.getIpAddress());

			if (resultEntry.getNodeIdentifier() == null || resultEntry.getNodeIdentifier().isEmpty()) {
			} else {
				NodeReference nodeReference = new NodeReference();
				nodeReference.setValue(resultEntry.getNodeIdentifier());
				logEntry.setNodeIdentifier(nodeReference);
			}

			if (resultEntry.getSubject() == null || resultEntry.getSubject().isEmpty()) {
			} else {
				Subject subject = new Subject();
				subject.setValue(resultEntry.getSubject());
				logEntry.setSubject(subject);
			}

			logEntry.setUserAgent(resultEntry.getUserAgent());
			logResult.addLogEntry(logEntry);

		}

		return logResult;

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
		accessLog.setDateAdded(new Date());
		accessLog.setEvent(EventsEnum.valueOfFromDataOne(eventData.getEvent()));
		accessLog.setIpAddress(eventData.getIpAddress());
		accessLog.setIrodsPath(eventData.getIrodsPath());
		accessLog.setNodeIdentifier(eventData.getNodeIdentifier());
		accessLog.setPermanentId(eventData.getId().getValue());
		accessLog.setSubject(eventData.getSubject());
		accessLog.setUserAgent(eventData.getUserAgent());

		accessLogDAO.save(accessLog);
		log.info("logged!");

	}

	/**
	 * @return the accessLogDAO
	 */
	public AccessLogDAO getAccessLogDAO() {
		return accessLogDAO;
	}

}
