package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

import org.dataone.service.exceptions.SynchronizationFailed;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class MNError {
		
		@XmlAttribute
		private String detailCode;
		@XmlAttribute
		private String errorCode;
		@XmlAttribute
		private String name;
		@XmlAttribute
		private String pid;
		@XmlElement
		private String description;
		
		public MNError() {
		}
		
		public String getDetailCode() {
			return detailCode;
		}
		
		public void setDetailCode(String detailCode) {
			this.detailCode = detailCode;
		}
		
		public String getErrorCode() {
			return errorCode;
		}
		
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getPid() {
			return pid;
		}
		
		public void setPid(String pid) {
			this.pid = pid;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public SynchronizationFailed copyToSynchronizationFailed() {
			SynchronizationFailed syncFailed = new SynchronizationFailed(
														this.detailCode,
														this.description);
			
			syncFailed.setPid(this.pid);
			
			return syncFailed;
			
		}
}
