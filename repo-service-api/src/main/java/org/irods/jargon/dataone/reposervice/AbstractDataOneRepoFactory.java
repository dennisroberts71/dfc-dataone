/**
 * 
 */
package org.irods.jargon.dataone.reposervice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory to create repo services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOneRepoFactory {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	public abstract DataOneRepoAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
