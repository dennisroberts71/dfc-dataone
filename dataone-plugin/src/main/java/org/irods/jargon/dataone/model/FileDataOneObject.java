package org.irods.jargon.dataone.model;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
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

/**
 * @author Dennis Roberts - CyVerse
 */
public class FileDataOneObject implements DataOneObject {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccount account;
	private final PublicationContext ctx;
	private final DataObject dataObject;

	public FileDataOneObject(final PublicationContext ctx, final IRODSAccount account, final DataObject dataObject) {

		if (account == null) {
			throw new NullPointerException("No iRODS account provided.");
		}

		if (ctx == null) {
			throw new NullPointerException("No publication context provided.");
		}

		if (dataObject == null) {
			throw new NullPointerException("No data object provided.");
		}

		this.account = account;
		this.ctx = ctx;
		this.dataObject = dataObject;
	}

	@Override
	public String getName() throws JargonException, PluginNotFoundException {
		return dataObject.getDataName();
	}

	@Override
	public long getSize() throws JargonException, PluginNotFoundException {
		return dataObject.getDataSize();
	}

	@Override
	public InputStream getInputStream() throws JargonException, PluginNotFoundException {

		// Get the input stream.
		IRODSFileFactory fileFactory = ctx.getIrodsAccessObjectFactory().getIRODSFileFactory(account);
		IRODSFile file = fileFactory.instanceIRODSFile(dataObject.getAbsolutePath());
		return fileFactory.instanceIRODSFileInputStream(file);
	}

	@Override
	public DescribeResponse describe() throws JargonException, PluginNotFoundException {

		// Look up the repo service.
		AbstractDataOneRepoServiceAO repoService = ctx.getPluginDiscoveryService().instanceRepoService(account);

		// Determine the format identifier.
		ObjectFormatIdentifier formatIdentifier = new ObjectFormatIdentifier();
		formatIdentifier.setValue(repoService.dataObjectFormat(dataObject));

		// Determine the content length.
		BigInteger contentLength = new BigInteger(new Long(getSize()).toString());

		// Determine the last modified time.
		Date lastModified = repoService.getLastModifiedDateForDataObject(dataObject);

		// Get the checksum.
		Checksum checksum = new Checksum();
		String csum = dataObject.getChecksum();
		if (csum == null) {
			log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
		} else {
			checksum.setValue(csum);
			checksum.setAlgorithm(CommonConfig.getChecksumAlgorithm(ctx));
		}

		// Get the serial version.
		BigInteger serialVersion = getSerialVersion();

		return new DescribeResponse(formatIdentifier, contentLength, lastModified, checksum, serialVersion);
	}

	private BigInteger getSerialVersion() {
		// TODO: hardcode version to 1 for now
		return new BigInteger(new Long(1).toString());
	}
}
