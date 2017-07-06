/**
 *
 */
package org.irods.jargon.dataone.reposervice;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.model.DataOneObject;
import org.irods.jargon.dataone.model.DataOneObjectListResponse;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * Abstract implementation of a dataone repo service. This service is in charge
 * of coordinating data objects that are to be provided to DataONE polling
 *
 * @author mconway
 *
 */
public abstract class AbstractDataOneRepoServiceAO extends AbstractDataOnePlugin {

	/**
	 * Default constructor with required values
	 *
	 * @param irodsAccount
	 *            {@link IRODSAccount} for the current iRODS connection
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration and other
	 *            information
	 */
	public AbstractDataOneRepoServiceAO(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/**
	 * Retrieves a list of DataOne objects matching the given parameters.
	 *
	 * @param fromDate
	 *            the start date for the search.
	 * @param toDate
	 *            the end date for the search.
	 * @param formatId
	 *            the format identifier for the search.
	 * @param replicaStatus
	 *            the replica status for the search.
	 * @param start
	 *            the start index for paged searches.
	 * @param count
	 *            the maximum number of results for paged searches.
	 * @return a possibly partial list of matching DataOne objects.
	 * @throws JargonException
	 */
	public abstract DataOneObjectListResponse getExposedObjects(Date fromDate, Date toDate,
			ObjectFormatIdentifier formatId, Boolean replicaStatus, Integer start, Integer count)
			throws JargonException;

	/**
	 * Retrieves the entire list of identifiers that have been exposed to DataOne.
	 *
	 * @return the list of identifiers.
	 * @throws JargonException
	 */
	public abstract List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException;

	/**
	 * Retrieves the most recent modification date for a {@link DataObject}.
	 *
	 * @param dataObject the {@link DataObject}
	 * @return the most recent modification date.
	 * @throws JargonException
	 */
	public abstract Date getLastModifiedDate(DataObject dataObject) throws JargonException;

	/**
	 * Retrieves the most recent modification date for a {@link Collection}.
	 *
	 * @param collection the {@link Collection}.
	 * @return the most recent modification date.
	 * @throws JargonException
	 */
	public abstract Date getLastModifiedDate(Collection collection) throws JargonException;

	/**
	 * Retrieves the format of a {@link DataObject}.
	 *
	 * @param dataObject the {@link DataObject}
	 * @return a string containing the corresponding MIME type.
	 * @throws JargonException
	 */
	public abstract String getFormat(DataObject dataObject) throws JargonException;

	/**
	 * Retrieves the format of a {@link Collection}.
	 *
	 * @param collection the {@link Collection}.
	 * @return a string containing the corresponding MIME type.
	 * @throws JargonException
	 */
	public abstract String getFormat(Collection collection) throws JargonException;
}
