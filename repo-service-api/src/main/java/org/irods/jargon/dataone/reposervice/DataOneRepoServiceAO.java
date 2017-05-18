package org.irods.jargon.dataone.reposervice;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;

/**
 * Abstraction of management of the DataOne repository (objects to list,
 * maintenance of status of DataOne published objects)
 * 
 * @author mconway
 *
 */
public interface DataOneRepoServiceAO {

	/**
	 * Returns a complete list of Unique Identifiers that are associated with
	 * iRODS objects that are exposed to DataONE
	 * 
	 * @return <code>List<<code>Identifier</code>></code>
	 * @throws JargonException
	 */
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException;

	/**
	 * Returns an iRODS Data Object for the DataONE Identifier object specified
	 * 
	 * @return <code>List<<code>DataObject</code>></code>
	 * @throws JargonException
	 */
	public DataObjectListResponse getListOfDataoneExposedDataObjects(Date fromDate, Date toDate,
			ObjectFormatIdentifier formatId, Boolean replicaStatus, Integer start, Integer count)
			throws JargonException;

}
