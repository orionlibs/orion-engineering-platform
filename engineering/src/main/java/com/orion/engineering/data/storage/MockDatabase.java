package com.orion.engineering.data.storage;

import com.orion.engineering.internetofeverything.thing.model.Thing;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service(value = "mockDatabase")
public class MockDatabase implements Database
{
    private final Map<Class<?>, Map<UUID, Object>> cache = new ConcurrentHashMap<>();


    @Override
    public void save(Object model)
    {
        if(model instanceof Thing thing)
        {
            getTable(Thing.class).put(thing.getId(), thing);
        }
        else if(model instanceof ThingConfiguration config)
        {
            getTable(ThingConfiguration.class).put(config.getThingID(), config);
        }
        else if(model instanceof ThingPolicy policy)
        {
            getTable(ThingPolicy.class).put(policy.getThingID(), policy);
        }
    }


    @Override
    public Thing getThingByID(UUID thingID)
    {
        return (Thing)getTable(Thing.class).get(thingID);
    }


    @Override
    public ThingConfiguration getThingConfigurationByID(UUID thingID)
    {
        return (ThingConfiguration)getTable(ThingConfiguration.class).get(thingID);
    }


    @Override
    public ThingPolicy getThingPolicyByID(UUID thingID)
    {
        return (ThingPolicy)getTable(ThingPolicy.class).get(thingID);
    }


    @Override public ThingCertificates getThingCertificatesByID(UUID thingID)
    {
        return (ThingCertificates)getTable(ThingCertificates.class).get(thingID);
    }


    private Map<UUID, Object> getTable(Class<?> type)
    {
        return cache.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
    }
}
