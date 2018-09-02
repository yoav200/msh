package com.choyo.msh;

import com.choyo.msh.messages.MessagesManager;
import com.choyo.msh.socialauth.SocialAuthTemplate;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

@SpringBootApplication
public class MshApplication {

    public static void main(String[] args) {
        SpringApplication.run(MshApplication.class, args);
    }

    @Bean
    public SocialAuthConfig socialAuthConfig() throws Exception {
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("oauth_consumer.properties"));
        SocialAuthConfig socialAuthConfig = new SocialAuthConfig();
        socialAuthConfig.setApplicationProperties(properties);
        return socialAuthConfig;
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SocialAuthTemplate SocialAuthTemplate() {
        return new SocialAuthTemplate();
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SocialAuthManager socialAuthManager(SocialAuthConfig socialAuthConfig) throws Exception {
        SocialAuthManager socialAuthManager = new SocialAuthManager();
        socialAuthManager.setSocialAuthConfig(socialAuthConfig);
        return socialAuthManager;
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public MessagesManager messagesManager() {
        return new MessagesManager();
    }
}
