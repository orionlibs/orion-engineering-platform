package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.ThingConfigurationService;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingConfigurationViewer
{
    @Autowired private ThingConfigurationService thingConfigurationService;


    public ThingConfiguration getThingConfiguration(UUID thingID)
    {
        return thingConfigurationService.getThingConfiguration(thingID);
    }
}
