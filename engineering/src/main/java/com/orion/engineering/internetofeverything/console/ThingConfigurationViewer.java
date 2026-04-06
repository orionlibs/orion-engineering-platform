package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingConfigurationViewer
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;


    public ThingConfiguration getThingConfiguration(UUID thingID)
    {
        return database.getThingConfigurationByID(thingID);
    }
}
