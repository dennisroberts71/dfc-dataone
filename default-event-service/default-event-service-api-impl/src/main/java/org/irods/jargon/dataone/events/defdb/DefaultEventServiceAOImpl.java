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
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.events.AbstractEventServiceAO;
import org.irods.jargon.dataone.events.EventsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Persistence based event service
 * 
 * @author mcc
 *
 */
public class DefaultEventServiceAOImpl extends AbstractEventServiceAO {

	private AccessLogDAO accessLogDAO;
	private static final Logger log = LoggerFactory.getLogger(DefaultEventServiceAOImpl.class);

	/**
	 * @param irodsAccount
	 * @param publicationContext
	 */
	public DefaultEventServiceAOImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
		init();
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

	private void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "event-service-beans.xml", "event-dao-hibernate-spring.cfg.xml" });
		this.accessLogDAO = (AccessLogDAO) context.getBean("accessLogDAO");
		log.info("successfully loaded DAO");
	}

}
