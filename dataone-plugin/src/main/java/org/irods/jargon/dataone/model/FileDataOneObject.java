package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.Subject;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.dataone.config.CommonConfig;
import org.irods.jargon.dataone.plugin.PluginNotFoundException;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * A DataOne object that is stored as a {@link DataObject} in iRODS.
 *
 * @author Dennis Roberts - CyVerse
 */
public class FileDataOneObject extends AbstractDataOneObject {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccount account;
	private final PublicationContext ctx;
	private final Identifier id;
	private final DataObject dataObject;
	private final Date lastModified;

	public FileDataOneObject(final PublicationContext ctx, final IRODSAccount account, final Identifier id,
							 final Date lastModified, final DataObject dataObject) {

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

		if (dataObject == null) {
			throw new NullPointerException("No data object provided.");
		}

		this.account = account;
		this.ctx = ctx;
		this.id = id;
		this.lastModified = lastModified;
		this.dataObject = dataObject;
	}

	@Override
	protected Logger getLog() {
		return log;
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
	public String getPath() throws JargonException, PluginNotFoundException {
		return dataObject.getAbsolutePath();
	}

	@Override
	public String getName() throws JargonException, PluginNotFoundException {
		return dataObject.getDataName();
	}

	@Override
	public ObjectFormatIdentifier getFormat() throws JargonException, PluginNotFoundException {
		AbstractDataOneRepoServiceAO repoService = ctx.getPluginDiscoveryService().instanceRepoService(account);

		// Determine the format.
		ObjectFormatIdentifier result = new ObjectFormatIdentifier();
		result.setValue(repoService.getFormat(dataObject.getAbsolutePath()));

		return result;
	}

	@Override
	public BigInteger getSize() throws JargonException, PluginNotFoundException {
		return BigInteger.valueOf(dataObject.getDataSize());
	}

	@Override
	public Checksum getChecksum() throws JargonException, PluginNotFoundException {
		Checksum checksum = new Checksum();

		// Determine the checksum.
		String csum = dataObject.getChecksum();
		if (csum == null) {
			log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
		} else {
			checksum.setValue(csum);
			checksum.setAlgorithm(CommonConfig.getChecksumAlgorithm(ctx));
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
	protected List<UserFilePermission> getPermissions() throws JargonException {
		DataObjectAO dataObjectAO = ctx.getIrodsAccessObjectFactory().getDataObjectAO(account);
		return dataObjectAO.listPermissionsForDataObject(dataObject.getAbsolutePath());
	}

	@Override
	public Date getLastModifiedDate() throws JargonException, PluginNotFoundException {
		return lastModified;
	}

	@Override
	public InputStream getInputStream() throws JargonException, PluginNotFoundException {

		// Get the input stream.
		IRODSFileFactory fileFactory = ctx.getIrodsAccessObjectFactory().getIRODSFileFactory(account);
		IRODSFile file = fileFactory.instanceIRODSFile(dataObject.getAbsolutePath());
		return fileFactory.instanceIRODSFileInputStream(file);
	}

	private Subject getOwnerSubject() {
		Subject result = new Subject();
		result.setValue("uid=" + dataObject.getDataOwnerName());
		return result;
	}
}
