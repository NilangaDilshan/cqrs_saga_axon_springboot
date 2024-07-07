package com.dilshan.productservice.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XStream configuration class
 * https://stackoverflow.com/questions/75604172/forbiddenclassexception-appears-during-handling-axon-event
 */
@Configuration
public class XStreamConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        xStream.addPermission(AnyTypePermission.ANY);

        return xStream;
    }
}