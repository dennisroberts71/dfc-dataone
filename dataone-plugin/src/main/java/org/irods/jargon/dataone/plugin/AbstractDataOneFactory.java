/**
 * 
 */
package org.irods.jargon.dataone.plugin;

import org.irods.jargon.core.connection.IRODSAccount;

/**
 * @author mcc
 *
 */
public abstract class AbstractDataOneFactory<T extends AbstractDataOnePlugin> {

	public abstract T instance(PublicationContext publicationContext, IRODSAccount irodsAccount);
}
