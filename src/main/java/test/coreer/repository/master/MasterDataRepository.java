package test.coreer.repository.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import test.coreer.model.master.MasterData;
import test.coreer.model.master.Tenant;

/**
 * Created by aieremenko on 12/26/16.
 */
public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {
    @Query(value = "SELECT d FROM MasterData d  WHERE d.tenant = :tenant")
    MasterData findByTenant(@Param("tenant") Tenant tenant);
}
