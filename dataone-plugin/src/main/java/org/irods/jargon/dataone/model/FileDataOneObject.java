package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.AccessPolicy;
import org.dataone.service.types.v1.AccessRule;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.ReplicationPolicy;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.dataone.config.CommonConfig;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.util.PermissionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Dennis Roberts - CyVerse
 */
public class FileDataOneObject implements DataOneObject {

	private static final String DEFAULT_FORMAT = "application/octet-stream";

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccount account;
	private final PublicationContext ctx;
	private final Identifier id;
	private final DataObject dataObject;

	public FileDataOneObject(final PublicationContext ctx, final IRODSAccount account, final Identifier id,
							 final DataObject dataObject) {

		if (account == null) {
			throw new NullPointerException("No iRODS account provided.");
		}

		if (ctx == null) {
			throw new NullPointerException("No publication context provided.");
		}

		if (id == null || id.getValue() == null || id.getValue().isEmpty()) {
			throw new NullPointerException("No identifier provided.");
		}

		if (dataObject == null) {
			throw new NullPointerException("No data object provided.");
		}

		this.account = account;
		this.ctx = ctx;
		this.id = id;
		this.dataObject = dataObject;
	}

	@Override
	public String getPath() throws JargonException, PluginNotFoundException {
		return dataObject.getAbsolutePath();
	}

	@Override
	public String getName() throws JargonException, PluginNotFoundException {
		return dataObject.getDataName();
	}

	@Override
	public ObjectFormatIdentifier getFormat() throws JargonException, PluginNotFoundException {
		AbstractDataOneRepoServiceAO repoService = ctx.getPluginDiscoveryService().instanceRepoService(account);

		// Determine the format.
		ObjectFormatIdentifier result = new ObjectFormatIdentifier();
		result.setValue(repoService.dataObjectFormat(dataObject));

		return result;
	}

	@Override
	public ObjectFormatIdentifier getFormat(String defaultFormat) throws JargonException, PluginNotFoundException {
		try {
			return getFormat();
		} catch (Exception e) {
			log.warn("failed to determine object format: {}", e.toString());
			ObjectFormatIdentifier result = new ObjectFormatIdentifier();
			result.setValue(defaultFormat);
			return result;
		}
	}

	@Override
	public BigInteger getSize() throws JargonException, PluginNotFoundException {
		return BigInteger.valueOf(dataObject.getDataSize());
	}

	@Override
	public Checksum getChecksum() throws JargonException, PluginNotFoundException {
		Checksum checksum = new Checksum();

		// Determine the checksum.
		String csum = dataObject.getChecksum();
		if (csum == null) {
			log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
		} else {
			checksum.setValue(csum);
			checksum.setAlgorithm(CommonConfig.getChecksumAlgorithm(ctx));
		}

		return checksum;
	}

	@Override
	public Subject getSubmitter() throws JargonException, PluginNotFoundException {
		return getOwnerSubject();
	}

	@Override
	public Subject getRightsHolder() throws JargonException, PluginNotFoundException {
		return getOwnerSubject();
	}

	@Override
	public AccessPolicy getAccessPolicy() throws JargonException, PluginNotFoundException {

		// Look up the data object permissions.
		DataObjectAO dataObjectAO = ctx.getIrodsAccessObjectFactory().getDataObjectAO(account);
		List<UserFilePermission> perms = dataObjectAO.listPermissionsForDataObject(dataObject.getAbsolutePath());
		if (perms == null) {
			perms = new ArrayList<>();
		}

		// Build the access policy.
		AccessPolicy result = new AccessPolicy();
		for (UserFilePermission perm : perms) {
			AccessRule rule = new AccessRule();
			Subject subject = new Subject();

			if (perm.getUserName().equals("anonymous")) {
				subject.setValue("public");
			} else {
				subject.setValue("uid=" + perm.getUserName());
			}
			rule.addSubject(subject);
			List<Permission> d1Permissions = PermissionUtils.getDataOnePermission(perm);
			for (Permission d1Permission : d1Permissions) {
				rule.addPermission(d1Permission);
			}
			result.addAllow(rule);
		}

		return result;
	}

	@Override
	public ReplicationPolicy getReplicationPolicy() throws JargonException, PluginNotFoundException {
		ReplicationPolicy result = new ReplicationPolicy();
		result.setReplicationAllowed(false);
		return result;
	}

	@Override
	public Date getLastModifiedDate() throws JargonException, PluginNotFoundException {
		AbstractDataOneRepoServiceAO repoService = ctx.getPluginDiscoveryService().instanceRepoService(account);
		return repoService.getLastModifiedDate(dataObject);
	}

	@Override
	public InputStream getInputStream() throws JargonException, PluginNotFoundException {

		// Get the input stream.
		IRODSFileFactory fileFactory = ctx.getIrodsAccessObjectFactory().getIRODSFileFactory(account);
		IRODSFile file = fileFactory.instanceIRODSFile(dataObject.getAbsolutePath());
		return fileFactory.instanceIRODSFileInputStream(file);
	}

	@Override
	public DescribeResponse describe() throws JargonException, PluginNotFoundException {
		return new DescribeResponse(getFormat(), getSize(), getLastModifiedDate(), getChecksum(), getSerialVersion());
	}

	@Override
	public SystemMetadata getSystemMetadata() throws JargonException, PluginNotFoundException {
		SystemMetadata result = new SystemMetadata();
		result.setSerialVersion(getSerialVersion());
		result.setIdentifier(id);
		result.setFormatId(getFormat());
		result.setSize(getSize());
		result.setChecksum(getChecksum());
		result.setSubmitter(getSubmitter());
		result.setRightsHolder(getRightsHolder());
		result.setAccessPolicy(getAccessPolicy());
		result.setReplicationPolicy(getReplicationPolicy());
		result.setDateUploaded(dataObject.getCreatedAt());
		result.setDateSysMetadataModified(getLastModifiedDate());
		result.setDateUploaded(getLastModifiedDate());
		result.setOriginMemberNode(CommonConfig.getNodeReference(ctx));
		result.setAuthoritativeMemberNode(CommonConfig.getNodeReference(ctx));
		return result;
	}

	@Override
	public ObjectInfo getObjectInfo() throws JargonException, PluginNotFoundException {
		ObjectInfo objectInfo = new ObjectInfo();
		objectInfo.setChecksum(getChecksum());
		objectInfo.setFormatId(getFormat(DEFAULT_FORMAT));
		objectInfo.setDateSysMetadataModified(getLastModifiedDate());
		objectInfo.setIdentifier(id);
		objectInfo.setSize(getSize());
		return objectInfo;
	}

	private BigInteger getSerialVersion() {
		// TODO: hardcode version to 1 for now
		return new BigInteger(new Long(1).toString());
	}

	private Subject getOwnerSubject() {
		Subject result = new Subject();
		result.setValue("uid=" + dataObject.getDataOwnerName());
		return result;
	}
}
