package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.ThingService;
import com.orion.engineering.internetofeverything.thing.model.ThingData;
import com.orion.engineering.internetofeverything.thing.model.ThingPlatform;
import com.orion.engineering.internetofeverything.thing.model.ThingSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingCreatorPage
{
    @Autowired private ThingService thingService;


    public ThingData createThing(String name, String description, String type, ThingPlatform platform, ThingSDK sdk)
    {
        return thingService.create(name, description, type, platform, sdk);
    }
}
