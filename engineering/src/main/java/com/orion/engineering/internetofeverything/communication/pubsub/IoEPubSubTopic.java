package com.orion.engineering.internetofeverything.communication.pubsub;

import com.orion.engineering.core.abstraction.Enumeration;
import com.orion.engineering.core.pubsub.PubSubTopic;

public enum IoEPubSubTopic implements PubSubTopic
{
    THING_PAYLOAD_VIEWER_PAYLOAD("/topic/thing-payload-viewer/payload");
    private String name;


    IoEPubSubTopic(String name)
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
