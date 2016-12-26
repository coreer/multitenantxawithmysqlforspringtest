package test.coreer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import test.coreer.dto.DataDTO;
import test.coreer.model.master.MasterData;
import test.coreer.model.master.Tenant;
import test.coreer.model.tenant.TenantData;
import test.coreer.repository.master.MasterDataRepository;
import test.coreer.repository.master.TenantRepository;
import test.coreer.repository.tenant.TenantDataRepository;

/**
 * Created by aieremenko on 12/26/16.
 */
@Service
@Transactional
public class DataService {

    private final CurrentTenantProvider currentTenantProvider;
    private final TenantRepository tenantRepository;
    private final MasterDataRepository masterDataRepository;
    private final TenantDataRepository tenantDataRepository;

    @Autowired
    public DataService(CurrentTenantProvider currentTenantProvider,
                       TenantRepository tenantRepository,
                       MasterDataRepository masterDataRepository,
                       TenantDataRepository tenantDataRepository) {
        this.currentTenantProvider = currentTenantProvider;
        this.tenantRepository = tenantRepository;
        this.masterDataRepository = masterDataRepository;
        this.tenantDataRepository = tenantDataRepository;
    }


    public DataDTO storeData(DataDTO data) {
        final Tenant currentTenant = tenantRepository.findOne(currentTenantProvider.getCurrentTenantId());

        final MasterData masterData = MasterData.builder()
            .mcolumn1(data.getParam1())
            .mcolumn2(data.getParam2())
            .tenant(currentTenant)
            .build();

        final TenantData tenantData = TenantData.builder()
            .tcolumn1(data.getParam3())
            .tcolumn2(data.getParam4())
            .tenantId(currentTenant.getId())
            .build();

        masterDataRepository.save(masterData);
        tenantDataRepository.save(tenantData);

        data.setMasterRowId(masterData.getId());
        data.setTenantRowId(tenantData.getId());

        return data;
    }


    public DataDTO updateData(DataDTO data) {

        final MasterData masterData = masterDataRepository.findOne(data.getMasterRowId());
        masterData.setMcolumn1(data.getParam1());
        masterData.setMcolumn2(data.getParam2());

        final TenantData tenantData = tenantDataRepository.findOne(data.getTenantRowId());
        tenantData.setTcolumn1(data.getParam3());
        tenantData.setTcolumn2(data.getParam4());

        masterDataRepository.save(masterData);
        tenantDataRepository.save(tenantData);

        data.setMasterRowId(masterData.getId());
        data.setTenantRowId(tenantData.getId());

        return data;
    }

}
