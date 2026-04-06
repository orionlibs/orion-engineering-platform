package com.orion.engineering.internetofeverything.thing.model;

import com.orion.engineering.core.abstraction.Enumeration;

public enum ThingOperation implements Enumeration
{
    EMPTY("Empty"),
    SUBSCRIBE_TO_MQTT_TOPIC("Subscribe to MQTT Topic"),
    PUBLISH_TO_MQTT_TOPIC("Publish to MQTT Topic");
    private String name;


    ThingOperation(String name)
    {
        this.name = name;
    }


    @Override
    public String get()
    {
        return this.name;
    }


    @Override
    public boolean is(Enumeration other)
    {
        return false;
    }


    @Override
    public boolean isNot(Enumeration other)
    {
        return false;
    }
}
