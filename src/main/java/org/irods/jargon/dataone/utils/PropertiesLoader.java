package org.irods.jargon.dataone.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {
	
	private Properties properties;
	
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public PropertiesLoader() {
			
		this.properties = new Properties();
		InputStream input = null;
		input = getClass().getClassLoader().getResourceAsStream(this.propertiesFilename);

		// load a properties file
		try {
			this.properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", this.propertiesFilename);
			log.error("IOException: {}", e.getStackTrace());
			this.properties = new Properties();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
	
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
