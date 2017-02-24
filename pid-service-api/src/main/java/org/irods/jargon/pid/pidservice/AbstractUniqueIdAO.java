/**
 * 
 */
package org.irods.jargon.pid.pidservice;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;

/**
 * Abstract superclass for a pid handler that will accept the provided context
 * from the DataONE service when created by the discovery mechanism
 * 
 * @author mconway
 *
 */
public abstract class AbstractUniqueIdAO extends AbstractDataOnePlugin
		implements UniqueIdAO {

	/**
	 * Default constructor takes a context object with a handle to connection
	 * and config
	 * 
	 * @param publicationContext
	 */
	public AbstractUniqueIdAO(PublicationContext publicationContext) {
		super(publicationContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getIdentifierFromDataObject
	 * (org.irods.jargon.core.pub.domain.DataObject)
	 */
	@Override
	public abstract Identifier getIdentifierFromDataObject(DataObject dataObject)
			throws JargonException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getDataObjectFromIdentifier
	 * (org.dataone.service.types.v1.Identifier)
	 */
	@Override
	public abstract DataObject getDataObjectFromIdentifier(Identifier identifier)
			throws JargonException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getListOfDataoneExposedIdentifiers
	 * ()
	 */
	@Override
	public abstract List<Identifier> getListOfDataoneExposedIdentifiers()
			throws JargonException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getListOfDataoneExposedDataObjects
	 * (java.util.Date, java.util.Date,
	 * org.dataone.service.types.v1.ObjectFormatIdentifier, java.lang.Boolean,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public abstract DataObjectListResponse getListOfDataoneExposedDataObjects(
			Date fromDate, Date toDate, ObjectFormatIdentifier formatId,
			Boolean replicaStatus, Integer start, Integer count)
			throws JargonException;

}
