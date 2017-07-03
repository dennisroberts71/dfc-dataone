/**
 * 
 */
package org.irods.jargon.dataone.plugin;

import org.irods.jargon.core.connection.IRODSAccount;

/**
 * Service that can obtain references to loaded plugins
 * 
 * @author mcc
 *
 */
public interface PluginFactoryDiscoveryServiceProvider {

	public AbstractDataOneFactory<?> instancePidService(IRODSAccount irodsAccount) throws PluginNotFoundException;

	public AbstractDataOneFactory<?> instanceRepoService(IRODSAccount irodsAccount) throws PluginNotFoundException;

	public AbstractDataOneFactory<?> instanceEventService(IRODSAccount irodsAccount) throws PluginNotFoundException;

}
