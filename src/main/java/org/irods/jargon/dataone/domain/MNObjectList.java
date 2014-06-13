package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ObjectList;

@XmlRootElement(name = "objectList")
public class MNObjectList {
	
	@XmlAttribute
	private int count;
	
	@XmlAttribute
	private int start;
	
	@XmlAttribute
	private int total;
	
	private List<MNObjectInfo> objectInfo;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<MNObjectInfo> getObjectInfo() {
		return objectInfo;
	}

	public void setObjectInfo(List<MNObjectInfo> objectInfo) {
		this.objectInfo = objectInfo;
	}
	
	public void copy(ObjectList objectList) {
		
		if (objectList == null) {
			throw new IllegalArgumentException("MNObjectList::copy - ObjectList is null");
		}
		
		this.count = objectList.getCount();
		this.start = objectList.getStart();
		this.total = objectList.getTotal();
		
		if (objectList.getObjectInfoList() == null) {
			List<ObjectInfo> newObjectInfoList = objectList.getObjectInfoList();
			this.objectInfo = new ArrayList<MNObjectInfo>();
			
			for (ObjectInfo oi : newObjectInfoList) {
				MNObjectInfo mnObjectInfo = new MNObjectInfo();
				mnObjectInfo.copy(oi);
				this.objectInfo.add(mnObjectInfo);
			}
		}
		
	}

}
