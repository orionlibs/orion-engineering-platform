package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.internetofeverything.thing.model.ThingOperation;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ThingPolicy
{
    private UUID thingID;
    private String name;
    private Map<ThingOperation, Boolean> operationToAllowedStatusMapper;
}
