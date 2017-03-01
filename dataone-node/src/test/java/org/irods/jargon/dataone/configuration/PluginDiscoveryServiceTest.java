package org.irods.jargon.dataone.configuration;

import java.util.Properties;

import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.events.EventServiceAO;
import org.irods.jargon.dataone.utils.DataOneTestHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class PluginDiscoveryServiceTest {

	private static DataOneTestHelper dataOneTestHelper;
	private static Properties dataOneProperties;

	@BeforeClass
	public static void before() throws Exception {
		dataOneTestHelper = new DataOneTestHelper();
		dataOneProperties = dataOneTestHelper.getTestProperties();
	}

	@Test
	public void testInstanceEventService() throws Exception {
		IRODSAccessObjectFactory aof = Mockito
				.mock(IRODSAccessObjectFactory.class);
		PublicationContext publicationContext = new PublicationContext();
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(dataOneTestHelper
				.getPluginJarLocation(dataOneProperties));
		publicationContext.setRestConfiguration(restConfiguration);
		publicationContext.setIrodsAccessObjectFactory(aof);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);

		EventServiceAO eventServiceAO = pluginDiscoveryService
				.instanceEventService();
		Assert.assertNotNull("no eventServiceAO", eventServiceAO);
	}
}
