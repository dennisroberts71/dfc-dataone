package org.irods.jargon.dataone.id.handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.service.AbstractJargonService;
import org.irods.jargon.pid.pidservice.UniqueIdAO;
//import org.irods.jargon.dataprofile.DataProfile;
//import org.irods.jargon.dataprofile.DataProfileService;
//import org.irods.jargon.dataprofile.DataProfileServiceImpl;
//import org.irods.jargon.dataprofile.DataTypeResolutionService;
//import org.irods.jargon.dataprofile.DataTypeResolutionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueIdAOHandleImpl extends AbstractJargonService implements UniqueIdAO {

	private Properties properties;
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public DataObject getDataObjectFromIdentifier(final Identifier identifier)
			throws JargonException, FileNotFoundException {

		DataObject dataObject = null;
		log.info("retrieving irods data object id from identifier: {}", identifier.getValue());
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(identifier);
		log.info("got id: {}", dataObjectId);

		if (dataObjectId < 0) {
			throw new FileNotFoundException("");
		}

		try {

			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			// Find iRODS object here from Identifier
			dataObject = dataObjectAO.findById(new Long(dataObjectId).intValue());
			if (dataObject != null) {
				log.info("found iRODS file: {}", dataObject.getAbsolutePath());
			} else {
				log.warn("did not find data object for id={}", dataObjectId);
			}

			// } catch (Exception e) {
			// log.warn("Cannot access iRODS object for id={}", dataObjectId);
			// throw e;
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

		return dataObject;
	}

	@Override
	public Identifier getIdentifierFromDataObject(final DataObject dataObject) throws JargonException {

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
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {

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

	public long getDataObjectIdFromDataOneIdentifier(final Identifier pid) {

		int idIdx = pid.getValue().indexOf("/") + 1;
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
		properties = new Properties();
		InputStream input = null;
		input = getClass().getClassLoader().getResourceAsStream(propertiesFilename);

		// load a properties file
		try {
			properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", propertiesFilename);
			log.error("IOException: {}", e.getStackTrace());
			properties = new Properties();
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

}
