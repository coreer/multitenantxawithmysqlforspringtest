package test.coreer.db.migration;

import javax.sql.DataSource;

/**
 * Created by aieremenko on 12/9/16.
 */
public interface DatabaseEvolutionary {
    void applyEvolutions(DataSource dataSource);
}
