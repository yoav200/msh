package com.choyo.msh.socialauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "msh.socialauth")
public class SocialauthControllerConfig {

    private String baseCallbackUrl;

    public String successPageUrl;

    public String accessDeniedPageUrl;
}