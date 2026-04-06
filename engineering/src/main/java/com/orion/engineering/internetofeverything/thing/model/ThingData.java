package com.orion.engineering.internetofeverything.thing.model;

import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingData
{
    private Thing thing;
    private ThingConfiguration configuration;
    private ThingCertificates certificates;
    private ThingPolicy policy;
}
