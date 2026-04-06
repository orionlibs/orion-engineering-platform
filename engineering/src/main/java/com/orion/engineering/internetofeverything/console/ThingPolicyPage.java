package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.model.ThingOperation;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicy;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicyService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingPolicyPage
{
    @Autowired private ThingPolicyService thingPolicyService;


    public ThingPolicy getThingPolicy(UUID thingID)
    {
        return thingPolicyService.getThingPolicy(thingID);
    }


    public void addAllowedThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        thingPolicyService.addAllowedThingOperationToThingPolicy(thingID, operation);
    }


    public void removeAllowedThingOperationToThingPolicy(UUID thingID, ThingOperation operation)
    {
        thingPolicyService.removeAllowedThingOperationToThingPolicy(thingID, operation);
    }
}
