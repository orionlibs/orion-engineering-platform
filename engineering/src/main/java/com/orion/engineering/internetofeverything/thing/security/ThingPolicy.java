package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.internetofeverything.thing.model.ThingOperation;
import java.util.EnumSet;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingPolicy
{
    private UUID thingID;
    private EnumSet<ThingOperation> allowedOperations;
}
