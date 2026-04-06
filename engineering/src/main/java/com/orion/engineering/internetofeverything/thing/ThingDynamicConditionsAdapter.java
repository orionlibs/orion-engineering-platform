package com.orion.engineering.internetofeverything.thing;

public interface ThingDynamicConditionsAdapter
{
    boolean shouldThingAdaptItsOperatingConditions(ThingContext thingContext);


    CurrentThingOperatingConditions adaptOperatingConditions(ThingContext thingContext);
}
