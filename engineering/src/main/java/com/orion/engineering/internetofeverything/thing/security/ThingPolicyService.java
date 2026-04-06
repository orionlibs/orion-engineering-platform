package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.ThingOperation;
import java.util.HashMap;
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
                            .operationToAllowedStatusMapper(new HashMap<>())
                            .build();
            database.save(policy);
        }
        return policy;
    }


    public void addNameToThingPolicy(UUID thingID, String name)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.setName(name);
        database.save(policy);
    }


    public void addThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getOperationToAllowedStatusMapper().put(operation, true);
        database.save(policy);
    }


    public void removeThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getOperationToAllowedStatusMapper().remove(operation);
        database.save(policy);
    }


    public void allowThingOperation(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getOperationToAllowedStatusMapper().put(operation, true);
        database.save(policy);
    }


    public void forbidThingOperation(UUID thingID, ThingOperation operation)
    {
        ThingPolicy policy = database.getThingPolicyByID(thingID);
        policy.getOperationToAllowedStatusMapper().put(operation, false);
        database.save(policy);
    }
}
