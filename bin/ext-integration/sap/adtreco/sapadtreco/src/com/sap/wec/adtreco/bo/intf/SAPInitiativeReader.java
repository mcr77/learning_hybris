package com.sap.wec.adtreco.bo.intf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;

import java.util.List;

import com.sap.wec.adtreco.bo.impl.SAPInitiative;


public interface SAPInitiativeReader extends BusinessObject
{

	public List<SAPInitiative> getAllInitiatives();

	public List<SAPInitiative> searchInitiatives(final String search);

	public List<SAPInitiative> searchInitiativesForBP(final String businesPartner);

	public SAPInitiative getInitiative(final String id);

	public SAPInitiative getSelectedInitiative(final String id);


}
