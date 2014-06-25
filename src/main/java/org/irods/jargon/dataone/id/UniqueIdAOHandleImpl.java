package org.irods.jargon.dataone.id;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.handle.hdllib.HandleValue;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataprofile.DataProfile;
import org.irods.jargon.dataprofile.DataProfileService;
import org.irods.jargon.dataprofile.DataProfileServiceImpl;
import org.irods.jargon.dataprofile.DataTypeResolutionService;
import org.irods.jargon.dataprofile.DataTypeResolutionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UniqueIdAOHandleImpl implements UniqueIdAO {
	
	private final RestConfiguration restConfiguration;
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private Properties properties;
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public UniqueIdAOHandleImpl(RestConfiguration restConfiguration,
			IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.restConfiguration = restConfiguration;
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		loadProperties();
	}
	
	@Override
	public DataObject getDataObjectFromIdentifier(Identifier identifier) throws JargonException {
		
		DataObject dataObject = null;
		log.info("retrieving irods data object id from identifier: {}", identifier.getValue());
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(identifier);
		log.info("got id: {}", dataObjectId);
	
		try {	
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			// Find iRODS object here from Identifier
			dataObject = dataObjectAO.findById(new Long(dataObjectId).intValue());
			if (dataObject != null) {
				log.info("found iRODS file: {}", dataObject.getAbsolutePath());
			} 
			else {
				log.warn("did not find data object for id={}", dataObjectId);
			}
			
		} catch (Exception e) {
			log.warn("Cannot access iRODS object for id={}", dataObjectId);
			throw new JargonException(e.getMessage());
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
		
		return dataObject;
	}

	@Override
	public Identifier getIdentifierFromDataObject(DataObject dataObject) throws JargonException {
		
		if (dataObject == null) {
			log.error("getIdentifierFromDataObject: dataObject is null");
			throw new JargonException("dataObject is null");
		}
		Identifier identifier = new Identifier();
		
		String prefix = getHandlePrefix();
		if (prefix == null) {
			log.error("getIdentifierFromDataObject: prefix is null");
			throw new JargonException("prefix is null");
		}
		String objectId = String.valueOf(dataObject.getId());
		
		identifier.setValue(prefix + "/" + objectId);
		
		return identifier;
	}
	
	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers()
			throws JargonException {
		
		List<Identifier> identifiers = new ArrayList<Identifier>();
		
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
		
		//LinkedBlockingQueue<List<String>> queue = new LinkedBlockingQueue<List<String>>();
		LinkedList<List<String>> queue = new LinkedList<List<String>>();

        HandleListerRunnable hlThread = new HandleListerRunnable(
        										queue,
        										authHandle,
        										authIndex,
        										privateKey,
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
        if (! queue.isEmpty()) {
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

	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(
			Date fromDate,
			Date toDate,
			ObjectFormatIdentifier formatId,
			Boolean replicaStatus,
			Integer start,
			Integer count
		) throws JargonException {
		
		//get complete list of exposed data objects
		// TODO: see if this can be done differently to just request list
		// using filter items
		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();
		List<DataObject> dataObjectListTmp = new ArrayList<DataObject>();
		List<DataObject> dataObjectListFinal = new ArrayList<DataObject>();
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
				
				if (matchesFromDate(fromDate, modifiedDate) &&
					matchesToDate(toDate, modifiedDate) &&
					matchesFormatId(formatId, dataObject) &&
					matchesReplicaStatus(replicaStatus)) {
					dataObjectListTmp.add(dataObject);
				}
			}		
		}
		//save total before filtering with start and count
		dataObjectListResponse.setTotal(dataObjectListTmp.size());
		
		// now filter this list according to start and count
		if (dataObjectListTmp.size() > 0) {
			DataObject[] dataObjectArray =
						dataObjectListTmp.toArray(new DataObject[dataObjectListTmp.size()]);
			int end = start + count;
			for (int i=start; i<dataObjectArray.length && i<end; i++) {
					dataObjectListFinal.add(dataObjectArray[i]);
			}
		}
		
		dataObjectListResponse.setCount(dataObjectListFinal.size());
		dataObjectListResponse.setDataObjects(dataObjectListFinal);
		
		log.info("returning list of dataObjects: {}", dataObjectListFinal.toString());
		return dataObjectListResponse;
	}
	
	public long getDataObjectIdFromDataOneIdentifier(
			Identifier pid) throws JargonException {

		int idIdx = pid.getValue().indexOf("/")+1;
		long dataObjectId = Long.parseLong(pid.getValue().substring(idIdx));
		
		log.info("getDataObjectIdFromDataOneIdentifier: returning data object id: {}", dataObjectId);
		return dataObjectId;	
	}
	
	public String getHandlePrefix() throws JargonException {
		String prefix = null;
			
		prefix = properties.getProperty("irods.dataone.handle.prefix");
	 
		return prefix;
	}
	
	private void loadProperties() {
		this.properties = new Properties();
		InputStream input = null;
		input = getClass().getClassLoader().getResourceAsStream(this.propertiesFilename);

		// load a properties file
		try {
			this.properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", this.propertiesFilename);
			log.error("IOException: {}", e.getStackTrace());
			this.properties = new Properties();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
	
	private boolean matchesFromDate(Date fromDate, Date dataObjectModDate) {
		

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
	
	private boolean matchesToDate(Date toDate, Date dataObjectModDate) {
		
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
	
	private boolean matchesFormatId(ObjectFormatIdentifier formatId, DataObject dataObject) 
				throws JargonException {
		
		IRODSAccount irodsAccount = RestAuthUtils
				.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
		DataTypeResolutionService resolutionService = new DataTypeResolutionServiceImpl(
				irodsAccessObjectFactory, irodsAccount);
		DataProfileService dataProfileService = new DataProfileServiceImpl(
				irodsAccessObjectFactory, irodsAccount, resolutionService);
		
		@SuppressWarnings("unchecked")
		DataProfile<DataObject> dataProfile = dataProfileService.retrieveDataProfile(dataObject.getAbsolutePath());
		String mimeType = dataProfile.getMimeType();
		
		if ((formatId.getValue() == null) || (formatId.equals(mimeType))) {
			return true;
		}
		return false;
	}
	
	private boolean matchesReplicaStatus(Boolean replicaStatus) {
		
		// replication is always set to false for now
		if (replicaStatus == null || replicaStatus == false) {
			log.info("replicastatus : returning matches");
			return true;
		}
		
		log.info("toDate : returning no match");
		return false;
	}

}
