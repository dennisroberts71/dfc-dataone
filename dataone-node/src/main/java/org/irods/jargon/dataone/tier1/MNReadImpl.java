package org.irods.jargon.dataone.tier1;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.exceptions.SynchronizationFailed;
import org.dataone.service.mn.tier1.v1.MNRead;
import org.dataone.service.types.v1.*;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.ConfigConstants;
import org.irods.jargon.dataone.configuration.PluginDiscoveryService;
import org.irods.jargon.dataone.configuration.PluginNotFoundException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.domain.MNPermissionEnum;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.irods.jargon.dataone.events.EventData;
import org.irods.jargon.dataone.reposervice.DataObjectListResponse;
import org.irods.jargon.dataone.reposervice.DataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.model.DataOneObject;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.irods.jargon.pid.pidservice.UniqueIdAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private DataOneRepoServiceAO getRepoService() throws ServiceFailure {
		return getRepoService(getIRODSAccount());
	}

	private DataOneRepoServiceAO getRepoService(IRODSAccount irodsAccount) throws ServiceFailure {
		try {
			return pluginDiscoveryService.instanceRepoService(irodsAccount);
		} catch (Exception ex) {
			log.error("Error getting repo service instance: {}", ex.toString());
			throw new ServiceFailure("1390", ex.getMessage());
		}
	}

	private DataOneObject getDataOneObject(DataOneRepoServiceAO repoService, Identifier id) throws ServiceFailure {
		try {
			return repoService.getObject(id);
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
		DataOneRepoServiceAO repoService = getRepoService();
		DataOneObject dataOneObject = getDataOneObject(repoService, id);
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

		// Validate the response object.
		if (response == null) {
			throw new NullPointerException("No respose object provided.");
		}

		// Validate the identifier.
		validateIdentifier(id);

		// Look up the DataOne object.
		DataOneRepoServiceAO repoService = getRepoService();
		DataOneObject dataOneObject = getDataOneObject(repoService, id);
		if (dataOneObject == null) {
			throw new NotFound("1030", "The specified object does not exist on this node.");
		}

		// Build the response.
		try {
			response.setContentType("application/octet-stream");
			response.setContentLength((int) dataOneObject.getSize());
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

		if (id == null || id.getValue().isEmpty()) {
			throw new NotFound("1420", "invalid iRODS data object id");
		}

		if (algorithm == null || algorithm.isEmpty()) {
			throw new NotFound("1420", "invalid checksum algorithm provided");
		}

		Checksum checksum = new Checksum();

		DataObject dataObject;
		String path;
		IRODSAccount irodsAccount;
		IRODSFile irodsFile;
		// first try and find data object for this id
		try {
			irodsAccount = RestAuthUtils.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());

			UniqueIdAO pidService = pluginDiscoveryService.instanceUniqueIdService(irodsAccount);
			dataObject = pidService.getDataObjectFromIdentifier(id);

			path = dataObject.getAbsolutePath();
			irodsFile = publicationContext.getIrodsAccessObjectFactory().getIRODSFileFactory(irodsAccount)
					.instanceIRODSFile(path);

			if (!irodsFile.exists()) {
				log.info("file does not exist");
				throw new NotFound("1020", "No data object could be found for given PID:" + id.getValue());
			}

		} catch (Exception ex) {
			log.info("file does not exist");
			throw new NotFound("1020", "No data object could be found for given PID:" + id.getValue());
		}

		String csum = dataObject.getChecksum();
		if (csum == null) {
			log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
			throw new NotFound("1410", "Checksum does not exist for data object id provided");
		}
		checksum.setValue(csum);
		checksum.setAlgorithm(properties.getProperty("irods.dataone.chksum-algorithm"));

		return checksum;
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
		if (id == null || id.getValue().isEmpty()) {
			throw new InvalidToken("1402", "invalid iRODS data object id");
		}

		SystemMetadata metadata = new SystemMetadata();
		// TODO: hardcode version to 1 for now
		metadata.setSerialVersion(getSerialVersion());

		DataObject dataObject;
		IRODSAccount irodsAccount;
		Checksum checksum = new Checksum();

		DataOneRepoServiceAO repoService;

		// first try and find data object for this id
		try {
			irodsAccount = RestAuthUtils.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
			repoService = pluginDiscoveryService.instanceRepoService(irodsAccount);
		} catch (Exception ex) {
			log.error("{}", ex.toString());
			throw new ServiceFailure("1390", ex.getMessage());
		}

		// first try and find data object for this id
		try {

			UniqueIdAO pidService = pluginDiscoveryService.instanceUniqueIdService(irodsAccount);
			dataObject = pidService.getDataObjectFromIdentifier(id);

		} catch (Exception ex) {
			log.info("file does not exist");
			throw new NotFound("1060", "No metadata could be found for given PID:" + id.getValue());
		}

		try {
			String csum = dataObject.getChecksum();
			if (csum == null) {
				log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath()); // throw
																								// new
																								// NotFound("404",
																								// "1420");
			} else {
				checksum.setValue(csum);
				checksum.setAlgorithm(properties.getProperty("irods.dataone.chksum-algorithm"));
			}

			String format = repoService.dataObjectFormat(dataObject);

			metadata.setIdentifier(id);
			ObjectFormatIdentifier formatId = new ObjectFormatIdentifier();
			formatId.setValue(format);
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

			DataObjectAO dataObjectAO = publicationContext.getIrodsAccessObjectFactory().getDataObjectAO(irodsAccount);
			List<UserFilePermission> permissions = dataObjectAO
					.listPermissionsForDataObject(dataObject.getAbsolutePath());
			if (permissions != null) {
				AccessPolicy accessPolicy = new AccessPolicy();
				for (UserFilePermission permission : permissions) {
					AccessRule rule = new AccessRule();
					Subject subject = new Subject();

					// in DataONE - anonymous translates to public
					// TODO: also may need to make translation for "public" to
					// "authenticatedUser"
					if (permission.getUserName().equals("anonymous")) {
						subject.setValue("public");
					} else {
						subject.setValue("uid=" + permission.getUserName());
					}
					rule.addSubject(subject);
					List<Permission> d1Premissions = getD1Permission(permission);
					for (Permission d1Premission : d1Premissions) {
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

			// Use AVU epoch date //
			metadata.setDateUploaded(dataObject.getCreatedAt()); //
			metadata.setDateSysMetadataModified(dataObject.getUpdatedAt());
			Date startDate = repoService.getLastModifiedDateForDataObject(dataObject);
			metadata.setDateSysMetadataModified(startDate);
			metadata.setDateUploaded(startDate);

			NodeReference nodeReference = new NodeReference();
			nodeReference.setValue(properties.getProperty("irods.dataone.identifier"));
			metadata.setOriginMemberNode(nodeReference);
			metadata.setAuthoritativeMemberNode(nodeReference);

		} catch (Exception e) {
			log.error("Cannot access iRODS object: {}", dataObject.getAbsolutePath());
			throw new ServiceFailure("1090", e.getMessage());
		}

		// first try and find data object for this id

		return metadata;
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

		IRODSAccount irodsAccount;
		DataObjectListResponse response = new DataObjectListResponse();
		List<ObjectInfo> objectInfoList = new ArrayList<>();
		ObjectList objectList = new ObjectList();
		DataOneRepoServiceAO repoService;

		// first try and find data object for this id
		try {
			irodsAccount = RestAuthUtils.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());

			repoService = pluginDiscoveryService.instanceRepoService(irodsAccount);
			response = repoService.getListOfDataoneExposedDataObjects(fromDate, toDate, formatId, replicaStatus, start,
					count);
		} catch (Exception ex) {
			log.error("{}", ex.toString());
			throw new ServiceFailure("1580", "Could not retrieve list of data objects");
		}

		List<DataObject> dataObjects = response.getDataObjects();

		for (DataObject dObject : dataObjects) {

			ObjectInfo oInfo = new ObjectInfo();

			if (dObject.getChecksum() != null) {
				Checksum checksum = new Checksum();
				checksum.setValue(dObject.getChecksum());
				checksum.setAlgorithm(properties.getProperty("irods.dataone.chksum-algorithm"));
				oInfo.setChecksum(checksum);
			}

			String format = null;
			try {
				format = repoService.dataObjectFormat(dObject);

			} catch (Exception e1) {
				log.error(e1.toString());
				log.error("cannot retrieve mime type for object:{} setting to application/octet-stream",
						dObject.getAbsolutePath());
				format = "application/octet-stream";
			}

			ObjectFormatIdentifier fId = new ObjectFormatIdentifier();
			fId.setValue(format);
			oInfo.setFormatId(fId);

			Date startDate = new Date();
			try {
				startDate = repoService.getLastModifiedDateForDataObject(dObject);
			} catch (Exception e1) {
				log.error(e1.toString());
				log.error("cannot retrieve start date for object: {}", dObject.getAbsolutePath());
			}
			oInfo.setDateSysMetadataModified(startDate);

			Identifier id;
			try {
				UniqueIdAO pidService = pluginDiscoveryService.instanceUniqueIdService(irodsAccount);
				id = pidService.getIdentifierFromDataObject(dObject);
			} catch (JargonException | PluginNotFoundException e) {
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
		objectList.setTotal(response.getTotal());
		objectList.setCount(objectInfoList.size());
		objectList.setStart(start);

		return objectList;

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

		Identifier pid = null;
		IRODSAccount irodsAccount;
		if (syncFailed.getPid() != null) {
			pid = new Identifier();
			pid.setValue(syncFailed.getPid());
		} else {
			throw new ServiceFailure("2161", "The identifier cannot be null.");
		}

		DataObject dataObject;
		try {
			irodsAccount = RestAuthUtils.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());

			UniqueIdAO pidService = pluginDiscoveryService.instanceUniqueIdService(irodsAccount);
			dataObject = pidService.getDataObjectFromIdentifier(pid);

		} catch (Exception ex) {
			throw new ServiceFailure("2161",
					"The identifier specified by " + syncFailed.getPid() + " was not found on this node.");
		}

		// TODO: mcc - see
		// https://github.com/DICE-UNC/dfc-dataone/blob/master/src/main/java/org/irods/jargon/dataone/events/EventLogAOElasticSearchImpl.java

		try {
			DataOneEventServiceAO eventServiceAO = pluginDiscoveryService.instanceEventService(irodsAccount);
			EventData eventData = new EventData();
			eventData.setDescription("DataONE replication");
			eventData.setEvent(Event.SYNCHRONIZATION_FAILED);
			eventData.setId(pid);
			eventData.setIrodsPath(dataObject.getAbsolutePath());
			eventData.setNodeIdentifier(
					publicationContext.getAdditionalProperties().getProperty(ConfigConstants.PROPERTY_NODE_IDENTIFIER));
			eventData.setUserAgent(irodsAccount.getUserName());

			eventServiceAO.recordEvent(eventData);

		} catch (PluginNotFoundException | JargonException e) {
			log.error("failed to log synchronization failed event: {}", e.toString());
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}

		return true;
	}

	@Override
	public boolean synchronizationFailed(final Session arg0, final SynchronizationFailed arg1)
			throws InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
		throw new NotImplemented("2160", "this service has not been implemented");
	}

	// need to return every DataONE permission implied by iRODS permissions i.e.
	// write would require read and write to be listed as DataONE permissions
	// and
	// own would include read, write, and changePermission
	private List<Permission> getD1Permission(final UserFilePermission p) {
		List<Permission> permissions = new ArrayList<>();
		FilePermissionEnum fpEnum = p.getFilePermissionEnum();
		switch (fpEnum) {
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

	private BigInteger getSerialVersion() {
		// TODO: hardcode version to 1 for now
		Long verLong = new Long(1);
		String verStr = verLong.toString();
		return new BigInteger(verStr);
	}

}
