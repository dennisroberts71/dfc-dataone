package org.irods.jargon.dataone.tier1;

import org.apache.commons.io.IOUtils;
import org.dataone.service.exceptions.*;
import org.dataone.service.mn.tier1.v1.MNRead;
import org.dataone.service.types.v1.*;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.config.CommonConfig;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceAO;
import org.irods.jargon.dataone.events.EventData;
import org.irods.jargon.dataone.model.DataOneObject;
import org.irods.jargon.dataone.pidservice.AbstractDataOnePidServiceAO;
import org.irods.jargon.dataone.plugin.PluginDiscoveryService;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;

public class MNReadImpl implements MNRead {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private final PublicationContext publicationContext;
	private final PluginDiscoveryService pluginDiscoveryService;
	private PropertiesLoader properties = new PropertiesLoader();

	/**
	 * @param publicationContext
	 * @param pluginDiscoveryService
	 */
	public MNReadImpl(final PublicationContext publicationContext,
					  final PluginDiscoveryService pluginDiscoveryService) {
		super();
		this.publicationContext = publicationContext;
		this.pluginDiscoveryService = pluginDiscoveryService;
	}

	private IRODSAccount getIRODSAccount() throws ServiceFailure {
		try {
			return RestAuthUtils.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
		} catch (Exception ex) {
			log.error("Error getting iRODS Account: {}", ex.toString());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	private AbstractDataOneRepoServiceAO getRepoService() throws ServiceFailure {
		return getRepoService(getIRODSAccount());
	}

	private AbstractDataOneRepoServiceAO getRepoService(IRODSAccount irodsAccount) throws ServiceFailure {
		try {
			return pluginDiscoveryService.instanceRepoService(irodsAccount);
		} catch (Exception ex) {
			log.error("Error getting repo service instance: {}", ex.toString());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	private AbstractDataOnePidServiceAO getPidService() throws ServiceFailure {
		return getPidService(getIRODSAccount());
	}

	private AbstractDataOnePidServiceAO getPidService(IRODSAccount irodsAccount) throws ServiceFailure {
		try {
			return pluginDiscoveryService.instancePidService(irodsAccount);
		} catch (Exception ex) {
			log.error("Error getting PID service instance: {}", ex.toString());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	private AbstractDataOneEventServiceAO getEventService(IRODSAccount irodsAccount) throws ServiceFailure {
		try {
			return pluginDiscoveryService.instanceEventService(irodsAccount);
		} catch (Exception ex) {
			log.error("Error getting event service instance: {}", ex.toString());
			throw new ServiceFailure("2161", ex.getMessage());
		}
	}

	private DataOneObject getDataOneObject(AbstractDataOnePidServiceAO pidService, Identifier id)
			throws ServiceFailure {
		try {
			return pidService.getObject(id);
		} catch (Exception ex) {
			log.error("Error looking up DataOne object: {}", ex.getMessage());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	private void validateIdentifier(Identifier id) throws NotFound {
		if (id == null || id.getValue().isEmpty()) {
			log.error("id is null or empty");
			throw new NotFound("1402", "invalid DataOne object id");
		}
	}

	@Override
	public DescribeResponse describe(final Identifier id)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {

		// Validate the identifier.
		validateIdentifier(id);

		// Look up the DataOne object.
		AbstractDataOnePidServiceAO pidService = getPidService();
		DataOneObject dataOneObject = getDataOneObject(pidService, id);
		if (dataOneObject == null) {
			throw new NotFound("1380", "The specified object does not exist on this node.");
		}

		// Return the description.
		try {
			return dataOneObject.describe();
		} catch (Exception ex) {
			log.error("Error obtaining DataOne object description: {}", ex.getMessage());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	@Override
	public DescribeResponse describe(final Session arg0, final Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {
		throw new NotImplemented("1361", "this service has not been implemented");
	}

	public void streamObject(final HttpServletResponse response, final Identifier id) throws ServiceFailure, NotFound {

		// Validate the parameters.
		if (response == null) {
			throw new NullPointerException("No response object provided.");
		}
		validateIdentifier(id);

		// Look up the DataOne object.
		AbstractDataOnePidServiceAO pidService = getPidService();
		DataOneObject dataOneObject = getDataOneObject(pidService, id);
		if (dataOneObject == null) {
			throw new NotFound("1030", "The specified object does not exist on this node.");
		}

		// Build the response.
		try {
			response.setContentType("application/octet-stream");
			response.setContentLength(dataOneObject.getSize().intValue());
			response.setHeader("Content-Disposition", "attachment;filename=" + dataOneObject.getName());

			// Copy the object contents to the output stream.
			InputStream stream = dataOneObject.getInputStream();
			IOUtils.copy(stream, response.getOutputStream());
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(response.getOutputStream());
		} catch (Exception ex) {
			log.error("Error streaming DataOne object: {}", ex.getMessage());
			throw new ServiceFailure("1030", "Unable to stream object.");
		}
	}

	@Override
	public IRODSFileInputStream get(final Identifier id)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound, InsufficientResources {

		// TODO: Not sure how to implement this properly - used streamObject
		// method instead
		IRODSFileInputStream stream = null;

		// if (id == null || id.toString().isEmpty()) {
		// throw new NotFound("invalid", "identifier is invalid");
		// }

		// String path = "/dfcmain/home/DFC-public/DFC-slide.pptx";
		// String path = "/dfcmain/home/lisa/test_this.txt";

		// log.info("decoded path:{}", path);
		//
		// try {
		// IRODSAccount irodsAccount = RestAuthUtils
		// .getIRODSAccountFromBasicAuthValues(restConfiguration);
		// IRODSFile irodsFile = irodsAccessObjectFactory
		// .getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		//
		// if (!irodsFile.exists()) {
		// log.info("file does not exist");
		// throw new NotFound("404",
		// "The iRODS member node can't find object requested -
		// "+id.toString());
		// }
		//
		// stream = irodsAccessObjectFactory
		// .getIRODSFileFactory(irodsAccount)
		// .instanceIRODSFileInputStream(irodsFile);
		//
		// } catch (Exception e) {
		// throw new ServiceFailure(e.getMessage(), e.toString());
		// } finally {
		// irodsAccessObjectFactory.closeSessionAndEatExceptions();
		//
		// }

		return stream;
	}

	@Override
	public InputStream get(final Session arg0, final Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound, InsufficientResources {
		throw new NotImplemented("1001", "this service has not been implemented");
	}

	@Override
	public Checksum getChecksum(final Identifier id, final String algorithm)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {

		// Validate the parameters.
		validateIdentifier(id);
		if (algorithm == null || algorithm.isEmpty()) {
			throw new NotFound("1420", "invalid checksum algorithm provided");
		}

		// Look up the DataOne object.
		AbstractDataOnePidServiceAO pidService = getPidService();
		DataOneObject dataOneObject = getDataOneObject(pidService, id);
		if (dataOneObject == null) {
			throw new NotFound("1020", "The specified object does not exist on this node.");
		}

		try {
			return dataOneObject.getChecksum();
		} catch (Exception ex) {
			log.error("Error obtaining checksum for DataOne object: {}", ex.getMessage());
			throw new ServiceFailure("1020", "Unable to retrieve checksum.");
		}
	}

	@Override
	public Checksum getChecksum(final Session arg0, final Identifier arg1, final String arg2)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {
		throw new NotImplemented("1401", "this service has not been implemented");
	}

	@Override
	public InputStream getReplica(final Identifier arg0)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound, InsufficientResources {
		// This is implemented in streamObject
		return null;
	}

	@Override
	public InputStream getReplica(final Session arg0, final Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound, InsufficientResources {
		throw new NotImplemented("2180", "this service has not been implemented");
	}

	@Override
	public SystemMetadata getSystemMetadata(final Identifier id)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {

		// Validate the parameters.
		validateIdentifier(id);

		// Look up the DataOne object.
		AbstractDataOnePidServiceAO pidService = getPidService();
		DataOneObject dataOneObject = getDataOneObject(pidService, id);
		if (dataOneObject == null) {
			throw new NotFound("1060", "The specified object does not exist on this node.");
		}

		try {
			return dataOneObject.getSystemMetadata();
		} catch (Exception e) {
			log.error("Unable to get system metadata: {}", e.getMessage());
			throw new ServiceFailure("1090", e.getMessage());
		}
	}

	@Override
	public SystemMetadata getSystemMetadata(final Session arg0, final Identifier arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure, NotFound {
		throw new NotImplemented("1041", "this service has not been implemented");
	}

	@Override
	public ObjectList listObjects(final Date fromDate, final Date toDate, final ObjectFormatIdentifier formatId,
								  final Boolean replicaStatus, final Integer start, final Integer count)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {

		// Get the list of DataOne objects.
		AbstractDataOneRepoServiceAO repoService = getRepoService();
		try {
			return repoService.getExposedObjects(fromDate, toDate, formatId, replicaStatus, start, count)
					.toObjectList();
		} catch (Exception e) {
			log.error("Unable to list DataOne objects: {}", e.toString());
			throw new ServiceFailure("1580", "Could not retrieve list of DataOne objects");
		}
	}

	@Override
	public ObjectList listObjects(final Session arg0, final Date arg1, final Date arg2,
								  final ObjectFormatIdentifier arg3, final Boolean arg4, final Integer arg5, final Integer arg6)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
		throw new NotImplemented("1521", "this service has not been implemented");
	}

	@Override
	public boolean synchronizationFailed(final SynchronizationFailed syncFailed)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {

		// Validate the arguments.
		if (syncFailed == null) {
			throw new ServiceFailure("2161", "The synchronization failed object cannot be null.");
		}
		if (syncFailed.getPid() == null || syncFailed.getPid().isEmpty()) {
			throw new ServiceFailure("2161", "The identifier cannot be null or empty.");
		}

		// Look up the data object.
		IRODSAccount account = getIRODSAccount();
		Identifier id = new Identifier();
		id.setValue(syncFailed.getPid());
		AbstractDataOnePidServiceAO pidService = getPidService(account);
		DataOneObject dataOneObject = getDataOneObject(pidService, id);
		if (dataOneObject == null) {
			throw new ServiceFailure("2161", "The specified object does not exist on this node.");
		}

		// Log the event.
		AbstractDataOneEventServiceAO eventService = getEventService(account);
		try {
			EventData eventData = new EventData();
			eventData.setDescription("DataOne replication");
			eventData.setEvent(Event.SYNCHRONIZATION_FAILED);
			eventData.setId(id);
			eventData.setIrodsPath(dataOneObject.getPath());
			eventData.setNodeIdentifier(CommonConfig.getDataOneNodeId(publicationContext));
			eventData.setUserAgent(account.getUserName());
			eventService.recordEvent(eventData);
		} catch (PluginNotFoundException | JargonException e) {
			log.error("Failed to log Synchronization Failure event: {}", e.toString());
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}

		return true;
	}

	@Override
	public boolean synchronizationFailed(final Session arg0, final SynchronizationFailed arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
		throw new NotImplemented("2160", "this service has not been implemented");
	}
}
