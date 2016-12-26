package test.coreer.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import test.coreer.config.XAConfig;
import test.coreer.dto.DataDTO;
import test.coreer.model.master.MasterData;
import test.coreer.model.master.Tenant;
import test.coreer.repository.master.MasterDataRepository;
import test.coreer.repository.master.TenantRepository;
import test.coreer.repository.tenant.TenantDataRepository;

/**
 * Created by aieremenko on 12/26/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest( classes = DataServiceTests.CurrentTenantProviderImpl.class)
@Transactional(propagation = REQUIRES_NEW, isolation = READ_UNCOMMITTED)
public class DataServiceTests {

    @Autowired
    private DataService dataService;

    @Autowired
    private MasterDataRepository masterDataRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantDataRepository tenantDataRepository;



    private static final int CUR_TENANT_ID = 3;
    @TestConfiguration
    public static class CurrentTenantProviderImpl {
        @Bean
        public CurrentTenantProvider authUtility() {
            return () -> CUR_TENANT_ID;
        }

    }

    @Test
    public void new_data_should_be_able_to_be_stored_to_distributed_repository() {
        final String newMasterParam1 = "newMasterParam1";
        final String newMasterParam2 = "newMasterParam2";
        final String newTenantParam1 = "newTenantParam1";
        final String newTenantParam2 = "newTenantParam2";
        final DataDTO dataDTO = DataDTO.builder()
            .param1(newMasterParam1)
            .param2(newMasterParam2)
            .param3(newTenantParam1)
            .param4(newTenantParam2)
            .build();
        final DataDTO result = dataService.storeData(dataDTO);

        final Tenant curTenant = tenantRepository.findOne(CUR_TENANT_ID);

        final MasterData masterData = masterDataRepository.findByTenant(curTenant);

        assertThat(masterData.getMcolumn1(), equalTo(newMasterParam1));
        assertThat(masterData.getMcolumn2(), equalTo(newMasterParam2));
    }


    private static final int MASTER_DATA_TEST_DATA1_ID = 10;
    private static final String MASTER_DATA_TEST_DATA1_COLUMN1 = "masterDataTestData1Column1";
    private static final String MASTER_DATA_TEST_DATA1_COLUMN2 = "masterDataTestData1Column2";

    private static final int TENANT_DATA_TEST_DATA1_ID = 20;
    private static final String TENANT_DATA_TEST_DATA1_COLUMN1 = "tenantDataTestData1Column1";
    private static final String TENANT_DATA_TEST_DATA1_COLUMN2 = "tenantDataTestData1Column2";
    @SqlGroup({
        @Sql(
            statements = "INSERT INTO master_data (id, mcolumn1, mcolumn2, tenant_id) VALUES (" + MASTER_DATA_TEST_DATA1_ID + ", '"+ MASTER_DATA_TEST_DATA1_COLUMN1 +"', '" + MASTER_DATA_TEST_DATA1_COLUMN2 +"', " + CUR_TENANT_ID +");",
            config = @SqlConfig(dataSource = XAConfig.MASTER_DATA_SOURCE_NAME)
        ),
        @Sql(
            statements = "INSERT INTO tenant_data (id, tcolumn1, tcolumn2, tenant_id) VALUES (" + TENANT_DATA_TEST_DATA1_ID + ", '"+ TENANT_DATA_TEST_DATA1_COLUMN1 +"', '" + TENANT_DATA_TEST_DATA1_COLUMN2 +"', " + CUR_TENANT_ID +");",
            config = @SqlConfig(dataSource = XAConfig.DEFAULT_TENANT_DATA_SOURCE_NAME)
        )
    })
    @Test
    public void testdData_from_Sql_annotation_should_be_already_accessible_for_service_under_test() {
        final DataDTO dataDTO = DataDTO.builder()
            .masterRowId(MASTER_DATA_TEST_DATA1_ID)
            .param1("updatedMasterParam1")
            .param2("updatedMasterParam2")
            .param3("updatedTenantParam1")
            .param4("updatedTenantParam2")
            .build();
        final DataDTO result = dataService.updateData(dataDTO);
    }

}