package org.irods.jargon.dataone.utils;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleUtils {
	
	private static RestConfiguration restConfiguration;
	private static IRODSAccessObjectFactory irodsAccessObjectFactory;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public HandleUtils(RestConfiguration restConfiguration,
			IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.restConfiguration = restConfiguration;
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
	}
	
	public DataObject getDataObjectFromDataOneIdentifier(
			Identifier pid) throws Exception {
		
		DataObject dataObject = null;
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(pid);
	
		try {	
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			// Find iRODS object here from Identifier
			dataObject = dataObjectAO.findById(new Long(dataObjectId).intValue());
			log.info("found iRODS file: {}", dataObject.getAbsolutePath());
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new Exception(e.getMessage()); //TODO: fix this with correct exception
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
		
		return dataObject;
	}
	
	public long getDataObjectIdFromDataOneIdentifier(
			Identifier pid) throws ServiceFailure {
		
		int idIdx = pid.getValue().indexOf("/");
		long dataObjectId = Long.parseLong(pid.getValue().substring(idIdx));
		
		return dataObjectId;	
	}

}
