package com.orion.engineering.internetofeverything.thing.payload;

public interface ThingPayloadObservable
{
    void addObserver(ThingPayloadObserver observer);
    void removeObserver(ThingPayloadObserver observer);
    void notifyObservers(ThingPayload payload);
}
