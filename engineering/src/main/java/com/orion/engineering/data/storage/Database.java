package com.orion.engineering.data.storage;

import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import java.util.UUID;

public interface Database
{
    void save(Object model);


    ThingConfiguration getThingConfigurationByID(UUID thingID);
}
