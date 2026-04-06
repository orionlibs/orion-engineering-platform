package com.orion.engineering.internetofeverything.thing.security;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingCertificates
{
    private UUID thingID;
    private String deviceCertificate;
    private String rootCertificateAuthorityCertificate;
    private String privateKey;
    private String publicKey;
}
