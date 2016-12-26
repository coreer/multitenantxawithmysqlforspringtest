package test.coreer.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import lombok.extern.log4j.Log4j2;
import test.coreer.model.tenant.TenantBaseObject;

/**
 * Created by aieremenko on 12/2/16.
 */
@Log4j2
@Configuration
@DependsOn(XAConfig.TRANSACTION_MANAGER_NAME)
@ComponentScan("com.dimanex.api.config.multitenancy")
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
    entityManagerFactoryRef = TenantsDatabaseConfig.TENANT_ENTITY_MANAGER_NAME,
    transactionManagerRef = XAConfig.TRANSACTION_MANAGER_NAME,
    basePackages = {"test.coreer.repository.tenant"})
public class TenantsDatabaseConfig {
    public static final String TENANT_ENTITY_MANAGER_NAME = "tenantEntityManager";


    @Bean(name = TENANT_ENTITY_MANAGER_NAME)
    @DependsOn(XAConfig.TRANSACTION_MANAGER_NAME)
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(JpaVendorAdapter jpaVendorAdapter,
                                                                             @Value("${spring.jpa.properties.hibernate.dialect}")
                                                                                 String hibernateDialect,
                                                                             @Qualifier(XAConfig.DEFAULT_TENANT_DATA_SOURCE_NAME)
                                                                                 DataSource defaultTenantDatasource,
                                                                             MultiTenantConnectionProvider connectionProvider,
                                                                             CurrentTenantIdentifierResolver tenantResolver) {

        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        //emfBean.setDataSource(defaultTenantDatasource);
        emfBean.setJtaDataSource(defaultTenantDatasource);
        emfBean.setPackagesToScan(TenantBaseObject.class.getPackage().getName());
        emfBean.setJpaVendorAdapter(jpaVendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);

        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        emfBean.setJpaPropertyMap(properties);

        return emfBean;
    }


}
