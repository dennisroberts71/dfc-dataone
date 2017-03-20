package org.irods.jargon.dataone.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.QueryConditionOperators;

public class DataTypeUtils {

	public static String getDataObjectMimeType(IRODSAccount irodsAccount,
			IRODSAccessObjectFactory irodsAccessObjectFactory, DataObject dataObject)
			throws FileNotFoundException, JargonException {
		String mimeType = null;
		String filename = dataObject.getAbsolutePath();

		DefaultDetector typeDetector = new DefaultDetector();
		IRODSFile irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(filename);
		IRODSFileInputStream irodsStream = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
				.instanceIRODSFileInputStream(irodsFile);
		InputStream stream = new BufferedInputStream(irodsStream);
		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, filename);

		MediaType type;
		try {
			type = typeDetector.detect(stream, metadata);
		} catch (IOException e) {
			throw new FileNotFoundException("Cannot stream file in order to detect file type");
		}

		// if mime type is returned as "application/x-netcdf" change to
		// DataONE accepted name: "netCDF-4"
		mimeType = type.toString();
		if (mimeType.equals("application/x-netcdf")) {
			mimeType = "netCDF-4";
		}

		return mimeType;

	}

	public static String getDataObjectFormatFromMetadata(IRODSAccount irodsAccount,
			IRODSAccessObjectFactory irodsAccessObjectFactory, DataObject dataObject)
			throws FileNotFoundException, JargonException, JargonQueryException {

		String dataFormat = null;
		String formatAttr = "Format";
		List<AVUQueryElement> avuQueryList = new ArrayList<AVUQueryElement>();

		AVUQueryElement avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE,
				QueryConditionOperators.EQUAL, formatAttr);
		avuQueryList.add(avuQuery);

		DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
		// List<MetaDataAndDomainData> result =
		// dataObjectAO.findMetadataValuesForDataObject(dataObject.getAbsolutePath());
		List<MetaDataAndDomainData> result = dataObjectAO.findMetadataValuesForDataObjectUsingAVUQuery(avuQueryList,
				dataObject.getAbsolutePath());

		for (MetaDataAndDomainData metadata : result) {
			if (metadata.getAvuAttribute().equals("Format")) {
				dataFormat = metadata.getAvuValue();
				break;
			}
		}

		return dataFormat;

	}

}
