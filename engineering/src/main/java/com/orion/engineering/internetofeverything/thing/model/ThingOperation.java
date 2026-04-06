package com.orion.engineering.internetofeverything.thing.model;

import com.orion.engineering.core.abstraction.Enumeration;

public enum ThingOperation implements Enumeration
{
    SUBSCRIBE_TO_MQTT_TOPIC("ioe::subscribe-to-mqtt-topic"),
    PUBLISH_TO_MQTT_TOPIC("ioe::publish-to-mqtt-topic");
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
