/**
 * 
 */
package org.irods.jargon.dataone.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;
import org.xeustechnologies.jcl.context.JclContext;

/**
 * Service to locate and surface provided publisher plug-ins. Handles all the
 * classloader and reflection tasks.
 * 
 * @author Mike Conway - DICE
 *
 */
@Component
public class PluginDiscoveryService {

	private JarClassLoader jcl;
	private List<URL> urls = new ArrayList<URL>();
	@Autowired
	private PublicationContext publicationContext;

	public static final Logger log = LoggerFactory
			.getLogger(PluginDiscoveryService.class);

	public PluginDiscoveryService() {
	}

	private final Class loadImplClass(Class clazz)
			throws PluginNotFoundException {

		/*
		 * see: https://github.com/ronmamo/reflections
		 */

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder
				.setUrls(urls)
				.addClassLoader(JclContext.get())
				.addScanners(new SubTypesScanner(),
						new TypeAnnotationsScanner());
		Reflections reflections = new Reflections(configurationBuilder);

		Set<Class<?>> mechanisms = reflections.getTypesAnnotatedWith(clazz,
				true);
		log.info("mechanisms:{}", mechanisms);

		if (mechanisms.isEmpty()) {
			throw new PluginNotFoundException("no plugin found for class:"
					+ clazz.getName());
		}

		return mechanisms.iterator().next();

	}

	/**
	 * Initialize publisher based on (required) configuration
	 * 
	 * @throws PublicationException
	 */
	public void init() {
		log.info("init()");
		if (publicationContext == null) {
			throw new PluginRuntimeException(
					"init() cannot be called, no provided publicationContext");
		}

		if (publicationContext.getRestConfiguration().getPluginJarLocation() == null
				|| publicationContext.getRestConfiguration()
						.getPluginJarLocation().isEmpty()) {
			throw new PluginRuntimeException(
					"no jar file plugin directory specified");
		}

		log.info("scanning for plugin jars at:{}", publicationContext
				.getRestConfiguration().getPluginJarLocation());
		loadCandidateClasspaths(publicationContext.getRestConfiguration()
				.getPluginJarLocation());

	}

	private void loadCandidateClasspaths(String libDir) {

		/*
		 * See https://github.com/kamranzafar/JCL for usage of JCL
		 */

		File dependencyDirectory = new File(libDir);
		File[] files = dependencyDirectory.listFiles();
		ArrayList<URI> uris = new ArrayList<URI>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".jar")) {
				log.info("adding jar:{} to candidates", files[i]);
				uris.add(files[i].toURI());
			}
		}

		log.info("creating jar class loader...");
		jcl = new JarClassLoader();

		for (URI uri : uris) {
			log.info("adding uri for jar:{}", uri);
			try {
				jcl.add(uri.toURL());
				urls.add(uri.toURL()); // testing outside jcl FIXME: decide!
			} catch (MalformedURLException e) {
				log.error("malformed url for jar file:{}", uri, e);
				throw new PluginRuntimeException("error loading jar file", e);
			}
		}

		if (JclContext.get() == null) {
			DefaultContextLoader context = new DefaultContextLoader(jcl);
			context.loadContext();
		}

	}

	/**
	 * @return the publicationContext
	 */
	public PublicationContext getPublicationContext() {
		return publicationContext;
	}

	/**
	 * @param publicationContext
	 *            the publicationContext to set
	 */
	public void setPublicationContext(PublicationContext publicationContext) {
		this.publicationContext = publicationContext;
	}

}
