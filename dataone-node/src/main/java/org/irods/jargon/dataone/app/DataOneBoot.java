/**
 * 
 */
package org.irods.jargon.dataone.app;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * DataONE embedded container boot and app assemblies
 * 
 * @author mconway
 *
 */

public class DataOneBoot extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DataOneBoot.class, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.boot.web.support.SpringBootServletInitializer#onStartup
	 * (javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		super.onStartup(servletContext);
	}

}
