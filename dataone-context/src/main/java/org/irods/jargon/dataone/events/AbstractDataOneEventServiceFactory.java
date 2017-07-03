/**
* 
*/
package org.irods.jargon.dataone.events;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.plugin.AbstractDataOneFactory;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Factory to create event services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOneEventServiceFactory extends AbstractDataOneFactory<AbstractDataOneEventServiceAO> {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	@Override
	public abstract AbstractDataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
