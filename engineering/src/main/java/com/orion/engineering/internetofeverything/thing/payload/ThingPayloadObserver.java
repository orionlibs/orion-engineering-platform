package com.orion.engineering.internetofeverything.thing.payload;

public interface ThingPayloadObserver
{
    void onPayloadReceived(ThingPayload payload);
}
