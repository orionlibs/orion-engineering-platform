package com.orion.engineering.internetofeverything.thing.security;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ThingCertificateGenerator
{
    public String generateCertificateFile(ThingPolicy policy)
    {
        return UUID.randomUUID().toString();
    }


    public String generatePublicKey(ThingPolicy policy, String privateKey)
    {
        return UUID.randomUUID().toString();
    }


    public String generatePrivateKey(ThingPolicy policy)
    {
        return UUID.randomUUID().toString();
    }


    public String generateDeviceCertificate(ThingPolicy policy, String privateKey)
    {
        return UUID.randomUUID().toString();
    }


    public String generateRootCertificateAuthorityCertificate(ThingPolicy policy, String privateKey)
    {
        return UUID.randomUUID().toString();
    }
}
