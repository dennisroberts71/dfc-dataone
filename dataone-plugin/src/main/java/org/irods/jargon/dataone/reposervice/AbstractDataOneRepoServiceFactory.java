/**
 * 
 */
package org.irods.jargon.dataone.reposervice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.plugin.AbstractDataOneFactory;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Factory to create repo services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOneRepoServiceFactory extends AbstractDataOneFactory<AbstractDataOneRepoServiceAO> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.reposervice.DataOneRepoFactory#instance(org.
	 * irods.jargon.dataone.configuration.PublicationContext,
	 * org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public abstract AbstractDataOneRepoServiceAO instance(PublicationContext publicationContext,
			IRODSAccount irodsAccount);

}
