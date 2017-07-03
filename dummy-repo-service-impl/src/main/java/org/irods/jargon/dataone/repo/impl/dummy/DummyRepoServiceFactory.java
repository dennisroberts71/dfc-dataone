/**
 * 
 */
package org.irods.jargon.dataone.repo.impl.dummy;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceFactory;

/**
 * @author mcc
 *
 */
public class DummyRepoServiceFactory extends AbstractDataOneRepoServiceFactory {

	/**
	 * 
	 */
	public DummyRepoServiceFactory() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.reposervice.AbstractDataOneRepoFactory#instance(
	 * org.irods.jargon.dataone.configuration.PublicationContext,
	 * org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public AbstractDataOneRepoServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {
		return new DummyRepoServiceImpl(irodsAccount, publicationContext);
	}

}
