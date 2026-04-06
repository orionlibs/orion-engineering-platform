package com.orion.engineering.internetofeverything.thing.security;

import java.io.File;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThingCertificates
{
    private File certificateFile;
    private File privateKeyFile;
}
