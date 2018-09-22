package com.choyo.msh.payment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "msh.payment")
public class BraintreeGatewayConfig {

    private String environment;

    private String merchantId;

    private String publicKey;

    private String privateKey;

}
