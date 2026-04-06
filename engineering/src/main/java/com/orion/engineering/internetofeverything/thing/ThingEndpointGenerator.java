package com.orion.engineering.internetofeverything.thing;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ThingEndpointGenerator
{
    public String generateThingEndpoint(UUID thingID)
    {
        return "https://orionioe.com/things/" + thingID.toString();
    }
}
