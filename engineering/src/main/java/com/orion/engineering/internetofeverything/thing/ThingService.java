package com.orion.engineering.internetofeverything.thing;

import com.orion.engineering.data.storage.Database;
import com.orion.engineering.internetofeverything.thing.model.Thing;
import com.orion.engineering.internetofeverything.thing.model.ThingConfiguration;
import com.orion.engineering.internetofeverything.thing.model.ThingData;
import com.orion.engineering.internetofeverything.thing.model.ThingPlatform;
import com.orion.engineering.internetofeverything.thing.model.ThingSDK;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificateService;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicy;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicyService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingService
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;
    @Autowired private ThingPolicyService thingPolicyService;
    @Autowired private ThingConfigurationService thingConfigurationService;
    @Autowired private ThingCertificateService thingCertificateService;


    public ThingData create(String name, String description, String type, ThingPlatform platform, ThingSDK sdk)
    {
        Thing thing = Thing.builder()
                        .id(UUID.randomUUID())
                        .name(name)
                        .description(description)
                        .type(type)
                        .platform(platform.name())
                        .sdk(sdk.name())
                        .build();
        database.save(thing);
        ThingPolicy policy = thingPolicyService.addEmptyPolicyToThing(thing.getId());
        ThingConfiguration config = thingConfigurationService.addDefaultConfigurationToThing(thing.getId());
        ThingCertificates certs = thingCertificateService.addCertificatesToThing(thing.getId());
        return getAllThingData(thing.getId());
    }


    public ThingData getAllThingData(UUID thingID)
    {
        return ThingData.builder()
                        .thing(database.getThingByID(thingID))
                        .configuration(database.getThingConfigurationByID(thingID))
                        .certificates(database.getThingCertificatesByID(thingID))
                        .policy(database.getThingPolicyByID(thingID))
                        .build();
    }
}
