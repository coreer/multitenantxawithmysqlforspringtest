package test.coreer.config;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import lombok.extern.log4j.Log4j2;
import test.coreer.config.multitenancy.TenantConfig;
import test.coreer.db.migration.DatabaseEvolutionary;
import test.coreer.db.migration.FlywayDbEvolutionary;

/**
 * Created by aieremenko on 12/15/16.
 */
@Log4j2
@Configuration
@Import({MasterDatabaseConfig.class, TenantsDatabaseConfig.class})
@EnableTransactionManagement
public class XAConfig {
    public static final String MASTER_DATA_SOURCE_NAME = "masterDataSource";
    public static final String DEFAULT_TENANT_DATA_SOURCE_NAME = "defaultTenantDataSource";
    public static final String TRANSACTION_MANAGER_NAME = "transactionManager";
    public static final String USER_TRANSACTION_NAME = "atomikosUserTransaction";
    public static final String USER_TRANSACTION_MANAGER_NAME = "atomikosUserTransactionManager";

    @Value("${spring.datasource.dataSourceClassName}")
    private String dataSourceClassName;

    @Value("${spring.jpa.database-platform}")
    private String jpaDatabasePlatform;

    @Value("${spring.jta.atomikos.connectionfactory.min-pool-size}")
    private int atomikosMinPoolSize;
    @Value("${spring.jta.atomikos.connectionfactory.max-pool-size}")
    private int atomikosMaxPoolSize;

    @Autowired
    private TenantConfig tenantConfig;

    @Bean
    public DatabaseEvolutionary flywayDbEvolutionaryForTenants() {
        return new FlywayDbEvolutionary();
    }

    @Bean(name = USER_TRANSACTION_NAME)
    public UserTransaction atomikosUserTransaction() throws Throwable {
        UserTransactionImp atomikosUserTransaction = new UserTransactionImp();
        atomikosUserTransaction.setTransactionTimeout(10000);
        return atomikosUserTransaction;
    }

    @Bean(name = USER_TRANSACTION_MANAGER_NAME, initMethod = "init", destroyMethod = "close")
    public TransactionManager atomikosUserTransactionManager() throws Throwable {
        UserTransactionManager atomikosUserTransactionManager = new UserTransactionManager();
        atomikosUserTransactionManager.setForceShutdown(false);

        return atomikosUserTransactionManager;
    }

    @Bean(name = TRANSACTION_MANAGER_NAME)
    @DependsOn({USER_TRANSACTION_NAME, USER_TRANSACTION_MANAGER_NAME})
    public PlatformTransactionManager transactionManager(UserTransaction atomikosUserTransaction,
                                                         TransactionManager atomikosUserTransactionManager) throws Throwable {
        AtomikosJtaPlatform.jtaTransactionManager = new JtaTransactionManager(atomikosUserTransaction, atomikosUserTransactionManager);
        AtomikosJtaPlatform.jtaTransactionManager.setAllowCustomIsolationLevels(true);
        return AtomikosJtaPlatform.jtaTransactionManager;
    }


    @Bean(name = MASTER_DATA_SOURCE_NAME, destroyMethod = "close")
    @Primary
    @DependsOn(TRANSACTION_MANAGER_NAME)
    public DataSource masterDataSource(@Value("${spring.datasource.url}") String url,
                                       @Value("${spring.datasource.username}") String user,
                                       @Value("${spring.datasource.password}") String password) {

        log.debug("Configuring datasource {} {} {}", dataSourceClassName, url, user);

        final FlywayDbEvolutionary flywayDbEvolutionary = new FlywayDbEvolutionary("classpath:db/migration/master");
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(url);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setPassword(password);
        flywayDbEvolutionary.applyEvolutions(mysqlDataSource);


        final MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setURL(url);
        mysqlXADataSource.setUser(user);
        mysqlXADataSource.setPassword(password);


        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setUniqueResourceName("masterXADatasource");
        xaDataSource.setXaDataSourceClassName(MysqlXADataSource.class.getName());
        xaDataSource.setMinPoolSize(atomikosMinPoolSize);
        xaDataSource.setMaxPoolSize(atomikosMaxPoolSize);


        return xaDataSource;

    }

    @Bean(name = DEFAULT_TENANT_DATA_SOURCE_NAME, destroyMethod = "close")
    @DependsOn(TRANSACTION_MANAGER_NAME)
    public DataSource defaultTenantDataSource(DatabaseEvolutionary flywayDbEvolutionary) {
        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(tenantConfig.getDefaultDb().getUrl());
        mysqlDataSource.setUser(tenantConfig.getDefaultDb().getUsername());
        mysqlDataSource.setPassword(tenantConfig.getDefaultDb().getPassword());
        flywayDbEvolutionary.applyEvolutions(mysqlDataSource);


        final MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setURL(tenantConfig.getDefaultDb().getUrl());
        mysqlXADataSource.setUser(tenantConfig.getDefaultDb().getUsername());
        mysqlXADataSource.setPassword(tenantConfig.getDefaultDb().getPassword());


        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setUniqueResourceName("defaultTenantXADatasource");
        xaDataSource.setXaDataSourceClassName(MysqlXADataSource.class.getName());
        xaDataSource.setMinPoolSize(tenantConfig.getAtomikosTenantConfig().getMinPoolSize());
        xaDataSource.setMaxPoolSize(tenantConfig.getAtomikosTenantConfig().getMaxPoolSize());

        return xaDataSource;

    }

    @Bean
    @DependsOn({MASTER_DATA_SOURCE_NAME, DEFAULT_TENANT_DATA_SOURCE_NAME})
    public JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform(jpaDatabasePlatform);
        hibernateJpaVendorAdapter.setShowSql(true);
        return hibernateJpaVendorAdapter;
    }

}
