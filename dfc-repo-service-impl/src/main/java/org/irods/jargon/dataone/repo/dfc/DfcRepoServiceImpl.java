/**
 * 
 */
package org.irods.jargon.dataone.repo.dfc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.DataObjectListResponse;
import org.irods.jargon.dataone.utils.DataTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcc
 *
 */
public class DfcRepoServiceImpl extends AbstractDataOneRepoServiceAO {

	public DfcRepoServiceImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(final Date fromDate, final Date toDate,
			final ObjectFormatIdentifier formatId, final Boolean replicaStatus, final Integer start,
			final Integer count) throws JargonException {

		// get complete list of exposed data objects
		// TODO: see if this can be done differently to just request list
		// using filter items
		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();
		List<DataObject> dataObjectListTmp = new ArrayList<>();
		List<DataObject> dataObjectListFinal = new ArrayList<>();
		List<Identifier> ids = getListOfDataoneExposedIdentifiers();

		log.info("finding list of data objects with list of {} identifiers", ids.size());

		for (Identifier id : ids) {
			DataObject dataObject;
			dataObject = null; // TODO://recast
								// getDataObjectFromIdentifier(id);

			if (dataObject != null) {
				Date modifiedDate = dataObject.getUpdatedAt();

				if (matchesFromDate(fromDate, modifiedDate) && matchesToDate(toDate, modifiedDate) &&
				// matchesFormatId(formatId, dataObject) && support not
				// required
						matchesReplicaStatus(replicaStatus)) {
					dataObjectListTmp.add(dataObject);
				}
			}
		}
		// save total before filtering with start and count
		dataObjectListResponse.setTotal(dataObjectListTmp.size());

		// now filter this list according to start and count
		if (dataObjectListTmp.size() > 0) {
			DataObject[] dataObjectArray = dataObjectListTmp.toArray(new DataObject[dataObjectListTmp.size()]);
			int end = start + count;
			for (int i = start; i < dataObjectArray.length && i < end; i++) {
				dataObjectListFinal.add(dataObjectArray[i]);
			}
		}

		dataObjectListResponse.setCount(dataObjectListFinal.size());
		dataObjectListResponse.setDataObjects(dataObjectListFinal);

		log.info("returning list of dataObjects: {}", dataObjectListFinal.toString());
		return dataObjectListResponse;
	}

	private boolean matchesFromDate(final Date fromDate, final Date dataObjectModDate) {

		if (fromDate == null || fromDate.getTime() <= 0) {
			log.info("fromDate : returning matches");
			return true;
		}

		if (fromDate.getTime() <= dataObjectModDate.getTime()) {
			log.info("fromDate : returning matches");
			return true;
		}

		log.info("fromDate : returning no match");
		return false;
	}

	private boolean matchesToDate(final Date toDate, final Date dataObjectModDate) {

		if (toDate == null || toDate.getTime() <= 0) {
			log.info("toDate : returning matches");
			return true;
		}

		if (toDate.getTime() >= dataObjectModDate.getTime()) {
			log.info("toDate : returning matches");
			return true;
		}

		log.info("toDate : returning no match");
		return false;
	}

	private boolean matchesFormatId(final ObjectFormatIdentifier formatId, final DataObject dataObject)
			throws JargonException, JargonQueryException {

		String dataFormat = DataTypeUtils.getDataObjectFormatFromMetadata(getIrodsAccount(),
				this.getPublicationContext().getIrodsAccessObjectFactory(), dataObject);

		if ((formatId.getValue() == null) || (formatId.getValue().equals(dataFormat))) {
			return true;
		}
		return false;
	}

	private boolean matchesReplicaStatus(final Boolean replicaStatus) {

		// replication is always set to false for now
		if (replicaStatus == null || replicaStatus == false) {
			log.info("replicastatus : returning matches");
			return true;
		}

		log.info("toDate : returning no match");
		return false;
	}

	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {

		List<Identifier> identifiers = new ArrayList<>();

		// FIXME: recast
		return identifiers;
	}

}
