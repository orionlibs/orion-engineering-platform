package com.orion.engineering.internetofeverything.communication;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayload;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayloadObservable;
import com.orion.engineering.internetofeverything.thing.payload.ThingPayloadObserver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingPayloadReceiver implements ThingPayloadObservable
{
    private final List<ThingPayloadObserver> observers = new ArrayList<>();
    @Autowired @Qualifier(value = "mockDatabase") private Database database;


    @Override
    public void addObserver(ThingPayloadObserver observer)
    {
        observers.add(observer);
    }


    @Override
    public void removeObserver(ThingPayloadObserver observer)
    {
        observers.remove(observer);
    }


    @Override
    public void notifyObservers(ThingPayload payload)
    {
        observers.forEach(observer -> observer.onPayloadReceived(payload));
    }


    public void receive(String payload)
    {
        ThingPayload payload1 = ThingPayload.builder()
                        .payload(payload)
                        .build();
        database.save(payload1);
        notifyObservers(payload1);
    }
}
