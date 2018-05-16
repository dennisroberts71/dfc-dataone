package org.irods.jargon.dataone.config;

import org.dataone.service.types.v1.NodeReference;
import org.irods.jargon.dataone.plugin.ConfigConstants;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * @author Sarah Roberts - CyVerse
 */
public class CommonConfig {
	// Prevent instantiation.
	private CommonConfig() {
	}

	public static String getChecksumAlgorithm(PublicationContext ctx) {
		return ctx.getAdditionalProperties()
				.getProperty(ConfigConstants.PROPERTY_CHECKSUM_ALGORITHM, ConfigConstants.DEFAULT_CHECKSUM_ALGORITHM);
	}

	public static String getDataOneNodeId(PublicationContext ctx) {
		return ctx.getAdditionalProperties()
				.getProperty(ConfigConstants.PROPERTY_NODE_IDENTIFIER, ConfigConstants.DEFAULT_NODE_IDENTIFIER);
	}

	public static NodeReference getNodeReference(PublicationContext ctx) {
		NodeReference result = new NodeReference();
		result.setValue(getDataOneNodeId(ctx));
		return result;
	}
}
