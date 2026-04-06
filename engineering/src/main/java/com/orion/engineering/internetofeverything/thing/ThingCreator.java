package com.orion.engineering.internetofeverything.thing;

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
