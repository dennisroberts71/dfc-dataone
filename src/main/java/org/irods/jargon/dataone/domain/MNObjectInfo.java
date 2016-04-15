package org.irods.jargon.dataone.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.ObjectInfo;
import org.irods.jargon.dataone.utils.ISO8601;

@XmlType(propOrder={"identifier","formatId","checksum","dateSysMetadataModified","size"})
public class MNObjectInfo {
	
	private String identifier;
	private String formatId;
	private MNChecksum checksum;
	private String dateSysMetadataModified;
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
	public String getDateSysMetadataModified() {
		return dateSysMetadataModified;
	}
	public void setDateSysMetadataModified(String dateSysMetadataModified) {
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
			this.dateSysMetadataModified = ISO8601.convertToGMTString(objectInfo.getDateSysMetadataModified());
		}
		
		if (objectInfo.getSize() != null) {
			this.size = objectInfo.getSize().longValue();
		}
		
	}

}
