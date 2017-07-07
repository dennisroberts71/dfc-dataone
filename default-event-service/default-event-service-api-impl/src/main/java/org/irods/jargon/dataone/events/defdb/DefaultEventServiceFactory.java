package org.irods.jargon.dataone.events.defdb;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceAO;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceFactory;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dennis
 */
public class DefaultEventServiceFactory extends AbstractDataOneEventServiceFactory {

	private AccessLogDAO accessLogDAO;
	private static final Logger log = LoggerFactory.getLogger(DefaultEventServiceFactory.class);

	public DefaultEventServiceFactory() {
		super();
		this.accessLogDAO = init();
	}

	private AccessLogDAO init() {
		log.info("init()");
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "event-service-beans.xml", "event-dao-hibernate-spring.cfg.xml" });
		log.info("applicationContext built");
		AccessLogDAO accessLogDAO = (AccessLogDAO) context.getBean("accessLogDAO");
		log.info("loaced accessLogDAO:{}", accessLogDAO);
		log.info("successfully loaded DAO");
		return accessLogDAO;
	}

	@Override
	public AbstractDataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {
		return new DefaultEventServiceAOImpl(irodsAccount, publicationContext, accessLogDAO);
	}

}
