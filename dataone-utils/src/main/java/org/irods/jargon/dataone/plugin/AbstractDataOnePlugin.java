package org.irods.jargon.dataone.plugin;

import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * 
 * @author mconway
 *
 */
public class AbstractDataOnePlugin {

	/**
	 * Represents a handle to iRODS connection and general configuration
	 * information
	 */
	private final PublicationContext publicationContext;

	/**
	 * @param publicationContext
	 */
	public AbstractDataOnePlugin(PublicationContext publicationContext) {
		this.publicationContext = publicationContext;
	}

	/**
	 * @return the publicationContext
	 */
	protected PublicationContext getPublicationContext() {
		return publicationContext;
	}

}
