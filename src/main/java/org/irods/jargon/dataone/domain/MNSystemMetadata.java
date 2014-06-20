package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.AccessRule;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.SystemMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "systemMetadata")
@XmlType(propOrder={"serialVersion","identifier","formatId","size",
				"checksum","submitter","rightsHolder","accessPolicy",
				"replicationPolicy","obsoletes","obsoletedBy","archived",
				"dateUploaded","dateSysMetadataModified","originMemberNode",
				"authoritativeMemberNode"})

public class MNSystemMetadata {

	private long serialVersion;
	private String identifier;
	private String formatId;
	private long size;
	private MNChecksum checksum;
//	private MNSubject submitter;    NOTE: a Subject is just a string
//	private MNSubject rightsHolder;
	private String submitter;
	private String rightsHolder;
	private List<MNAccessPolicy> accessPolicy;
	private MNReplicationPolicy replicationPolicy;
	private String obsoletes;
	private String obsoletedBy;
	private boolean archived;
	private Date dateUploaded;
	private Date dateSysMetadataModified;
	private String originMemberNode;
	private String authoritativeMemberNode;
	
	public MNSystemMetadata() {
		
	}
	
	
	@XmlElementWrapper(name = "accessPolicy")
	@XmlElement(name = "allow")
	public List<MNAccessPolicy> getAccessPolicy() {
		return accessPolicy;
	}
	
	public void setAccessPolicy(List<MNAccessPolicy> ap) {
		this.accessPolicy = ap;
	}


	public long getSerialVersion() {
		return serialVersion;
	}


	public void setSerialVersion(long serialVersion) {
		this.serialVersion = serialVersion;
	}


	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getFormatId() {
		return formatId;
	}


	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}


	public long getSize() {
		return size;
	}


	public void setSize(long size) {
		this.size = size;
	}


	public MNChecksum getChecksum() {
		return checksum;
	}


	public void setChecksum(MNChecksum checksum) {
		this.checksum = checksum;
	}


	public String getSubmitter() {
		return submitter;
	}


	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}


	public String getRightsHolder() {
		return rightsHolder;
	}


	public void setRightsHolder(String rightsHolder) {
		this.rightsHolder = rightsHolder;
	}


	public MNReplicationPolicy getReplicationPolicy() {
		return replicationPolicy;
	}


	public void setReplicationPolicy(MNReplicationPolicy replicationPolicy) {
		this.replicationPolicy = replicationPolicy;
	}


	public String getObsoletes() {
		return obsoletes;
	}


	public void setObsoletes(String obsoletes) {
		this.obsoletes = obsoletes;
	}


	public String getObsoletedBy() {
		return obsoletedBy;
	}


	public void setObsoletedBy(String obsoletedBy) {
		this.obsoletedBy = obsoletedBy;
	}


	public boolean isArchived() {
		return archived;
	}


	public void setArchived(boolean archived) {
		this.archived = archived;
	}


	public Date getDateUploaded() {
		return dateUploaded;
	}


	public void setDateUploaded(Date dateUploaded) {
		this.dateUploaded = dateUploaded;
	}


	public Date getDateSysMetadataModified() {
		return dateSysMetadataModified;
	}


	public void setDateSysMetadataModified(Date dateSysMetadataModified) {
		this.dateSysMetadataModified = dateSysMetadataModified;
	}


	public String getOriginMemberNode() {
		return originMemberNode;
	}


	public void setOriginMemberNode(String originMemberNode) {
		this.originMemberNode = originMemberNode;
	}


	public String getAuthoritativeMemberNode() {
		return authoritativeMemberNode;
	}


	public void setAuthoritativeMemberNode(String authoritativeMemberNode) {
		this.authoritativeMemberNode = authoritativeMemberNode;
	}

	
	public void copy(SystemMetadata metadata) {
		if (metadata == null) {
			throw new IllegalArgumentException("MNSystemMetadata::copy - SystemMetadata is null");
		}
		
		if (metadata.getSerialVersion() != null) {
			this.serialVersion = metadata.getSerialVersion().longValue();
		}
		
		if (metadata.getIdentifier() != null) {
			this.identifier = metadata.getIdentifier().getValue();
		}
		
		if (metadata.getFormatId() != null) {
			this.formatId = metadata.getFormatId().getValue();
		}
		
		if (metadata.getSize() != null) {
			this.size = metadata.getSize().longValue();
		}
		
		if (metadata.getChecksum() != null) {
			MNChecksum checksum = new MNChecksum();
			checksum.setValue(metadata.getChecksum().getValue());
			checksum.setAlgorithm(metadata.getChecksum().getAlgorithm());
			this.checksum = checksum;
		}
		
		if (metadata.getSubmitter() != null) {
			this.submitter = metadata.getSubmitter().getValue();
		}
		
		if (metadata.getRightsHolder() != null) {
			this.rightsHolder = metadata.getRightsHolder().getValue();
		}
		
		if ((metadata.getAccessPolicy() != null) && (metadata.getAccessPolicy().sizeAllowList()) > 0) {
			List<MNAccessPolicy> accessPolicies = new ArrayList<MNAccessPolicy>();
			for (AccessRule rule : metadata.getAccessPolicy().getAllowList()) {
				MNAccessPolicy policy = new MNAccessPolicy();
				policy.setSubject(rule.getSubject(0).getValue());
				List<String> permissions = new ArrayList<String>();
				for (Permission p : rule.getPermissionList()) {
					permissions.add(p.toString());
				}
				policy.setPermission(permissions);
				accessPolicies.add(policy);
			}
			this.accessPolicy = accessPolicies;
		}
		
		if (metadata.getReplicationPolicy() != null) {
			this.replicationPolicy = new MNReplicationPolicy();
			this.replicationPolicy.setReplicationAllowed(metadata.getReplicationPolicy().getReplicationAllowed());
		}
		
		if (metadata.getObsoletes() != null) {
			this.obsoletes = metadata.getObsoletes().getValue();
		}
		
		if (metadata.getObsoletedBy() != null) {
			this.obsoletedBy = metadata.getObsoletedBy().getValue();
		}

		if (metadata.getArchived() != null) {
			this.archived = metadata.getArchived();
		}
		
		this.dateUploaded = metadata.getDateUploaded();
		this.dateSysMetadataModified = metadata.getDateSysMetadataModified();
		
		if (metadata.getOriginMemberNode() != null) {
			this.originMemberNode = metadata.getOriginMemberNode().getValue();
		}
		
		if (metadata.getAuthoritativeMemberNode() != null) {
			this.authoritativeMemberNode = metadata.getAuthoritativeMemberNode().getValue();
		}	
		
		return;
	}
	
}
