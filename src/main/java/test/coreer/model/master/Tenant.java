package test.coreer.model.master;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created by aieremenko on 12/26/16.
 */
@Data
@Entity
@Table(name = "tenants")
public class Tenant extends MasterBaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "database_id", referencedColumnName = "id")
    private Database database;

}
