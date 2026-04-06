package com.orion.engineering.internetofeverything.thing.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingDescriptorItem
{
    private String name;
    private String description;
}
