package test.coreer.config.multitenancy;

import javax.sql.DataSource;

import test.coreer.model.master.Tenant;

/**
 * Created by aieremenko on 12/12/16.
 */
public interface TenantDatasourceProvider {
    DataSource createDataSource(String dataSourceClassName, Tenant tenant);
}
