package com.example.manager.util;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="portal")
@Component
@Data
@ToString
public class PortalProperties {
    /**
     * 门户地址
     */
    private String url;

    /**
     * 门户登录cookie
     */
    private String cookie;

    private String token;
}
