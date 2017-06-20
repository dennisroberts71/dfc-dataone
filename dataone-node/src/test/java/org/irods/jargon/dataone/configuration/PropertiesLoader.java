/**
 * 
 */
package org.irods.jargon.dataone.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irods.jargon.core.exception.JargonRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load config properties from /etc/irods-ext
 * 
 * @author mcc
 *
 */
public class PropertiesLoader {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 */
	public PropertiesLoader() {
	}

	public Properties loadD1Properties() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			log.info("loading properties from /etc/irods-ext/d1client.properties");
			String filename = "/etc/irods-ext/d1client.properties";
			input = new FileInputStream(filename);

			prop.load(input);
			return prop;
		} catch (IOException ex) {
			log.error("IOException loading props", ex);
			throw new JargonRuntimeException("unable to load properties", ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
