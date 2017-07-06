/**
 * 
 */
package org.irods.jargon.dataone.pidservice;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.model.DataOneObject;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Abstract superclass for a pid handler that will accept the provided context
 * from the DataONE service when created by the discovery mechanism
 * 
 * @author mconway
 *
 */
public abstract class AbstractDataOnePidServiceAO extends AbstractDataOnePlugin {

	/**
	 * Default constructor with required values
	 * 
	 * @param irodsAccount
	 *            {@link IRODSAccount} for the current iRODS connection
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration and other
	 *            information
	 */
	public AbstractDataOnePidServiceAO(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	public abstract Identifier getIdentifier(DataObject dataObject) throws JargonException;

	public abstract Identifier getIdentifier(Collection collection) throws JargonException;

	public abstract DataOneObject getObject(Identifier identifier) throws JargonException;
}
