package de.hybris.platform.cuppytrail.systemsetup;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cuppy.constants.CuppyConstants;
import de.hybris.platform.cuppy.systemsetup.CuppySystemSetup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SystemSetup(extension = "cuppytrail", process = SystemSetup.Process.ALL, type = SystemSetup.Type.PROJECT)
public class CuppyTrailSystemSetup extends CuppySystemSetup {

    @SystemSetup
    public void importPreReqsFromCuppy(){

        final Map<String, String[]> params = new HashMap<String, String[]>();

        final String[] p_basics = {
                CuppyConstants.PARAM_BASICS_PLAYERS
        };

        params.put(CuppyConstants.EXTENSIONNAME + "_" + CuppyConstants.PARAM_BASICS, p_basics);

        final String[] p_wc2010 = {
          CuppyConstants.PARAM_WC2010_SETUP
        };

        params.put(CuppyConstants.EXTENSIONNAME + "_" + CuppyConstants.PARAM_WC2010, p_wc2010);

        final SystemSetupContext ctx = new SystemSetupContext(params, SystemSetup.Type.PROJECT, SystemSetup.Process.ALL, CuppyConstants.EXTENSIONNAME);

        //here we use the same context for both. since it is a fairly flat data object, this works just fine
        //super.importBasics(ctx);
        super.importWC2010(ctx);
    }

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getSystemSetupParameters(){
        return Collections.emptyList();
    }
}
