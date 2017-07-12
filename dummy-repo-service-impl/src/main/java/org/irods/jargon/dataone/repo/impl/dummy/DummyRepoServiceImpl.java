/**
 * 
 */
package org.irods.jargon.dataone.repo.impl.dummy;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.model.DataOneObjectListResponse;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mcc
 *
 */
public class DummyRepoServiceImpl extends AbstractDataOneRepoServiceAO {

	public DummyRepoServiceImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	@Override
	public DataOneObjectListResponse getExposedObjects(Date fromDate, Date toDate, ObjectFormatIdentifier formatId,
			Boolean replicaStatus, Integer start, Integer count) throws JargonException {
		return new DataOneObjectListResponse(new ArrayList<>(), 0, 0);
	}

	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {
		// FIXME: narrow down the needed public methods
		return null;
	}

	@Override
	public Date getLastModifiedDate(String path) throws JargonException {
		return new Date();
	}

	@Override
	public String getFormat(String path) throws JargonException {
		return "";
	}
}
