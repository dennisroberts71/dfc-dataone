/**
 * 
 */
package org.irods.jargon.dataone.pidservice.impl.dummy;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.pid.pidservice.AbstractDataOnePidFactory;
import org.irods.jargon.pid.pidservice.AbstractDataOnePidServiceAO;

/**
 * @author mcc
 *
 */
public class DummyPidServiceFactory extends AbstractDataOnePidFactory {

	/**
	 * 
	 */
	public DummyPidServiceFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.AbstractDataOnePidFactory#instance(org.
	 * irods.jargon.dataone.configuration.PublicationContext,
	 * org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public AbstractDataOnePidServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {

		return new DummyPidServiceAOImpl(irodsAccount, publicationContext);
	}

}
