package test.coreer.db.migration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

/**
 * Created by aieremenko on 12/9/16.
 */
public class FlywayDbEvolutionary implements DatabaseEvolutionary {

    private final String[] locations;

    public FlywayDbEvolutionary() {
        this("classpath:db/migration/tenant");
    }

    public FlywayDbEvolutionary(String... locations) {
        this.locations = locations;

    }

    @Override
    public void applyEvolutions(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setLocations(locations);
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
}
