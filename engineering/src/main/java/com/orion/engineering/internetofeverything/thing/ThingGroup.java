package com.orion.engineering.internetofeverything.thing;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingGroup
{
    private UUID id;
    private String name;
}
