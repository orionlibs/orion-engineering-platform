package com.orion.engineering.data.storage;

import com.orion.engineering.internetofeverything.thing.model.Thing;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicy;
import java.util.UUID;

public interface Database
{
    void save(Object model);


    Thing getThingByID(UUID thingID);


    ThingConfiguration getThingConfigurationByID(UUID thingID);


    ThingPolicy getThingPolicyByID(UUID thingID);


    ThingCertificates getThingCertificatesByID(UUID thingID);
}
