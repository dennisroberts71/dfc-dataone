/**
 * 
 */
package org.irods.jargon.dataone.events.impl.dummy;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceAO;
import org.irods.jargon.dataone.events.EventData;
import org.irods.jargon.dataone.events.EventsEnum;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy implementation of an event service suitable for testing of discovery
 * and loading
 * 
 * @author mconway
 *
 */
public class DummyEventServiceAOImpl extends AbstractDataOneEventServiceAO {

	public static final Logger log = LoggerFactory.getLogger(DummyEventServiceAOImpl.class);

	public DummyEventServiceAOImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.events.AbstractEventServiceAO#getLogs(java.util
	 * .Date, java.util.Date, org.irods.jargon.dataone.events.EventsEnum,
	 * java.lang.String, int, int)
	 */
	@Override
	public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count) {
		log.info("getLogs()");
		return new Log();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.events.AbstractEventServiceAO#recordEvent(org
	 * .dataone.service.types.v1.Event, org.dataone.service.types.v1.Identifier,
	 * java.lang.String)
	 */
	@Override
	public void recordEvent(EventData eventData) throws InvalidArgumentException, JargonException, ServiceFailure {
		log.info("recordEvent()");
		log.info("event:{}", eventData);

	}

}
