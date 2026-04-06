package com.orion.engineering.internetofeverything.thing.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingConfiguration
{
    private UUID thingID;
    private String endpoint;
}
