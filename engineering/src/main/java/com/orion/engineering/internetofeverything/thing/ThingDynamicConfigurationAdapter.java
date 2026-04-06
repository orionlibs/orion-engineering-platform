package com.orion.engineering.internetofeverything.thing;

public interface ThingDynamicConfigurationAdapter
{
    boolean shouldThingAdaptItsConfiguration(ThingContext thingContext, ThingConfiguration thingConfiguration);


    ThingConfiguration adaptConfiguration(ThingContext thingContext, ThingConfiguration thingConfiguration);
}
