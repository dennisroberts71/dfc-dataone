package org.irods.jargon.dataone.tier1;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectList;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.Stream2StreamAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: NEED TO ADD LOGGING OF THESE EVENTS
// GETREPLICA IS SAME AS GET, EXCEPT SHOULD BE LOGGED AS REPLICATION

public class MNReadImpl implements MNRead {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
    
    public MNReadImpl(
    			IRODSAccessObjectFactory irodsAccessObjectFactory,
    			RestConfiguration restConfiguration) {
    	
    	this.irodsAccessObjectFactory = irodsAccessObjectFactory;
    	this.restConfiguration = restConfiguration;
    }

	@Override
	public DescribeResponse describe(Identifier arg0) throws InvalidToken,
			NotAuthorized, NotImplemented, ServiceFailure, NotFound {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DescribeResponse describe(Session arg0, Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		throw new NotImplemented("501", "1361");
	}
	
	public void streamObject(HttpServletResponse response, String pid) throws ServiceFailure, NotFound {
		
		IRODSFileInputStream stream = null;
		OutputStream output = null;
		int contentLength = 0;
		
		Identifier id = new Identifier();
		id.setValue(pid);

		
		// Find iRODS object here from Identifier in metadata
		String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
		//String path = "/dfcmain/home/lisa/test_this.txt";	

		log.info("decoded path:{}", path);
		
		try {
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
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

		Checksum checksum = new Checksum();
		
		// Find iRODS object here from Identifier in metadata
		//String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
		String path = "/dfcmain/home/lisa/test_this.txt";	

		log.info("decoded path:{}", path);
		
		try {
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			IRODSFile irodsFile = irodsAccessObjectFactory
									.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
			if (!irodsFile.exists()) {
				log.info("file does not exist: {}", path);
				throw new NotFound("404", "1420");
			}
			log.info("found iRODS file: {}", irodsFile.getName());
			
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			String csum = dataObjectAO.findByAbsolutePath(path).getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", path);
				throw new NotFound("404", "1420");
			}
			checksum.setValue(csum);
			checksum.setAlgorithm("MD5");
			
		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", path);
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
	public SystemMetadata getSystemMetadata(Identifier arg0)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure,
			NotFound {
		// TODO Auto-generated method stub
		return null;
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
	public boolean synchronizationFailed(SynchronizationFailed arg0)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean synchronizationFailed(Session arg0,
			SynchronizationFailed arg1) throws InvalidToken, NotAuthorized,
			NotImplemented, ServiceFailure {
		throw new NotImplemented("501", "2160");
	}

}
