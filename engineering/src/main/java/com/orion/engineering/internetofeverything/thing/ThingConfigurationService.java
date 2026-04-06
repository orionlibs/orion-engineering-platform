package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingConfigurationService
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;
    @Autowired private ThingEndpointGenerator thingEndpointGenerator;


    public ThingConfiguration addDefaultConfigurationToThing(UUID thingID)
    {
        ThingConfiguration config = getThingConfiguration(thingID);
        if(config == null)
        {
            config = ThingConfiguration.builder()
                            .thingID(thingID)
                            .endpoint(thingEndpointGenerator.generateThingEndpoint(thingID))
                            .build();
            database.save(config);
        }
        return config;
    }


    public ThingConfiguration getThingConfiguration(UUID thingID)
    {
        return database.getThingConfigurationByID(thingID);
    }
}
