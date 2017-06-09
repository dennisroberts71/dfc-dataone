package org.irods.jargon.dataone.events.defdb;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceFactory;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;

/**
 * @author dennis
 */
public class DefaultEventServiceFactory extends AbstractDataOneEventServiceFactory {

	@Override
	public DataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {
		return new DefaultEventServiceAOImpl(irodsAccount, publicationContext);
	}
}
