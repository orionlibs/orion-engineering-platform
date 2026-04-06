package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import com.orion.engineering.internetofeverything.thing.model.ThingContext;

public interface ThingDynamicConfigurationAdapter
{
    boolean shouldThingAdaptItsConfiguration(ThingContext thingContext, ThingConfiguration thingConfiguration);


    ThingConfiguration adaptConfiguration(ThingContext thingContext, ThingConfiguration thingConfiguration);
}
