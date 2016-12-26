package test.coreer.config;

import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import lombok.extern.log4j.Log4j2;
import test.coreer.model.master.MasterBaseObject;

/**
 * Created by aieremenko on 12/1/16.
 */
@Log4j2
@Configuration
@DependsOn(XAConfig.TRANSACTION_MANAGER_NAME)
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
    entityManagerFactoryRef = MasterDatabaseConfig.MASTER_ENTITY_MANAGER_NAME,
    transactionManagerRef = XAConfig.TRANSACTION_MANAGER_NAME,
    basePackages = {"test.coreer.repository.master"})
public class MasterDatabaseConfig {
    public static final String MASTER_ENTITY_MANAGER_NAME = "masterEntityManager";


    @Bean(name = MASTER_ENTITY_MANAGER_NAME)
    @DependsOn(XAConfig.TRANSACTION_MANAGER_NAME)
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(JpaVendorAdapter jpaVendorAdapter,
                                                                             DataSource masterDataSource,
                                                                             JpaProperties jpaProperties) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJtaDataSource(masterDataSource);
        //em.setDataSource(masterDataSource);
        em.setPackagesToScan(new String[]{MasterBaseObject.class.getPackage().getName()});
        em.setJpaVendorAdapter(jpaVendorAdapter);

        em.setJpaProperties(new Properties(){{
            final Properties self = this;
            self.setProperty("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
            self.setProperty("javax.persistence.transactionType", "JTA");
            jpaProperties.getHibernateProperties(masterDataSource).forEach((k, v) -> self.setProperty(k, v));
        }});

        em.setPersistenceUnitName("master");

        return em;
    }


}
