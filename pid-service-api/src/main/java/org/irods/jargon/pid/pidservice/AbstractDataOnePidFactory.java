/**
 * 
 */
package org.irods.jargon.pid.pidservice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory to create pid services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOnePidFactory {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	public abstract UniqueIdAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
