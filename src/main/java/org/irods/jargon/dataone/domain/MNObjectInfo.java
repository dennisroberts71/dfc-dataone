package org.irods.jargon.dataone.domain;

import java.util.Date;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.ObjectInfo;

public class MNObjectInfo {
	
	private String identifier;
	private String formatId;
	private MNChecksum checksum;
	private Date dateSysMetadataModified;
	private long size;
	
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
	public MNChecksum getChecksum() {
		return checksum;
	}
	public void setChecksum(MNChecksum checksum) {
		this.checksum = checksum;
	}
	public Date getDateSysMetadataModified() {
		return dateSysMetadataModified;
	}
	public void setDateSysMetadataModified(Date dateSysMetadataModified) {
		this.dateSysMetadataModified = dateSysMetadataModified;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public void copy(ObjectInfo objectInfo){
		
		if (objectInfo == null) {
			throw new IllegalArgumentException("MNObjectInfo::copy - ObjectInfo is null");
		}
		
		if (objectInfo.getIdentifier() != null) {
			this.identifier = objectInfo.getIdentifier().getValue();
		}
		
		if (objectInfo.getFormatId() != null) {
			this.formatId = objectInfo.getFormatId().getValue();
		}
		
		if (objectInfo.getChecksum() != null) {
			Checksum newChecksum = objectInfo.getChecksum();
			this.checksum = new MNChecksum();
			this.checksum.copy(newChecksum);
		}
		
		if (objectInfo.getDateSysMetadataModified() != null) {
			this.dateSysMetadataModified = objectInfo.getDateSysMetadataModified();
		}
		
		if (objectInfo.getSize() != null) {
			this.size = objectInfo.getSize().longValue();
		}
		
	}

}
