package test.coreer.model.master;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created by aieremenko on 12/26/16.
 */
@Data
@Entity
@Table(name = "tenant_databases")
public class Database extends MasterBaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String host;
    private Integer port;
    @Column(name = "schema_name")
    private String schema;
    private String user;
    private String password;
}
