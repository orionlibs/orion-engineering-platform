package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.ThingOperation;
import java.util.EnumSet;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingPolicyService
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;


    public ThingPolicy getThingPolicy(UUID thingID)
    {
        return database.getThingPolicyByID(thingID);
    }


    public ThingPolicy addEmptyPolicyToThing(UUID thingID)
    {
        ThingPolicy policy = getThingPolicy(thingID);
        if(policy == null)
        {
            policy = ThingPolicy.builder()
                            .thingID(thingID)
                            .allowedOperations(EnumSet.of(ThingOperation.EMPTY))
                            .build();
            database.save(policy);
        }
        return policy;
    }


    public void addAllowedThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getAllowedOperations().add(operation);
        database.save(policy);
    }


    public void removeAllowedThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getAllowedOperations().remove(operation);
        database.save(policy);
    }
}
