package org.irods.jargon.dataone.plugin;

import java.io.File;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.utils.LocalFileUtils;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceAO;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.pid.pidservice.AbstractDataOnePidServiceAO;
import org.junit.Assert;
import org.junit.Test;

public class PluginDiscoveryServiceTest {

	@Test
	public void testInit() throws Exception {
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = LocalFileUtils.getClasspathResourceAsFile("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		// looks for no errors as OK
		Assert.assertTrue(true);

	}

	@Test
	public void testInstanceEventService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		AbstractDataOneEventServiceAO actual = pluginDiscoveryService.instanceEventService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

	@Test
	public void testInstanceUniqueIdService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		AbstractDataOnePidServiceAO actual = pluginDiscoveryService.instancePidService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

	@Test
	public void testInstanceRepoService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		AbstractDataOneRepoServiceAO actual = pluginDiscoveryService.instanceRepoService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

}
