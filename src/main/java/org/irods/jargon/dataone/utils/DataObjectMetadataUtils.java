package org.irods.jargon.dataone.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryOperatorEnum;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain;

public class DataObjectMetadataUtils {
	
	static public Date getStartDateTime(
			IRODSAccessObjectFactory irodsAccessObjectFactory,
			IRODSAccount irodsAccount,
			DataObject dObject)
			throws JargonQueryException, JargonException {
		
		long epoch=0;
		AVUQueryElement avuQuery = null;
		List<AVUQueryElement> avuQueryList = new ArrayList<AVUQueryElement>();
		List<MetaDataAndDomainData> metadataAndDomainDataList = new ArrayList<MetaDataAndDomainData>();
		String dateAttr = "StartDateTime";
		
		avuQuery = AVUQueryElement.instanceForValueQuery(
				AVUQueryPart.ATTRIBUTE,
				AVUQueryOperatorEnum.EQUAL,
				dateAttr);
		avuQueryList.add(avuQuery);
		
		DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
	
		metadataAndDomainDataList =  dataObjectAO.findMetadataValuesForDataObjectUsingAVUQuery(avuQueryList, dObject.getAbsolutePath());
		String value = null;
		for (MetaDataAndDomainData data : metadataAndDomainDataList) {
			value = data.getAvuValue();
			// just take the 1st one
			break;
		}
		epoch = Long.parseLong(value);
		// need to convert it 2 milliseconds for Java date
		Date theDate = new Date(epoch*1000);
		
		return theDate;
	}

}
