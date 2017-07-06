package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ObjectList;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of DataOne objects that is being returned from an object search. The list may be a partial
 * list if there are more matching objects than the requested object count.
 *
 * @author Dennis Roberts - CyVerse
 */
public class DataOneObjectListResponse {

	private final List<DataOneObject> dataOneObjects;
	private final int total;
	private final int start;

	/**
	 * Builds a new {@link DataOneObjectListResponse}.
	 *
	 * @param dataOneObjects the list of DataOne objects to include in the response.
	 * @param total the total number of matching DataOne objects.
	 * @param start the starting offset for the list.
	 */
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

	/**
	 * @return the list of DataOne objects.
	 */
	public List<DataOneObject> getDataOneObjects() {
		return dataOneObjects;
	}

	/**
	 * @return the total number of matching DataOne objects.
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @return the start index for the response.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the number of DataOne objects in this response.
	 */
	public int getCount() {
		return dataOneObjects.size();
	}

	/**
	 * Builds a list of {@link ObjectInfo} objects corresponding to the list of {@link DataOneObject} instances.
	 *
	 * @return the list of {@link ObjectInfo} objects.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	public List<ObjectInfo> getObjectInfoList() throws JargonException, PluginNotFoundException {
		List<ObjectInfo> objectInfoList = new ArrayList<>(getCount());
		for (DataOneObject dataOneObject : getDataOneObjects()) {
			objectInfoList.add(dataOneObject.getObjectInfo());
		}
		return objectInfoList;
	}

	/**
	 * Converts this object to an equivalent {@link ObjectList} object.
	 *
	 * @return the equivalent {@link ObjectList} object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	public ObjectList toObjectList() throws JargonException, PluginNotFoundException {
		ObjectList result = new ObjectList();
		result.setTotal(getTotal());
		result.setStart(getStart());
		result.setCount(getCount());
		result.setObjectInfoList(getObjectInfoList());
		return result;
	}
}
