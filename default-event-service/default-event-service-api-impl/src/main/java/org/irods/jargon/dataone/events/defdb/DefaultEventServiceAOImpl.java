/**
 * 
 */
package org.irods.jargon.dataone.events.defdb;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.events.AbstractEventServiceAO;
import org.irods.jargon.dataone.events.EventsEnum;

/**
 * Persistence based event service
 * 
 * @author mcc
 *
 */
public class DefaultEventServiceAOImpl extends AbstractEventServiceAO {

	/**
	 * @param irodsAccount
	 * @param publicationContext
	 */
	public DefaultEventServiceAOImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
		// TODO Auto-generated constructor stub
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
	public Log getLogs(Date arg0, Date arg1, EventsEnum arg2, String arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.events.AbstractEventServiceAO#recordEvent(org.
	 * dataone.service.types.v1.Event, org.dataone.service.types.v1.Identifier,
	 * java.lang.String)
	 */
	@Override
	public void recordEvent(Event arg0, Identifier arg1, String arg2)
			throws InvalidArgumentException, JargonException, ServiceFailure {
		// TODO Auto-generated method stub

	}

}
