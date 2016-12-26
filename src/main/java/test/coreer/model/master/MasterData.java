package test.coreer.model.master;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by aieremenko on 12/26/16.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = MasterData.MASTER_DATA_TABLE_NAME)
public class MasterData extends MasterBaseObject {
    public static final String MASTER_DATA_TABLE_NAME = "master_data";
    public static final String MASTER_DATA_LINK_COLUMN_TO_TENANT = "tenant_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String mcolumn1;
    private String mcolumn2;
    @ManyToOne
    @JoinColumn(name = MASTER_DATA_LINK_COLUMN_TO_TENANT, referencedColumnName = "id")
    private Tenant tenant;
}
