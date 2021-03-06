package org.irods.jargon.dataone.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.Subject;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.domain.Collection;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.zipservice.api.JargonZipServiceImpl;
import org.irods.jargon.zipservice.api.ZipServiceConfiguration;
import org.mockito.internal.matchers.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Sarah Roberts - CyVerse
 */
public class CollectionDataOneObject extends AbstractDataOneObject {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccount account;
	private final PublicationContext ctx;
	private final Identifier id;
	private final Date lastModified;
	private final Collection collection;

	public CollectionDataOneObject(final PublicationContext ctx, final IRODSAccount account, final Identifier id,
								   final Date lastModified, final Collection collection) {

 		if (account == null) {
			throw new NullPointerException("No iRODS account provided.");
		}

		if (ctx == null) {
			throw new NullPointerException("No publication context provided.");
		}

		if (id == null || id.getValue() == null || id.getValue().isEmpty()) {
			throw new NullPointerException("No identifier provided.");
		}

		if (lastModified == null) {
 			throw new NullPointerException("No last modified date provided.");
		}

		if (collection == null) {
			throw new NullPointerException("No collection provided.");
		}

		this.ctx = ctx;
		this.account = account;
		this.id = id;
		this.lastModified = lastModified;
		this.collection = collection;
	}

	@Override
	protected PublicationContext getPublicationContext() {
		return ctx;
	}

	@Override
	protected Identifier getId() {
		return id;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	public String getPath() throws JargonException, PluginNotFoundException {
		return collection.getAbsolutePath();
	}

	@Override
	public String getName() throws JargonException, PluginNotFoundException {
		return collection.getCollectionName();
	}

	@Override
	public ObjectFormatIdentifier getFormat() throws JargonException, PluginNotFoundException {
		AbstractDataOneRepoServiceAO repoService = ctx.getPluginDiscoveryService().instanceRepoService(account);

		// Determine the format.
		ObjectFormatIdentifier result = new ObjectFormatIdentifier();
		result.setValue(repoService.getFormat(collection.getAbsolutePath()));

		return result;
	}

	@Override
	public BigInteger getSize() throws JargonException, PluginNotFoundException {
		JargonZipServiceImpl zipService = getJargonZipService();
		List<String> paths = Collections.singletonList(collection.getAbsolutePath());
		return BigInteger.valueOf(zipService.computeBundleSizeInBytes(paths));
	}

	@Override
	public Checksum getChecksum() throws JargonException, PluginNotFoundException {
		Checksum checksum = new Checksum();

		// Calculate the checksum.
		try {
			checksum.setValue(DigestUtils.md5Hex(getInputStream()));
		} catch (IOException e) {
			throw new JargonException(e);
		}

		return checksum;
	}

	@Override
	public Subject getSubmitter() throws JargonException, PluginNotFoundException {
		return getOwnerSubject();
	}

	@Override
	public Subject getRightsHolder() throws JargonException, PluginNotFoundException {
		return getOwnerSubject();
	}

	@Override
	public List<UserFilePermission> getPermissions() throws JargonException {
		CollectionAO collectionAO = ctx.getIrodsAccessObjectFactory().getCollectionAO(account);
		return collectionAO.listPermissionsForCollection(collection.getAbsolutePath());
	}

	@Override
	public Date getLastModifiedDate() throws JargonException, PluginNotFoundException {
		return lastModified;
	}

	@Override
	public InputStream getInputStream() throws JargonException, PluginNotFoundException {
		JargonZipServiceImpl zipService = getJargonZipService();
		List<String> paths = Collections.singletonList(collection.getAbsolutePath());
		return zipService.obtainBundleAsInputStreamGivenPaths(paths);
	}

	private Subject getOwnerSubject() {
		Subject result = new Subject();
		result.setValue("uid" + collection.getCollectionOwnerName());
		return result;
	}

	private JargonZipServiceImpl getJargonZipService() {
		ZipServiceConfiguration config = new ZipServiceConfiguration();
		return new JargonZipServiceImpl(config, ctx.getIrodsAccessObjectFactory(), account);
	}
}
