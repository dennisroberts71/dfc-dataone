package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.*;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Dennis Roberts - CyVerse
 */
public interface DataOneObject {

	String getName() throws JargonException, PluginNotFoundException;

	ObjectFormatIdentifier getFormat() throws JargonException, PluginNotFoundException;

	ObjectFormatIdentifier getFormat(String defaultFormat) throws JargonException, PluginNotFoundException;

	BigInteger getSize() throws JargonException, PluginNotFoundException;

	Checksum getChecksum() throws JargonException, PluginNotFoundException;

	Subject getSubmitter() throws JargonException, PluginNotFoundException;

	Subject getRightsHolder() throws JargonException, PluginNotFoundException;

	AccessPolicy getAccessPolicy() throws JargonException, PluginNotFoundException;

	ReplicationPolicy getReplicationPolicy() throws JargonException, PluginNotFoundException;

	Date getLastModifiedDate() throws JargonException, PluginNotFoundException;

	InputStream getInputStream() throws JargonException, PluginNotFoundException;

	DescribeResponse describe() throws JargonException, PluginNotFoundException;

	SystemMetadata getSystemMetadata() throws JargonException, PluginNotFoundException;

	ObjectInfo getObjectInfo() throws JargonException, PluginNotFoundException;
}
