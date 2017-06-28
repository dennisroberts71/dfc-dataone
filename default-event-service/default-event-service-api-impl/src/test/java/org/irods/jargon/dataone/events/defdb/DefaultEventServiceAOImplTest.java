package org.irods.jargon.dataone.events.defdb;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:event-service-beans.xml",
		"classpath:event-dao-hibernate-spring.cfg.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class DefaultEventServiceAOImplTest {

	@Autowired
	private AccessLogDAO accessLogDAO;

	@Test
	public void testLoad() throws Exception {
		IRODSAccount dummyAccount = IRODSAccount.instance("host", 1247, "user", "pwd", "", "zone", "");
		IRODSAccessObjectFactory iaf = Mockito.mock(IRODSAccessObjectFactory.class);
		PublicationContext context = new PublicationContext();
		context.setIrodsAccessObjectFactory(iaf);
		AccessLogDAO accessLogDAO = Mockito.mock(AccessLogDAO.class);
		DefaultEventServiceAOImpl defaultEventServiceAO = new DefaultEventServiceAOImpl(dummyAccount, context,
				accessLogDAO);
		Assert.assertNotNull("no event service returned", defaultEventServiceAO);
	}

	/**
	 * @return the accessLogDAO
	 */
	public AccessLogDAO getAccessLogDAO() {
		return accessLogDAO;
	}

	/**
	 * @param accessLogDAO
	 *            the accessLogDAO to set
	 */
	public void setAccessLogDAO(AccessLogDAO accessLogDAO) {
		this.accessLogDAO = accessLogDAO;
	}

}
