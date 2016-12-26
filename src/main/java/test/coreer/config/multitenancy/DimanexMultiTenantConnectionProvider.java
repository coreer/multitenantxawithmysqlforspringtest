package test.coreer.config.multitenancy;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import test.coreer.repository.master.TenantRepository;

/**
 * Created by aieremenko on 12/2/16.
 */
@Component
public class DimanexMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl implements ApplicationListener<ContextRefreshedEvent> {
    private final String dataSourceClassName;
    private final Map<Integer, DataSource> tenantDatasources = new HashMap<>();
    private final TenantRepository tenantRepository;
    private final TenantDatasourceProvider datasourceProvider;

    @Autowired
    public DimanexMultiTenantConnectionProvider(@Value("${spring.datasource.dataSourceClassName}") String dataSourceClassName,
                                                TenantRepository tenantRepository,
                                                TenantDatasourceProvider datasourceProvider) {

        this.dataSourceClassName = dataSourceClassName;
        this.tenantRepository = tenantRepository;
        this.datasourceProvider = datasourceProvider;

    }

    @Override
    protected DataSource selectAnyDataSource() {
        return null;
    }

    @Override
    protected DataSource selectDataSource(String tenant) {
        return tenantDatasources.get(Integer.parseInt(tenant));
    }

    @Override
    @Transactional(readOnly = true, propagation = SUPPORTS)
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        tenantRepository.findAll().forEach(tenant -> {
                final DataSource dataSource = datasourceProvider.createDataSource(dataSourceClassName, tenant);
                tenantDatasources.put(tenant.getId(), dataSource);
            }
        );
    }
}
