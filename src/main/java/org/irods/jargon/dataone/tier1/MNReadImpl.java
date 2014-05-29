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
import javax.ws.rs.core.MediaType;

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
import org.dataone.service.types.v1.ObjectList;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.ReplicationPolicy;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.Stream2StreamAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.domain.MNPermissionEnum;
import org.irods.jargon.dataone.events.EventLogAO;
import org.irods.jargon.dataone.utils.HandleUtils;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// TODO: NEED TO ADD LOGGING OF THESE EVENTS
// GETREPLICA IS SAME AS GET, EXCEPT SHOULD BE LOGGED AS REPLICATION

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
			throw new InvalidToken("400", "1402");
		}

		DataObject dataObject = new DataObject();
		Checksum checksum = new Checksum();
		ObjectFormatIdentifier formatIdentifier = new ObjectFormatIdentifier();
		BigInteger contentLength;
		Date lastModified;
		BigInteger serialVersion;
		
		try {
			// need last modified, content length, Content-Type: application/octet-stream, object format (mime), checksum, serial version
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleUtils.getDataObjectFromDataOneIdentifier(id);
			
			lastModified = dataObject.getUpdatedAt();
			
			Long contentLengthLong = dataObject.getDataSize();
			String contentLengthStr = contentLengthLong.toString();
			contentLength =  new BigInteger(contentLengthStr);
			
			// TODO: needs to be updated with correct data type from AVU
			String format = "application/zip";
			formatIdentifier.setValue(format);
			
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
				throw new NotFound("404", "1420");
			}
			checksum.setValue(csum);
			checksum.setAlgorithm("MD5");
			
			serialVersion = getSerialVersion();
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure(e.getMessage(), e.toString()); //TODO: fix this with correct exception
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return new DescribeResponse(formatIdentifier, contentLength, lastModified, checksum, serialVersion);
	}

	@Override
	public DescribeResponse describe(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		throw new NotImplemented("501", "1361");
	}
	
	public void streamObject(HttpServletResponse response, Identifier id) throws ServiceFailure, NotFound {
		
		IRODSFileInputStream stream = null;
		OutputStream output = null;
		int contentLength = 0;
		String path = new String();;
		
		try {
			// Find iRODS object
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
			DataObject dataObject = handleUtils.getDataObjectFromDataOneIdentifier(id);
			path = dataObject.getAbsolutePath();
			//String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
			//String path = "/dfcmain/home/lisa/test_this.txt";	
			IRODSFile irodsFile = irodsAccessObjectFactory
									.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);

			if (!irodsFile.exists()) {
				log.info("file does not exist");
				throw new NotFound("404", "1020");
			}

			stream = irodsAccessObjectFactory
					.getIRODSFileFactory(irodsAccount)
					.instanceIRODSFileInputStream(irodsFile);
			// TODO: need to catch and return appropriate exceptions here for no permission
			
			
			contentLength = (int) irodsFile.length();
			log.info("contentLength={}", contentLength);
			
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment;filename=" + "DFC-slide.pptx");
			response.setContentLength(contentLength);
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
			throw new ServiceFailure(e.getMessage(), e.toString()); //TODO: fix this with correct exception for read and getreplica
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
		throw new NotImplemented("501", "1001");
	}

	@Override
	public Checksum getChecksum(Identifier id, String algorithm)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented,
			ServiceFailure, NotFound {
		
		if (id == null || id.getValue().isEmpty()) {
			throw new InvalidToken("400", "1402");
		}
		
		if (algorithm == null || algorithm.isEmpty()) {
			throw new InvalidToken("400", "1402");
		}

		DataObject dataObject = new DataObject();
		Checksum checksum = new Checksum();
		
		try {
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleUtils.getDataObjectFromDataOneIdentifier(id);
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
				throw new NotFound("404", "1420");
			}
			checksum.setValue(csum);
			checksum.setAlgorithm("MD5");
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure(e.getMessage(), e.toString()); //TODO: fix this with correct exception
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return checksum;
	}

	@Override
	public Checksum getChecksum(Session arg0, Identifier arg1, String arg2)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented,
			ServiceFailure, NotFound {
		throw new NotImplemented("501", "1401");
	}

	@Override
	public InputStream getReplica(Identifier arg0) throws InvalidToken,
			NotAuthorized, NotImplemented, ServiceFailure, NotFound,
			InsufficientResources {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getReplica(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound, InsufficientResources {
		throw new NotImplemented("501", "2180");
	}

	@Override
	public SystemMetadata getSystemMetadata(Identifier id)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		if (id == null || id.getValue().isEmpty()) {
			throw new InvalidToken("400", "1402");
		}

		DataObject dataObject = new DataObject();
		SystemMetadata metadata = new SystemMetadata();
		
		// TODO: hardcode version to 1 for now
		metadata.setSerialVersion(getSerialVersion());
		
		Checksum checksum = new Checksum();
		
		try {
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
			dataObject = handleUtils.getDataObjectFromDataOneIdentifier(id);
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
//				throw new NotFound("404", "1420");
			}
			checksum.setValue(csum);
			checksum.setAlgorithm("MD5");
			
			metadata.setIdentifier(id);
//			metadata.setFormatId(formatId); TODO: need to retrieve iRODS metadata here set to be: DataONE Object Format
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
			
			AccessPolicy accessPolicy = new AccessPolicy();
			List<UserFilePermission> permissions = dataObjectAO.listPermissionsForDataObject(dataObject.getAbsolutePath());
			if (permissions != null) {
				for (UserFilePermission permission : permissions) {
					AccessRule rule = new AccessRule();
					Subject subject = new Subject();
					subject.setValue("uid=" + permission.getUserId());
					rule.addSubject(subject);
					List<Permission> d1Premissions = getD1Permission(permission);
					for(Permission d1Premission : d1Premissions) {
						rule.addPermission(d1Premission);
					}
				}	
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
			throw new ServiceFailure(e.getMessage(), e.toString()); //TODO: fix this with correct exception
		} finally {	  
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}	
		return metadata;
	}

	@Override
	public SystemMetadata getSystemMetadata(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		throw new NotImplemented("501", "1041");
	}

	@Override
	public ObjectList listObjects(Date arg0, Date arg1,
			ObjectFormatIdentifier arg2, Boolean arg3, Integer arg4,
			Integer arg5) throws InvalidRequest, InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectList listObjects(Session arg0, Date arg1, Date arg2,
			ObjectFormatIdentifier arg3, Boolean arg4, Integer arg5,
			Integer arg6) throws InvalidRequest, InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		throw new NotImplemented("501", "1521");
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
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
			// just try to access the object to see if is there or not
			handleUtils.getDataObjectFromDataOneIdentifier(pid);
		} catch(Exception ex) {
			throw new ServiceFailure("2161", "The identifier specified by " + 
                    syncFailed.getPid() + " was not found on this node.");
		}			
        
        EventLogAO eventLog = new EventLogAO(irodsAccessObjectFactory, restConfiguration);
        try {
			eventLog.recordEvent(Event.SYNCHRONIZATION_FAILED, pid, syncFailed.getDescription());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}
        
		return true;
	}

	@Override
	public boolean synchronizationFailed(Session arg0,
			SynchronizationFailed arg1) throws InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		throw new NotImplemented("501", "2160");
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
		}
		return permissions;
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
	
	private BigInteger getSerialVersion() {
		// TODO: hardcode version to 1 for now
		Long verLong = new Long(1);
		String verStr = verLong.toString();
		return new BigInteger(verStr);
	}

}
