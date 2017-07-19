package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.AccessPolicy;
import org.dataone.service.types.v1.AccessRule;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.ReplicationPolicy;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.dataone.config.CommonConfig;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.util.PermissionUtils;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Dennis Roberts - CyVerse
 */
public abstract class AbstractDataOneObject implements DataOneObject {

	protected static final String DEFAULT_FORMAT = "application/octet-stream";

	protected abstract Logger getLog();

	protected abstract PublicationContext getPublicationContext();

	protected abstract Identifier getId();

	protected abstract List<UserFilePermission> getPermissions() throws JargonException;

	protected BigInteger getSerialVersion() throws JargonException {
		// TODO: hardcode version to 1 for now
		return BigInteger.valueOf(1);
	}

	@Override
	public ObjectFormatIdentifier getFormat(String defaultFormat) throws JargonException, PluginNotFoundException {
		try {
			return getFormat();
		} catch (Exception e) {
			getLog().warn("failed to determine object format: {}", e.toString());
			ObjectFormatIdentifier result = new ObjectFormatIdentifier();
			result.setValue(defaultFormat);
			return result;
		}
	}

	@Override
	public AccessPolicy getAccessPolicy() throws JargonException, PluginNotFoundException {

		// Look up the permissions.
		List<UserFilePermission> perms = getPermissions();
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
	public SystemMetadata getSystemMetadata() throws JargonException, PluginNotFoundException {
		SystemMetadata result = new SystemMetadata();
		result.setSerialVersion(getSerialVersion());
		result.setIdentifier(getId());
		result.setFormatId(getFormat());
		result.setSize(getSize());
		result.setChecksum(getChecksum());
		result.setSubmitter(getSubmitter());
		result.setRightsHolder(getRightsHolder());
		result.setAccessPolicy(getAccessPolicy());
		result.setReplicationPolicy(getReplicationPolicy());
		result.setDateSysMetadataModified(getLastModifiedDate());
		result.setDateUploaded(getLastModifiedDate());
		result.setOriginMemberNode(CommonConfig.getNodeReference(getPublicationContext()));
		result.setAuthoritativeMemberNode(CommonConfig.getNodeReference(getPublicationContext()));
		return result;
	}

	@Override
	public ObjectInfo getObjectInfo() throws JargonException, PluginNotFoundException {
		ObjectInfo objectInfo = new ObjectInfo();
		objectInfo.setChecksum(getChecksum());
		objectInfo.setFormatId(getFormat(DEFAULT_FORMAT));
		objectInfo.setDateSysMetadataModified(getLastModifiedDate());
		objectInfo.setIdentifier(getId());
		objectInfo.setSize(getSize());
		return objectInfo;
	}

	@Override
	public DescribeResponse describe() throws JargonException, PluginNotFoundException {
		return new DescribeResponse(getFormat(), getSize(), getLastModifiedDate(), getChecksum(), getSerialVersion());
	}
}
