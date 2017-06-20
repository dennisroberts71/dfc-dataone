package org.irods.jargon.dataone.events.defdb;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceFactory;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dennis
 */
public class DefaultEventServiceFactory extends AbstractDataOneEventServiceFactory {

	private static final Logger log = LoggerFactory.getLogger(DefaultEventServiceFactory.class);

	@Override
	public DataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {
		return new DefaultEventServiceAOImpl(irodsAccount, publicationContext, init());
	}

	private AccessLogDAO init() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "event-service-beans.xml", "event-dao-hibernate-spring.cfg.xml" });
		AccessLogDAO accessLogDAO = (AccessLogDAO) context.getBean("accessLogDAO");
		log.info("successfully loaded DAO");
		return accessLogDAO;
	}
}
