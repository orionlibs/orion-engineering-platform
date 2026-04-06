package com.orion.engineering.internetofeverything.thing.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingDescriptor
{
    private List<ThingDescriptorItem> descriptorItems;
}
