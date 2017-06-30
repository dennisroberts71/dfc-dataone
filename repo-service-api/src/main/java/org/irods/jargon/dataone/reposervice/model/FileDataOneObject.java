package org.irods.jargon.dataone.reposervice.model;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.reposervice.DataOneRepoServiceAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Dennis Roberts, CyVerse
 */
public class FileDataOneObject implements DataOneObject {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DataOneRepoServiceAO repoService;
    private final DataObject dataObject;

    public FileDataOneObject(final DataOneRepoServiceAO repoService, final DataObject dataObject) {

        if (repoService == null) {
            throw new NullPointerException("No repo service provided.");
        }

        if (dataObject == null) {
            throw new NullPointerException("No data object provided.");
        }

        this.repoService = repoService;
        this.dataObject = dataObject;
    }

    @Override
    public String getName() throws JargonException {
        return dataObject.getDataName();
    }

    @Override
    public long getSize() throws JargonException {
        return dataObject.getDataSize();
    }

    @Override
    public DescribeResponse describe() throws JargonException {

        // Determine the format identifier.
        ObjectFormatIdentifier formatIdentifier = new ObjectFormatIdentifier();
        formatIdentifier.setValue(repoService.dataObjectFormat(dataObject));

        // Determine the content length.
        Long contentLengthLong = dataObject.getDataSize();
        String contentLengthStr = contentLengthLong.toString();
        BigInteger contentLength = new BigInteger(contentLengthStr);

        // Determine the last modified time.
        Date lastModified = repoService.getLastModifiedDateForDataObject(dataObject);

        // Get the checksum.
        Checksum checksum = new Checksum();
        String csum = dataObject.getChecksum();
        if (csum == null) {
            log.info("checksum does not exist for file: {}", dataObject.getAbsolutePath());
        } else {
            checksum.setValue(csum);
            checksum.setAlgorithm(repoService.getChecksumAlgorithm());
        }

        // Get the serial version.
        BigInteger serialVersion = getSerialVersion();

        return new DescribeResponse(formatIdentifier, contentLength, lastModified, checksum, serialVersion);
    }

    @Override
    public InputStream getInputStream() throws JargonException {
        // TODO: implement me.
        return null;
    }

    private BigInteger getSerialVersion() {
        // TODO: hardcode version to 1 for now
        Long verLong = new Long(1);
        String verStr = verLong.toString();
        return new BigInteger(verStr);
    }
}
