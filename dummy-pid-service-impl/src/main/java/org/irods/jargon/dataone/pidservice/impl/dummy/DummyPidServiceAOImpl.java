package org.irods.jargon.dataone.pidservice.impl.dummy;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.model.DataOneObject;
import org.irods.jargon.dataone.model.FileDataOneObject;
import org.irods.jargon.dataone.pidservice.AbstractDataOnePidServiceAO;
import org.irods.jargon.dataone.plugin.PublicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DummyPidServiceAOImpl extends AbstractDataOnePidServiceAO {

	public static final Logger log = LoggerFactory.getLogger(DummyPidServiceAOImpl.class);

	public DummyPidServiceAOImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	@Override
	public Identifier getIdentifier(String path) throws JargonException {
		return getFakeIdentifier();
	}

	@Override
	public DataOneObject getObject(Identifier identifier) throws JargonException {
		return new FileDataOneObject(getPublicationContext(), getIrodsAccount(), identifier, new Date(),
				new DataObject());
	}

	private Identifier getFakeIdentifier() {
		Identifier id = new Identifier();
		id.setValue(new String("http://handle/dummy/" + String.valueOf(System.currentTimeMillis())));
		return id;
	}
}
