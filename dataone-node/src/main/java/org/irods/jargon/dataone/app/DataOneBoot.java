/**
 * 
 */
package org.irods.jargon.dataone.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * DataONE embedded container boot and app assemblies
 * 
 * @author mconway
 *
 */

@SpringBootApplication
public class DataOneBoot extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DataOneBoot.class, args);
	}

}
