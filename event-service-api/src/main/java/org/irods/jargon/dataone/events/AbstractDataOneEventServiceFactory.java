/**
 * 
 */
package org.irods.jargon.dataone.events;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory to create event services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOneEventServiceFactory {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	public abstract EventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
