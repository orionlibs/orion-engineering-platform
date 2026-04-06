package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.internetofeverything.thing.model.CurrentThingOperatingConditions;
import com.orion.engineering.internetofeverything.thing.model.ThingContext;

public interface ThingDynamicConditionsAdapter
{
    boolean shouldThingAdaptItsOperatingConditions(ThingContext thingContext);


    CurrentThingOperatingConditions adaptOperatingConditions(ThingContext thingContext);
}
