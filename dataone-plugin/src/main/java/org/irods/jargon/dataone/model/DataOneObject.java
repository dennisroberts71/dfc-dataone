package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.AccessPolicy;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ReplicationPolicy;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;

/**
 * Represents a DataOne object that is stored in iRODS. The initial implementation will be for data objects, but
 * implementations for other iRODS entities such as collections may be added in the future.
 *
 * @author Dennis Roberts - CyVerse
 */
public interface DataOneObject {

	/**
	 * Retrieves the path to the object.
	 *
	 * @return the path to the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	String getPath() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the name of the object.
	 *
	 * @return the name of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	String getName() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the format of the object.
	 *
	 * @return the format identifier of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	ObjectFormatIdentifier getFormat() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the format of the object, using the provided default if no format can be identified.
	 *
	 * @param defaultFormat the default format to use if no other format can be identified.
	 * @return the format identifier of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	ObjectFormatIdentifier getFormat(String defaultFormat) throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the size of an object.
	 *
	 * @return the size of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	BigInteger getSize() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the checksum of an object.
	 *
	 * @return the checksum of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	Checksum getChecksum() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves information about the submitter of the object.
	 *
	 * @return the submitter of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	Subject getSubmitter() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves information about the person who holds the rights to the object.
	 *
	 * @return the rights holder of the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	Subject getRightsHolder() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves access policy information for the object.
	 *
	 * @return the access policy information for the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	AccessPolicy getAccessPolicy() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves replication policy information for the object.
	 *
	 * @return the replication policy information for the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	ReplicationPolicy getReplicationPolicy() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the most recent modification date for the object.
	 *
	 * @return the most recent modification date for the DataOne object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	Date getLastModifiedDate() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves an input stream that can be used to stream the object contents.
	 *
	 * @return the input stream.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	InputStream getInputStream() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves a description of the object.
	 *
	 * @return the DataOne object description.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	DescribeResponse describe() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves the system metadata for the object.
	 *
	 * @return the DataOne system metadata for the object.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	SystemMetadata getSystemMetadata() throws JargonException, PluginNotFoundException;

	/**
	 * Retrieves basic information about the object.
	 *
	 * @return the DataOne object information.
	 * @throws JargonException
	 * @throws PluginNotFoundException
	 */
	ObjectInfo getObjectInfo() throws JargonException, PluginNotFoundException;
}
