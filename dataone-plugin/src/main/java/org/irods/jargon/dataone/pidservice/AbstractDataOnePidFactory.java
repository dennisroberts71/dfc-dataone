/**
 * 
 */
package org.irods.jargon.dataone.pidservice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.plugin.AbstractDataOneFactory;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Factory to create pid services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOnePidFactory extends AbstractDataOneFactory<AbstractDataOnePidServiceAO> {

	@Override
	public abstract AbstractDataOnePidServiceAO instance(PublicationContext publicationContext,
			IRODSAccount irodsAccount);

}
