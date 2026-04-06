package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.Thing;
import com.orion.engineering.internetofeverything.thing.model.ThingPlatform;
import com.orion.engineering.internetofeverything.thing.model.ThingSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingCreator
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;


    public Thing create(String name, ThingPlatform platform, ThingSDK sdk)
    {
        Thing thing = Thing.builder()
                        .name(name)
                        .platform(platform)
                        .sdk(sdk)
                        .build();
        database.save(thing);
        return thing;
    }
}
