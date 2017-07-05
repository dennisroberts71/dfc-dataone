package org.irods.jargon.dataone.config;

import org.dataone.service.types.v1.NodeReference;
import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * @author Dennis Roberts - CyVerse
 */
public class CommonConfig {
	private static final String CHECKSUM_ALGORITHM_PROPERTY = "irods.dataone.chksum-algorithm";
	private static final String DATAONE_NODE_ID_PROPERTY = "irods.dataone.identifier";

	private static final String DEFAULT_CHECKSUM_ALGORITHM = "MD5";
	private static final String DEFAULT_DATAONE_NODE_ID = "fake-node-id";

	// Prevent instantiation.
	private CommonConfig() {
	}

	public static String getChecksumAlgorithm(PublicationContext ctx) {
		return ctx.getAdditionalProperties().getProperty(CHECKSUM_ALGORITHM_PROPERTY, DEFAULT_CHECKSUM_ALGORITHM);
	}

	public static String getDataOneNodeId(PublicationContext ctx) {
		return ctx.getAdditionalProperties().getProperty(DATAONE_NODE_ID_PROPERTY, DEFAULT_DATAONE_NODE_ID);
	}

	public static NodeReference getNodeReference(PublicationContext ctx) {
		NodeReference result = new NodeReference();
		result.setValue(getDataOneNodeId(ctx));
		return result;
	}
}
