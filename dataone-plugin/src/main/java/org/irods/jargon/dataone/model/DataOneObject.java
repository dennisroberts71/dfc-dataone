package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.DescribeResponse;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;

import java.io.InputStream;

/**
 * @author Dennis Roberts - CyVerse
 */
public interface DataOneObject {

	String getName() throws JargonException, PluginNotFoundException;

	long getSize() throws JargonException, PluginNotFoundException;

	InputStream getInputStream() throws JargonException, PluginNotFoundException;

	DescribeResponse describe() throws JargonException, PluginNotFoundException;
}
