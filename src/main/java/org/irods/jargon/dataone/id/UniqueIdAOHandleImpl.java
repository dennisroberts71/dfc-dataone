package org.irods.jargon.dataone.id;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;

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
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(identifier);
	
		try {	
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			// Find iRODS object here from Identifier
			dataObject = dataObjectAO.findById(new Long(dataObjectId).intValue());
			log.info("found iRODS file: {}", dataObject.getAbsolutePath());
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
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
		
		int timeout = 5 * 1000; // 5 second timeout
		
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
		
		Queue<List<String>> queue = new PriorityQueue<List<String>>();

        HandleListerRunnable hlThread = new HandleListerRunnable(
        										queue,
        										authHandle,
        										authIndex,
        										privateKey,
        										namingAuthority);
        hlThread.run();
        int totalSleep = 0;
        while ((queue.isEmpty()) && (totalSleep <= timeout)) {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.warn("listObjects: thread interrupted while waiting for list of abjects");
			}
        	totalSleep+=100;
        }
        
        if (! queue.isEmpty()) {
        	log.debug("listObjects: got list of Handle values");
        	List<String> handles = queue.element();
        	
        	for (String h : handles) {
        		Identifier id = new Identifier();
        		id.setValue(h);
        	}
        }
        
		return identifiers;
	}

	@Override
	public List<DataObject> getListOfDataoneExposedDataObjects(
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
		List<DataObject> dataObjectListTmp = new ArrayList<DataObject>();
		List<DataObject> dataObjectListFinal = new ArrayList<DataObject>();
		List<Identifier> ids = getListOfDataoneExposedIdentifiers();
		
		for (Identifier id : ids) {
			DataObject dataObject = getDataObjectFromIdentifier(id);
			Date modifiedDate = dataObject.getUpdatedAt();
			
			if (matchesFromDate(fromDate, modifiedDate) &&
				matchesToDate(toDate, modifiedDate) &&
				matchesFormatId(formatId, dataObject) &&
				matchesReplicaStatus(replicaStatus)) {
				dataObjectListTmp.add(dataObject);
			}
				
		}
		
		// now filter this list according to start and count
		DataObject[] dataObjectArray = (DataObject[]) dataObjectListTmp.toArray();
		int end = start + count;
		for (int i=start; i<dataObjectArray.length && i<end; i++) {
				dataObjectListFinal.add(dataObjectArray[i]);
		}
		
		return dataObjectListFinal;
	}
	
	public long getDataObjectIdFromDataOneIdentifier(
			Identifier pid) throws JargonException {
		
		int idIdx = pid.getValue().indexOf("/");
		long dataObjectId = Long.parseLong(pid.getValue().substring(idIdx));
		
		log.info("getDataObjectIdFromDataOneIdentifier: returning data object id: {}", dataObjectId);
		return dataObjectId;	
	}
	
	public String getHandlePrefix() throws JargonException {
		String prefix = null;
			
		prefix = properties.getProperty("irods.dataone.handle");
	 
		return prefix;
	}
	
	private void loadProperties() {
		
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
			return true;
		}
		
		if (fromDate.getTime() <= dataObjectModDate.getTime()) {
			return true;
		}
		
		return false;
	}
	
	private boolean matchesToDate(Date toDate, Date dataObjectModDate) {
		
		if (toDate == null || toDate.getTime() <= 0) {
			return true;
		}
		
		if (toDate.getTime() >= dataObjectModDate.getTime()) {
			return true;
		}
		
		return false;
	}
	
	private boolean matchesFormatId(ObjectFormatIdentifier formatId, DataObject dataObject) {
		//TODO: need to use new Tika based jargon object that Mike wrote
		return true;
	}
	
	private boolean matchesReplicaStatus(Boolean replicaStatus) {
		
		// replication is always set to false for now
		if (replicaStatus == null || replicaStatus == false) {
			return true;
		}
		
		return false;
	}

}
