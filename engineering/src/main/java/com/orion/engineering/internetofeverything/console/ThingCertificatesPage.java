package com.orion.engineering.internetofeverything.console;

import com.orion.engineering.internetofeverything.thing.security.ThingCertificateService;
import com.orion.engineering.internetofeverything.thing.security.ThingCertificates;
import com.orion.engineering.internetofeverything.thing.security.ThingPolicyNotExistException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingCertificatesPage
{
    @Autowired private ThingCertificateService thingCertificateService;


    public ThingCertificates getThingCertificates(UUID thingID)
    {
        return thingCertificateService.getThingCertificates(thingID);
    }


    public void addAllowedThingOperationToThingPolicy(UUID thingID) throws ThingPolicyNotExistException
    {
        thingCertificateService.addCertificatesToThing(thingID);
    }
}
