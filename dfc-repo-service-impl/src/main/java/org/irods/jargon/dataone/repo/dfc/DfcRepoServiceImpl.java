/**
 * 
 */
package org.irods.jargon.dataone.repo.dfc;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.id.handle.HandleListerRunnable;
import org.irods.jargon.dataone.repo.reposervice.AbstractRepoServiceAO;
import org.irods.jargon.dataone.repo.reposervice.DataObjectListResponse;
import org.irods.jargon.dataone.utils.DataTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcc
 *
 */
public class DfcRepoServiceImpl extends AbstractRepoServiceAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param publicationContext
	 */
	public DfcRepoServiceImpl(PublicationContext publicationContext) {
		super(publicationContext);
	}

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
			try {
				dataObject = getDataObjectFromIdentifier(id);
			} catch (JargonException ex) {
				// just ignore this id
				dataObject = null;
			}

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

		String dataFormat = DataTypeUtils.getDataObjectFormatFromMetadata(irodsAccount, irodsAccessObjectFactory,
				dataObject);

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

	private List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {

		List<Identifier> identifiers = new ArrayList<>();

		// retrieve properties to get list of current handles from
		// handle server
		StringBuilder hdl = new StringBuilder();
		hdl.append(properties.getProperty("irods.dataone.handle.namingAuthority"));
		hdl.append("/");
		hdl.append(properties.getProperty("irods.dataone.handle.prefix"));
		String authHandle = hdl.toString();
		String authIndex = properties.getProperty("irods.dataone.handle.index");
		String privateKey = properties.getProperty("irods.dataone.handle.privateKeyPath");
		String namingAuthority = properties.getProperty("irods.dataone.handle.prefix");

		log.info("config params for handle lister:");
		log.info("authHandle: {}", authHandle);
		log.info("authIndex:{}", authIndex);
		log.info("privateKey: {}", privateKey);
		log.info("namingAuthority: {}", namingAuthority);

		// LinkedBlockingQueue<List<String>> queue = new
		// LinkedBlockingQueue<List<String>>();
		LinkedList<List<String>> queue = new LinkedList<>();

		HandleListerRunnable hlThread = new HandleListerRunnable(queue, authHandle, authIndex, privateKey,
				namingAuthority);
		hlThread.run();
		log.info("continuing after call to run");

		synchronized (queue) {
			while (queue.isEmpty()) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					log.warn("getListOfDataoneExposedIdentifiers: caught InterruptedException while waiting for queue");
				}
			}
		}

		List<String> handles = null;
		if (!queue.isEmpty()) {
			log.debug("listObjects: got list of Handle values");
			handles = queue.element();
		}

		if (handles != null) {
			log.info("listObjects: got list of Handle values: {}", handles.toString());

			for (String h : handles) {
				Identifier id = new Identifier();
				id.setValue(h);
				identifiers.add(id);
			}
		}

		log.info("returning identifiers: {}", identifiers);
		return identifiers;
	}

}
