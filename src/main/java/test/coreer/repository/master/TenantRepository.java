package test.coreer.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;

import test.coreer.model.master.Tenant;

/**
 * Created by aieremenko on 12/26/16.
 */
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
}
