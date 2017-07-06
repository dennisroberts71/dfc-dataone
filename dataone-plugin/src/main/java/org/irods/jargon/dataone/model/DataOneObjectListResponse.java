package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ObjectList;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Roberts - CyVerse
 */
public class DataOneObjectListResponse {

	private final List<DataOneObject> dataOneObjects;
	private final int total;
	private final int start;

	public DataOneObjectListResponse(final List<DataOneObject> dataOneObjects, final int total, final int start) {

		// Validate the list of dataOne objects.
		if (dataOneObjects == null) {
			throw new NullPointerException("No list of DataOne objects provided.");
		}

		// Validate the total.
		if (total < dataOneObjects.size()) {
			throw new IllegalArgumentException("The total must be longer than the number of DataOne objects.");
		}

		// Validate the start index.
		if (start < 0) {
			throw new IllegalArgumentException("The start index must not be negative.");
		}
		if (start >= total) {
			throw new IllegalArgumentException("The start index must be less than the total.");
		}

		this.dataOneObjects = dataOneObjects;
		this.total = total;
		this.start = start;
	}

	public List<DataOneObject> getDataOneObjects() {
		return dataOneObjects;
	}

	public int getTotal() {
		return total;
	}

	public int getStart() {
		return start;
	}

	public int getCount() {
		return dataOneObjects.size();
	}

	public List<ObjectInfo> getObjectInfoList() throws JargonException, PluginNotFoundException {
		List<ObjectInfo> objectInfoList = new ArrayList<>(getCount());
		for (DataOneObject dataOneObject : getDataOneObjects()) {
			objectInfoList.add(dataOneObject.getObjectInfo());
		}
		return objectInfoList;
	}

	public ObjectList toObjectList() throws JargonException, PluginNotFoundException {
		ObjectList result = new ObjectList();
		result.setTotal(getTotal());
		result.setStart(getStart());
		result.setCount(getCount());
		result.setObjectInfoList(getObjectInfoList());
		return result;
	}
}
