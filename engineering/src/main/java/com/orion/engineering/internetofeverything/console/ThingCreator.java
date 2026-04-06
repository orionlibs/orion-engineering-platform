package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.Thing;
import com.orion.engineering.internetofeverything.thing.ThingPlatform;
import com.orion.engineering.internetofeverything.thing.ThingSDK;

public class ThingCreator
{
    public Thing create(String name, ThingPlatform platform, ThingSDK sdk)
    {
        return Thing.builder()
                        .name(name)
                        .platform(platform)
                        .sdk(sdk)
                        .build();
    }
}
