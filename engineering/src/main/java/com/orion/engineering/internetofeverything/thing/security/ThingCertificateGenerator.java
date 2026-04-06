package com.orion.engineering.internetofeverything.thing.security;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ThingCertificateGenerator
{
    public String generateCertificateFile(UUID thingID)
    {
        return UUID.randomUUID().toString();
    }


    public String generatePrivateKeyFile(UUID thingID)
    {
        return UUID.randomUUID().toString();
    }
}
