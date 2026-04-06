package com.orion.engineering.data.storage;

import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service(value = "mockDatabase")
public class MockDatabase implements Database
{
    @Override
    public void save(Object model)
    {
    }


    @Override public ThingConfiguration getThingConfigurationByID(UUID thingID)
    {
        return ThingConfiguration.builder()
                        .endpoint("https://orionioe.com/things/" + thingID.toString())
                        .build();
    }
}
