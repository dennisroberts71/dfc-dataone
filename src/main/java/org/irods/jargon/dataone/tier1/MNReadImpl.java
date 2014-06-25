package org.irods.jargon.dataone.tier1;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.exceptions.SynchronizationFailed;
import org.dataone.service.mn.tier1.v1.MNRead;
import org.dataone.service.types.v1.AccessPolicy;
import org.dataone.service.types.v1.AccessRule;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.NodeReference;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ObjectList;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.ReplicationPolicy;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataprofile.DataProfileService;
import org.irods.jargon.dataprofile.DataTypeResolutionService;
import org.irods.jargon.dataprofile.DataTypeResolutionServiceImpl;
import org.irods.jargon.dataprofile.DataProfileServiceImpl;
import org.irods.jargon.dataprofile.DataProfile;
//import org.irods.jargon.core.pub.Stream2StreamAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.domain.MNPermissionEnum;
import org.irods.jargon.dataone.events.EventLogAOElasticSearchImpl;
import org.irods.jargon.dataone.id.DataObjectListResponse;
import org.irods.jargon.dataone.id.UniqueIdAOHandleImpl;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MNReadImpl implements MNRead {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
	
	private Properties properties;
	private String propertiesFilename = "d1client.properties";
    
    public MNReadImpl(
    			IRODSAccessObjectFactory irodsAccessObjectFactory,
    			RestConfiguration restConfiguration) {
    	
    	this.irodsAccessObjectFactory = irodsAccessObjectFactory;
    	this.restConfiguration = restConfiguration;
    	
    	loadProperties();
    }

	@Override
	public DescribeResponse describe(Identifier id) throws InvalidToken,
			NotAuthorized, NotImplemented, ServiceFailure, NotFound {
		
		if (id == null || id.getValue().isEmpty()) {
			throw new NotFound("1402", "invalid iRODS data object id");
		}

		DataObject dataObject = new DataObject();
		Checksum checksum = new Checksum();
		ObjectFormatIdentifier formatIdentifier = new ObjectFormatIdentifier();
		BigInteger contentLength;
		Date lastModified;
		BigInteger serialVersion;
		
		try {
			// need last modified, content length, Content-Type: application/octet-stream, object format (mime), checksum, serial version	
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleImpl.getDataObjectFromIdentifier(id);
		} catch (Exception e) {
			throw new NotFound("1380", "The specified object does not exist on this node.");
		}
			
		try {
			lastModified = dataObject.getUpdatedAt();
			
			Long contentLengthLong = dataObject.getDataSize();
			String contentLengthStr = contentLengthLong.toString();
			contentLength =  new BigInteger(contentLengthStr);
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			String format = getDataObjectMimeType(irodsAccount, dataObject);
			formatIdentifier.setValue(format);
			
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
				//throw new NotFound("404", "1420");
			}
			else {
				checksum.setValue(csum);
				checksum.setAlgorithm("MD5");
			}
			
			serialVersion = getSerialVersion();
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure("1390", e.getMessage());
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return new DescribeResponse(formatIdentifier, contentLength, lastModified, checksum, serialVersion);
	}

	@Override
	public DescribeResponse describe(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		throw new NotImplemented("1361", "this service has not been implemented");
	}
	
	public void streamObject(HttpServletResponse response, Identifier id) throws ServiceFailure, NotFound {
		
		IRODSFileInputStream stream = null;
		OutputStream output = null;
		int contentLength = 0;
		String path = new String();
		DataObject dataObject = new DataObject();
		IRODSAccount irodsAccount;
		IRODSFile irodsFile;
		
		// first try and find data object for this id
		try {
			irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleImpl.getDataObjectFromIdentifier(id);
			path = dataObject.getAbsolutePath();	
			irodsFile = irodsAccessObjectFactory
									.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);

			if (!irodsFile.exists()) {
				log.info("file does not exist");
				throw new NotFound("1020", "No data object could be found for given PID:" + id.getValue());
			}
		} catch(Exception ex) {
			log.info("file does not exist");
			throw new NotFound("1020", "No data object could be found for given PID:" + id.getValue());
		}
		
		// now try and stream it
		try {

			stream = irodsAccessObjectFactory
					.getIRODSFileFactory(irodsAccount)
					.instanceIRODSFileInputStream(irodsFile);
			// TODO: need to catch and return appropriate exceptions here for no permission
			
			
			contentLength = (int) irodsFile.length();
			log.info("contentLength={}", contentLength);
			
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment;filename=" + dataObject.getDataName());
			response.setContentLength(contentLength);
			response.addHeader("Vary", "Accept-Encoding");
			log.info("reponse: {}", response.toString());
			
			output = new BufferedOutputStream(response.getOutputStream());
			
//			Stream2StreamAO stream2StreamAO = getIrodsAccessObjectFactory()
//					.getStream2StreamAO(irodsAccount);
//			stream2StreamAO.streamToStreamCopyUsingStandardIO(input, output);

			int readBytes = 0;  
		    byte [] buffer = new byte [4096];  
		        
		    while ((readBytes = stream.read (buffer,0,4096)) != -1) { 
		    	log.info("readBytes={}", readBytes);
		        output.write (buffer,0,readBytes);  
		    }
		    output.flush();
		    if(stream != null)  
		        stream.close();  
		    if(output != null)  
		    	output.close();
			
		} catch (Exception e) {
			log.error("Cannot stream iRODS object: {}", path);
			throw new ServiceFailure("1030", "unable to stream iRODS data object");
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
	}

	@Override
	public IRODSFileInputStream get(Identifier id) throws InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure, NotFound, InsufficientResources {
		
		// TODO: Not sure how to implement this properly - used streamObject method instead
		IRODSFileInputStream stream = null;

//		if (id == null || id.toString().isEmpty()) {
//			throw new NotFound("invalid", "identifier is invalid");
//		}
		
//		String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
//		String path = "/dfcmain/home/lisa/test_this.txt";
	

//		log.info("decoded path:{}", path);
//
//		try {
//			IRODSAccount irodsAccount = RestAuthUtils
//					.getIRODSAccountFromBasicAuthValues(restConfiguration);
//			IRODSFile irodsFile = irodsAccessObjectFactory
//									.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
//
//			if (!irodsFile.exists()) {
//				log.info("file does not exist");
//				throw new NotFound("404", "The iRODS member node can't find object requested - "+id.toString());
//			}
//
//			stream = irodsAccessObjectFactory
//					.getIRODSFileFactory(irodsAccount)
//					.instanceIRODSFileInputStream(irodsFile);
//
//		} catch (Exception e) {
//			throw new ServiceFailure(e.getMessage(), e.toString());
//		} finally {
//			irodsAccessObjectFactory.closeSessionAndEatExceptions();
//
//		}

		return stream;
	}

	@Override
	public InputStream get(Session arg0, Identifier arg1) throws InvalidToken,
			NotAuthorized, NotImplemented, ServiceFailure, NotFound,
			InsufficientResources {
		throw new NotImplemented("1001", "this service has not been implemented");
	}

	@Override
	public Checksum getChecksum(Identifier id, String algorithm)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented,
			ServiceFailure, NotFound {
		
		if (id == null || id.getValue().isEmpty()) {
			throw new NotFound("1420", "invalid iRODS data object id");
		}
		
		if (algorithm == null || algorithm.isEmpty()) {
			throw new NotFound("1420", "invalid checksum algorithm provided");
		}

		DataObject dataObject = new DataObject();
		Checksum checksum = new Checksum();
		
		try {
			
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleImpl.getDataObjectFromIdentifier(id);
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
				throw new NotFound("1410", "checksum does not exist for data object id provided");
			}
			checksum.setValue(csum);
			checksum.setAlgorithm("MD5");
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure("1410", e.getMessage());
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return checksum;
	}

	@Override
	public Checksum getChecksum(Session arg0, Identifier arg1, String arg2)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented,
			ServiceFailure, NotFound {
		throw new NotImplemented("1401", "this service has not been implemented");
	}

	@Override
	public InputStream getReplica(Identifier arg0) throws InvalidToken,
			NotAuthorized, NotImplemented, ServiceFailure, NotFound,
			InsufficientResources {
		// This is implemented in streamObject
		return null;
	}

	@Override
	public InputStream getReplica(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound, InsufficientResources {
		throw new NotImplemented("2180", "this service has not been implemented");
	}

	@Override
	public SystemMetadata getSystemMetadata(Identifier id)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		if (id == null || id.getValue().isEmpty()) {
			throw new InvalidToken("1402", "invalid iRODS data object id");
		}

		DataObject dataObject = new DataObject();
		SystemMetadata metadata = new SystemMetadata();
		IRODSAccount irodsAccount;
		DataObjectAO dataObjectAO;
		
		// TODO: hardcode version to 1 for now
		metadata.setSerialVersion(getSerialVersion());
		
		Checksum checksum = new Checksum();
		
		// first try and find data object for this id
		try {
			irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleImpl.getDataObjectFromIdentifier(id);

		} catch(Exception ex) {
			log.info("file does not exist");
			throw new NotFound("1060", "No metadata could be found for given PID:" + id.getValue());
		}
		
		try {
			
//			IRODSAccount irodsAccount = RestAuthUtils
//					.getIRODSAccountFromBasicAuthValues(restConfiguration);
//			
//			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
//			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
//			dataObject = handleImpl.getDataObjectFromIdentifier(id);
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
//				throw new NotFound("404", "1420");
			}
			else {
				checksum.setValue(csum);
				checksum.setAlgorithm("MD5");
			}
			
			String mimeType = getDataObjectMimeType(irodsAccount, dataObject);
			metadata.setIdentifier(id);
			ObjectFormatIdentifier formatId = new ObjectFormatIdentifier();
			formatId.setValue(mimeType);
			metadata.setFormatId(formatId);
			
			Long dataSizeLong = new Long(dataObject.getDataSize());
			String dataSizeStr = dataSizeLong.toString();
			metadata.setSize(new BigInteger(dataSizeStr));
			metadata.setChecksum(checksum);
			
			String dataOwner = "uid=" + dataObject.getDataOwnerName();
			Subject submitter = new Subject();
			submitter.setValue(dataOwner);
			metadata.setSubmitter(submitter);
			
			Subject rightsHolder = new Subject();
			rightsHolder.setValue(dataOwner);
			metadata.setRightsHolder(rightsHolder);
			
			List<UserFilePermission> permissions = dataObjectAO.listPermissionsForDataObject(dataObject.getAbsolutePath());
			if (permissions != null) {
				AccessPolicy accessPolicy = new AccessPolicy();
				for (UserFilePermission permission : permissions) {
					AccessRule rule = new AccessRule();
					Subject subject = new Subject();
					subject.setValue("uid=" + permission.getUserName());
					rule.addSubject(subject);
					List<Permission> d1Premissions = getD1Permission(permission);
					for(Permission d1Premission : d1Premissions) {
						rule.addPermission(d1Premission);
					}
					accessPolicy.addAllow(rule);
				}
				metadata.setAccessPolicy(accessPolicy);
			}
			
			ReplicationPolicy replicationPolicy = new ReplicationPolicy();
			replicationPolicy.setReplicationAllowed(false);
			metadata.setReplicationPolicy(replicationPolicy);
			
			// Add support for obsoletes or obsoletedBy?
			
			// TODO: May eventually want do add support for dateUploaded and dateSysMetadataModified
			// use data object modified date for now
			metadata.setDateSysMetadataModified(dataObject.getUpdatedAt());
			
			NodeReference nodeReference = new NodeReference();
			nodeReference.setValue(properties.getProperty("irods.dataone.identifier"));
			metadata.setOriginMemberNode(nodeReference);
			metadata.setAuthoritativeMemberNode(nodeReference);		
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure("1090", e.getMessage());
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return metadata;
	}

	@Override
	public SystemMetadata getSystemMetadata(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		throw new NotImplemented("1041", "this service has not been implemented");
	}

	@Override
	public ObjectList listObjects(
			Date fromDate,
			Date toDate,
			ObjectFormatIdentifier formatId,
			Boolean replicaStatus,
			Integer start,
			Integer count) throws
				InvalidRequest,
				InvalidToken,
				NotAuthorized,
				NotImplemented,
				ServiceFailure {
		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();
		List<DataObject> dataObjectList = new ArrayList<DataObject>();
		List<ObjectInfo> objectInfoList = new ArrayList<ObjectInfo>();
		ObjectList objectList = new ObjectList();
		UniqueIdAOHandleImpl handleImpl;
		
		try {
			handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);

			dataObjectListResponse = handleImpl.getListOfDataoneExposedDataObjects(
						fromDate,
						toDate,
						formatId,
						replicaStatus,
						start,
						count);
		} catch(Exception ex) {
			log.info("{}", ex.toString());
			throw new ServiceFailure("1580", "Could not retrieve list of data objects");
		}
		
		dataObjectList = dataObjectListResponse.getDataObjects();
		
		for (DataObject dObject : dataObjectList) {
			
			ObjectInfo oInfo = new ObjectInfo();
			
			if (dObject.getChecksum() != null) {
				Checksum checksum = new Checksum();
				checksum.setValue(dObject.getChecksum());
				checksum.setAlgorithm("MD5"); //TODO: put this in properties file?
				oInfo.setChecksum(checksum);
			}
			
			oInfo.setDateSysMetadataModified(dObject.getUpdatedAt());
			
			IRODSAccount irodsAccount;
			String mimeType = null;
			try {
				irodsAccount = RestAuthUtils
						.getIRODSAccountFromBasicAuthValues(restConfiguration);
				mimeType = getDataObjectMimeType(irodsAccount, dObject);
			} catch (Exception e1) {
				log.error("cannot retrieve mime type for object:{} setting to application/octet-stream",
						dObject.getAbsolutePath());
				mimeType = "application/octet-stream";
			}
			ObjectFormatIdentifier fId = new ObjectFormatIdentifier();
			fId.setValue(mimeType);
			oInfo.setFormatId(fId);
			
			Identifier id;
			try {
				id = handleImpl.getIdentifierFromDataObject(dObject);
			} catch (JargonException e) {
				log.error("could not convert data object id to identifier: {}", e.toString());
				throw new ServiceFailure("1580", "Could not retrieve list of data objects");
			}
			oInfo.setIdentifier(id);
			
			Long dataSizeLong = new Long(dObject.getDataSize());
			String dataSizeStr = dataSizeLong.toString();
			oInfo.setSize(new BigInteger(dataSizeStr));
			
			objectInfoList.add(oInfo);
		}

		objectList.setObjectInfoList(objectInfoList);
		objectList.setTotal(dataObjectListResponse.getTotal());
		objectList.setCount(objectInfoList.size());
		objectList.setStart(start);
		
		return objectList;
	}

	@Override
	public ObjectList listObjects(Session arg0, Date arg1, Date arg2,
			ObjectFormatIdentifier arg3, Boolean arg4, Integer arg5,
			Integer arg6) throws InvalidRequest, InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		throw new NotImplemented("1521", "this service has not been implemented");
	}

	@Override
	public boolean synchronizationFailed(SynchronizationFailed syncFailed)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
		
		Identifier pid = null;
		
		if (syncFailed.getPid() != null) {
			pid = new Identifier();
			pid.setValue(syncFailed.getPid());
		}
		else {
			throw new ServiceFailure("2161", "The identifier cannot be null.");
		}
		
		// check to make sure pid is valid
		try {
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			// just try to access the object to see if is there or not
			handleImpl.getDataObjectFromIdentifier(pid);
		} catch(Exception ex) {
			throw new ServiceFailure("2161", "The identifier specified by " + 
                    syncFailed.getPid() + " was not found on this node.");
		}			
        
        EventLogAOElasticSearchImpl eventLog = new EventLogAOElasticSearchImpl(irodsAccessObjectFactory, restConfiguration);
        try {
			eventLog.recordEvent(Event.SYNCHRONIZATION_FAILED, pid, syncFailed.getDescription());
		} catch (Exception e) {
			log.error("failed to log synchronization failed event: {}", e.toString());
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}
        
		return true;
	}

	@Override
	public boolean synchronizationFailed(Session arg0,
			SynchronizationFailed arg1) throws InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		throw new NotImplemented("2160", "this service has not been implemented");
	}
	
	// need to return every DataONE permission implied by iRODS permissions i.e. 
	// write would require read and write to be listed as DataONE permissions and
	// own would include read, write, and changePermission
	private List<Permission> getD1Permission(UserFilePermission p) {
		List<Permission> permissions = new ArrayList<Permission>();
		FilePermissionEnum fpEnum = p.getFilePermissionEnum();
		switch(fpEnum) {
		case READ:
			permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
			break;
			
		case WRITE:
			permissions.add(Permission.READ);
			permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
			break;
			
		case OWN:
			permissions.add(Permission.READ);
			permissions.add(Permission.WRITE);
			permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
			break;
		default:
			permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
			break;
		}
		return permissions;
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
	
	private BigInteger getSerialVersion() {
		// TODO: hardcode version to 1 for now
		Long verLong = new Long(1);
		String verStr = verLong.toString();
		return new BigInteger(verStr);
	}
	
	private String getDataObjectMimeType(IRODSAccount irodsAccount, DataObject dataObject)
				throws FileNotFoundException, JargonException {
		String mimeType = null;
		
		DataTypeResolutionService resolutionService = new DataTypeResolutionServiceImpl(
				irodsAccessObjectFactory, irodsAccount);
		DataProfileService dataProfileService = new DataProfileServiceImpl(
				irodsAccessObjectFactory, irodsAccount, resolutionService);
		
		@SuppressWarnings("unchecked")
		DataProfile<DataObject> dataProfile = dataProfileService.retrieveDataProfile(dataObject.getAbsolutePath());
		mimeType = dataProfile.getMimeType();
		
		return mimeType;
		
	}

}
