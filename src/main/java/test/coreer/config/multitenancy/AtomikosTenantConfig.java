package test.coreer.config.multitenancy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Created by aieremenko on 12/22/16.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coreer.multitenancy.tenant.jta.atomikos.connectionfactory")
public class AtomikosTenantConfig {
    private int minPoolSize;
    private int maxPoolSize;
}
