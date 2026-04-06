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
    @Autowired private ThingCertificateGenerator thingCertificateGenerator;


    public ThingCertificates getThingCertificates(UUID thingID)
    {
        return database.getThingCertificatesByID(thingID);
    }


    public ThingCertificates addCertificatesToThing(UUID thingID)
    {
        ThingCertificates certs = getThingCertificates(thingID);
        if(certs == null)
        {
            certs = ThingCertificates.builder()
                            .thingID(thingID)
                            .certificateFile(thingCertificateGenerator.generateCertificateFile(thingID))
                            .privateKeyFile(thingCertificateGenerator.generatePrivateKeyFile(thingID))
                            .build();
            database.save(certs);
        }
        return certs;
    }
}
