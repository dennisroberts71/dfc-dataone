/**
 * 
 */
package org.irods.jargon.dataone.plugin;

/**
 * Handy constants for config, properties, etc
 * 
 * @author mcc
 *
 */
public class ConfigConstants {

	public static final String PROPERTY_CHECKSUM_ALGORITHM = "irods.dataone.chksum-algorithm";
	public static final String PROPERTY_NODE_IDENTIFIER = "irods.dataone.identifier";

	public static final String DEFAULT_CHECKSUM_ALGORITHM = "MD5";
	public static final String DEFAULT_NODE_IDENTIFIER = "fake-node-id";

	// Prevent instantiation.
	private ConfigConstants() {
	}

}
