package test.coreer.repository.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import test.coreer.model.tenant.TenantData;

/**
 * Created by aieremenko on 12/26/16.
 */
public interface TenantDataRepository extends JpaRepository<TenantData, Integer> {
}
