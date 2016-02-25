package org.irods.jargon.dataone.id;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.AVUQueryOperatorEnum;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.utils.DataTypeUtils;
//import org.irods.jargon.dataprofile.DataProfile;
//import org.irods.jargon.dataprofile.DataProfileService;
//import org.irods.jargon.dataprofile.DataProfileServiceImpl;
//import org.irods.jargon.dataprofile.DataTypeResolutionService;
//import org.irods.jargon.dataprofile.DataTypeResolutionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UniqueIdAOHandleInMetadataImpl implements UniqueIdAO {
	
	private final RestConfiguration restConfiguration;
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private Properties properties;
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public UniqueIdAOHandleInMetadataImpl(RestConfiguration restConfiguration,
			IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.restConfiguration = restConfiguration;
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		loadProperties();
	}
	
	@Override
	public DataObject getDataObjectFromIdentifier(Identifier identifier)
			throws JargonException, FileNotFoundException {
		
		DataObject dataObject = null;
		log.info("retrieving irods data object id from identifier: {}", identifier.getValue());
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(identifier);
		log.info("got id: {}", dataObjectId);
		
		// DataObjectAO.findDomainByMetadataQuery
		// returns List<DataObject>
		
		if (dataObjectId < 0) {
			throw new FileNotFoundException("");
		}
	
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
			
//		} catch (Exception e) {
//			log.warn("Cannot access iRODS object for id={}", dataObjectId);
//			throw e;
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
		
		AVUQueryElement avuQuery1 = null;
		AVUQueryElement avuQuery2 = null;
		
		String handleAttr = properties.getProperty("irods.dataone.publish_entity_metadata_attr");
		String handleValue = properties.getProperty("irods.dataone.publish_entity_metadata_value");
		
		List<Identifier> identifiers = new ArrayList<Identifier>();

		try {
			avuQuery1 = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.ATTRIBUTE,
						AVUQueryOperatorEnum.EQUAL,
						handleAttr);
			
			avuQuery2 = AVUQueryElement.instanceForValueQuery(
							AVUQueryPart.VALUE,
							AVUQueryOperatorEnum.EQUAL,
							handleValue);
			
		} catch (JargonQueryException e) {
			log.error("getListOfDataoneExposedIdentifiers: failed to create AVU query elements");
			return identifiers;
		}
		
		IRODSAccount irodsAccount = RestAuthUtils
				.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
		
		DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
		List<DataObject> dataObjects = new ArrayList<DataObject>();
		try {
			dataObjects = dataObjectAO.findDomainByMetadataQuery(Arrays.asList(avuQuery1, avuQuery2));
		} catch (JargonQueryException e) {
			log.error("getListOfDataoneExposedIdentifiers: failed to execute AVU query");
			return identifiers;
		}
		
		// restrict start date for now to 2/10/16
		Date afterDate = new Date(Long.parseLong("1455062400") * 1000);	
		for(DataObject dataObject : dataObjects) {
			if(dataObject.getCreatedAt().after(afterDate)) {
				identifiers.add(getIdentifierFromDataObject(dataObject));
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
		
		int total = 0;
		
		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();	

		List<AVUQueryElement> avuQueryList = createAVUQueryElementList(fromDate, toDate, formatId);
		
		IRODSAccount irodsAccount = RestAuthUtils
				.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
		
		DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
		List<DataObject> dataObjects = new ArrayList<DataObject>();
		
		try {
			dataObjects = dataObjectAO.findDomainByMetadataQuery(avuQueryList, start);
			// save original size of set returned
			total = dataObjects.size();			
		} catch (JargonQueryException e) {
			log.error("test_getListOfDataoneExposedDataObjects: failed to execute AVU query");
			return dataObjectListResponse;
		}
			
		// truncate list if count is specified and it is less than the total
		List<DataObject> finalDataObjects = new ArrayList<DataObject>();
		if ((count > 0) && (count < total)) {
			finalDataObjects = dataObjects.subList(0, count);
		}
		else {
			finalDataObjects = dataObjects;
		}
		
		dataObjectListResponse.setTotal(total);
		dataObjectListResponse.count = finalDataObjects.size();
		dataObjectListResponse.setDataObjects(finalDataObjects);
		
		return dataObjectListResponse;
	}
	
	
	public long getDataObjectIdFromDataOneIdentifier(Identifier pid) {

		int idIdx = pid.getValue().indexOf("/")+1;
		long dataObjectId;
		try {
			dataObjectId = Long.parseLong(pid.getValue().substring(idIdx));
		} catch (NumberFormatException e) {
			// set to catch illegal object identifiers for iRODS MN
			dataObjectId = -1;
		}
		
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
	
	
	private List<AVUQueryElement> createAVUQueryElementList(
			Date fromDate,
			Date toDate,
			ObjectFormatIdentifier formatId) {

		// TODO: probably should move these to properties
		String dateAttr = "StartDateTime";
		String formatAttr = "Format";
		List<AVUQueryElement> avuQueryList = new ArrayList<AVUQueryElement>();
		String handleAttr = properties.getProperty("irods.dataone.publish_entity_metadata_attr");
		String handleValue = properties.getProperty("irods.dataone.publish_entity_metadata_value");
		
		AVUQueryElement avuQuery;
		try {
			// DataOne exposed query
			avuQuery = AVUQueryElement.instanceForValueQuery(
					AVUQueryPart.ATTRIBUTE,
					AVUQueryOperatorEnum.EQUAL,
					handleAttr);
			avuQueryList.add(avuQuery);
		
			avuQuery = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.VALUE,
						AVUQueryOperatorEnum.EQUAL,
						handleValue);
			avuQueryList.add(avuQuery);
			
			// handle any date queries
			// restrict start date for now to 2/17/16 00:00:01
			long newFromDate;
			if(fromDate != null) {
				newFromDate = java.lang.Math.max(fromDate.getTime()/1000, Long.parseLong("1455667201"));
			}
			else {
				newFromDate = Long.parseLong("1455667201");
			}
			
			// fromDate query
			avuQuery = AVUQueryElement.instanceForValueQuery(
					AVUQueryPart.ATTRIBUTE,
					AVUQueryOperatorEnum.EQUAL,
					dateAttr);
			avuQueryList.add(avuQuery);
		
			avuQuery = AVUQueryElement.instanceForValueQuery(
					AVUQueryPart.VALUE,
					AVUQueryOperatorEnum.GREATER_OR_EQUAL,
					Long.toString(newFromDate));
			avuQueryList.add(avuQuery);
			
			// toDate query
			if ((toDate != null) && ((toDate.getTime()/1000) >= newFromDate)) {
				avuQuery = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.ATTRIBUTE,
						AVUQueryOperatorEnum.EQUAL,
						dateAttr);
				avuQueryList.add(avuQuery);
			
				avuQuery = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.VALUE,
						AVUQueryOperatorEnum.LESS_THAN,
						Long.toString(toDate.getTime()/1000));
				avuQueryList.add(avuQuery);
				
			}
			
			
//			if ((toDate != null) && (toDate.getTime() >= newFromDate)) {
//				avuQuery = AVUQueryElement.instanceForValueQuery(
//						AVUQueryPart.ATTRIBUTE,
//						AVUQueryOperatorEnum.EQUAL,
//						dateAttr);
//				avuQueryList.add(avuQuery);
//			
//				avuQuery = AVUQueryElement.instanceForValueQuery(
//						AVUQueryPart.VALUE,
//						AVUQueryOperatorEnum.BETWEEN,
//						Long.toString(toDate.getTime()/1000));
//				avuQueryList.add(avuQuery);
//				
//			}
//			else {
//				avuQuery = AVUQueryElement.instanceForValueQuery(
//						AVUQueryPart.ATTRIBUTE,
//						AVUQueryOperatorEnum.EQUAL,
//						dateAttr);
//				avuQueryList.add(avuQuery);
//			
//				avuQuery = AVUQueryElement.instanceForValueQuery(
//						AVUQueryPart.VALUE,
//						AVUQueryOperatorEnum.GREATER_OR_EQUAL,
//						Long.toString(newFromDate));
//				avuQueryList.add(avuQuery);
//				
//			}
			
			// handle data format query
			if ((formatId != null) && (formatId.getValue() != null)) {
				avuQuery = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.ATTRIBUTE,
						AVUQueryOperatorEnum.EQUAL,
						formatAttr);
				avuQueryList.add(avuQuery);
			
				avuQuery = AVUQueryElement.instanceForValueQuery(
						AVUQueryPart.VALUE,
						AVUQueryOperatorEnum.EQUAL,
						formatId.getValue());
				avuQueryList.add(avuQuery);
				
			}
		} catch (JargonQueryException e) {
			log.error("createAVUQueryElementList: failed to construct AVU query");
			return avuQueryList;
		}
		
		return avuQueryList;
	}

}
