package org.irods.jargon.dataone.config;

import org.irods.jargon.dataone.plugin.PublicationContext;

/**
 * @author Dennis Roberts - CyVerse
 */
public class CommonConfig {
	private static final String CHECKSUM_ALGORITHM_PROPERTY = "irods.dataone.chksum-algorithm";

	private static final String DEFAULT_CHECKSUM_ALGORITHM = "MD5";

	// Prevent instantiation.
	private CommonConfig() {
	}

	public static String getChecksumAlgorithm(PublicationContext ctx) {
		return ctx.getAdditionalProperties().getProperty(CHECKSUM_ALGORITHM_PROPERTY, DEFAULT_CHECKSUM_ALGORITHM);
	}
}
