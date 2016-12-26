package test.coreer.config.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Created by aieremenko on 12/15/16.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coreer.multitenancy.tenant")
public class TenantConfig {
    private String urlTemplate;
    private DefaultDb defaultDb;
    @Autowired
    private AtomikosTenantConfig atomikosTenantConfig;

    @Data
    @ConfigurationProperties(prefix = "coreer.multitenancy.tenant.defaultDb")
    public static class DefaultDb {
        private String url;
        private String username;
        private String password;
    }
}
