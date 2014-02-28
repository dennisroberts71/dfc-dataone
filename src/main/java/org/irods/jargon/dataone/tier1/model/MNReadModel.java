package org.irods.jargon.dataone.tier1.model;

import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.ws.rs.WebApplicationException;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MNReadModel {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
	
	public MNReadModel(IRODSAccessObjectFactory irodsAccessObjectFactory, RestConfiguration restConfiguration) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.restConfiguration = restConfiguration;
	}
	
	public InputStream doGet(Identifier id) throws
											InvalidToken,
											NotAuthorized,
											NotImplemented,
											ServiceFailure,
											NotFound,
											InsufficientResources {
		InputStream input = null;

		log.info("doGet()");

		if (id == null || id.toString().isEmpty()) {
			throw new NotFound("invalid", "identifier is invalid");
		}
		
		String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
	

		log.info("decoded path:{}", path);

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			IRODSFile irodsFile = irodsAccessObjectFactory
									.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);

			if (!irodsFile.exists()) {
				log.info("file does not exist");
				throw new NotFound("404", "The iRODS member node can't find object requested - "+id.toString());
			}

			input = irodsAccessObjectFactory
					.getIRODSFileFactory(irodsAccount)
					.instanceIRODSFileInputStream(irodsFile);

//			int contentLength = (int) irodsFile.length();
//
//			response.setContentType("application/octet-stream");
//			response.setContentLength(contentLength);
//			response.setHeader("Content-disposition", "attachment; filename=\""
//					+ decodedPathString + "\"");
//
//			OutputStream output;
//			try {
//				output = new BufferedOutputStream(response.getOutputStream());
//			} catch (IOException ioe) {
//				log.error(
//						"io exception getting output stream to download file",
//						ioe);
//				throw new JargonException("exception downloading iRODS data",
//						ioe);
//			}
//			Stream2StreamAO stream2StreamAO = getIrodsAccessObjectFactory()
//					.getStream2StreamAO(irodsAccount);
//			stream2StreamAO.streamToStreamCopyUsingStandardIO(input, output);
		} catch (Exception e) {
			throw new ServiceFailure(e.getMessage(), e.toString()); //TODO: fix this with correct exception
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();

		}
		
		return input;

	}

}
