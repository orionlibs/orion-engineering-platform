package com.orion.engineering.internetofeverything.thing.security;

import com.orion.engineering.data.storage.Database;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThingCertificateService
{
    @Autowired @Qualifier(value = "mockDatabase") private Database database;
    @Autowired private ThingPolicyService thingPolicyService;
    @Autowired private ThingCertificateGenerator thingCertificateGenerator;


    public ThingCertificates getThingCertificates(UUID thingID)
    {
        return database.getThingCertificatesByID(thingID);
    }


    public ThingCertificates addCertificatesToThing(UUID thingID) throws ThingPolicyNotExistException
    {
        ThingPolicy policy = thingPolicyService.getThingPolicy(thingID);
        if(policy == null)
        {
            throw new ThingPolicyNotExistException("Thing policy is required before thing certificates can be generated");
        }
        ThingCertificates certs = getThingCertificates(thingID);
        if(certs == null)
        {
            certs = ThingCertificates.builder()
                            .thingID(thingID)
                            .certificateFile(thingCertificateGenerator.generateCertificateFile(policy))
                            .privateKeyFile(thingCertificateGenerator.generatePrivateKeyFile(policy))
                            .build();
            database.save(certs);
        }
        return certs;
    }
}
