package test.coreer.config.multitenancy;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Data;
import test.coreer.db.migration.DatabaseEvolutionary;
import test.coreer.model.master.Tenant;

/**
 * Created by aieremenko on 12/12/16.
 */
@Component
@Configuration
@EnableConfigurationProperties(DevTenantDatasourceProvider.DevTenantConfigs.class)
public class DevTenantDatasourceProvider implements TenantDatasourceProvider {

    @Autowired
    private DevTenantConfigs tenantConfigs;

    @Autowired
    private DatabaseEvolutionary databaseEvolutionary;

    @Autowired
    private TenantConfig tenantConfig;

    @Override
    public DataSource createDataSource(String dataSourceClassName, Tenant tenant) {
        final String url = String.format(
            tenantConfigs.getUrlTemplate(),
            tenant.getDatabase().getHost(),
            tenant.getDatabase().getPort(),
            tenant.getDatabase().getSchema()
        );

        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(url);
        mysqlDataSource.setUser(tenantConfigs.getUsername());
        mysqlDataSource.setPassword(tenantConfigs.getPassword());
        databaseEvolutionary.applyEvolutions(mysqlDataSource);


        final MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setURL(url);
        mysqlXADataSource.setUser(tenantConfigs.getUsername());
        mysqlXADataSource.setPassword(tenantConfigs.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setUniqueResourceName("tenantXADatasource" + tenant.getName());
        xaDataSource.setXaDataSourceClassName(MysqlXADataSource.class.getName());
        xaDataSource.setMinPoolSize(tenantConfig.getAtomikosTenantConfig().getMinPoolSize());
        xaDataSource.setMaxPoolSize(tenantConfig.getAtomikosTenantConfig().getMaxPoolSize());

        return xaDataSource;
    }

    @Data
    @ConfigurationProperties(prefix = "coreer.multitenancy.tenant")
    public static class DevTenantConfigs {
        private String username;
        private String password;
        private String urlTemplate;


    }
}
