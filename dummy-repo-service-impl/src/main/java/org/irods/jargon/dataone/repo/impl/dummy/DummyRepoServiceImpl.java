/**
 * 
 */
package org.irods.jargon.dataone.repo.impl.dummy;

import java.util.Date;

import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.repo.reposervice.AbstractRepoServiceAO;
import org.irods.jargon.dataone.repo.reposervice.DataObjectListResponse;

/**
 * @author mcc
 *
 */
public class DummyRepoServiceImpl extends AbstractRepoServiceAO {

	/**
	 * @param publicationContext
	 */
	public DummyRepoServiceImpl(PublicationContext publicationContext) {
		super(publicationContext);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.dataone.repo.reposervice.AbstractRepoServiceAO#
	 * getListOfDataoneExposedDataObjects(java.util.Date, java.util.Date,
	 * org.dataone.service.types.v1.ObjectFormatIdentifier, java.lang.Boolean,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(Date arg0, Date arg1, ObjectFormatIdentifier arg2,
			Boolean arg3, Integer arg4, Integer arg5) throws JargonException {
		return new DataObjectListResponse();
	}

}
