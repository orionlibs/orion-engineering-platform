package com.orion.engineering.internetofeverything.thing;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingDescriptorItem
{
    private String name;
    private String description;
}
