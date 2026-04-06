package com.orion.engineering.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "orion")
@Data
public class ApplicationConfiguration
{
    //private String bitbucketApiUsername;
    //private String bitbucketApiToken;
    //private String bitbucketApiBaseUrl;


    /*@Data
    public static class Class1 {
        private String token;
    }*/
}
