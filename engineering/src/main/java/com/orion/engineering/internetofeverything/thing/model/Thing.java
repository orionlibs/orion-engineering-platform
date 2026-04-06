package com.orion.engineering.internetofeverything.thing.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Thing
{
    private UUID id;
    private String uniqueResourceID;
    private String name;
    private String description;
    private String type;
    private String platform;
    private String sdk;
}
