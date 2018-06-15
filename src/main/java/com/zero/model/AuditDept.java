package com.zero.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:configuration.properties")
@ConfigurationProperties(prefix = "business.audit")
public class AuditDept {

    private String plan;
    private String storage;
}
