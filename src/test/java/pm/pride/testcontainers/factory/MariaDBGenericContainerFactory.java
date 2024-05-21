package pm.pride.testcontainers.factory;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import pm.pride.ResourceAccessor.Config;

import java.util.Properties;

/** This is an example for the creation of a test container, making use of the
 * {@link GenericContainer} class rather than {@link JdbcDatabaseContainer}.
 * Change the mariadb.test.config.properties file to use this factory class
 * if you like to see that it works. */
public class MariaDBGenericContainerFactory extends AbstractContainerFactory {
  private static final String DBUSER = "generictest";
  private static final String DBPASSWORD = "testpw";
  private static final String DB = "generictest";
  private static final int JDBCPORT = 3306;

  public MariaDBGenericContainerFactory(Properties prideConfig) {
    super(prideConfig, createContainer(prideConfig));
  }

  /** How do we know that the container needs to be configured like that?
   * It takes reading the documentation of the underlying docker image
   * rather than the much simpler documentation of a pre-defined test
   * container module. For this particular example see
   * https://hub.docker.com/_/mariadb */
  private static GenericContainer createContainer(Properties prideConfig) {
    return new GenericContainer<>(parseImage(prideConfig))
      .withEnv("MARIADB_ROOT_PASSWORD", DBPASSWORD)
      .withEnv("MARIADB_USER", DBUSER)
			.withEnv("MARIADB_PASSWORD", DBPASSWORD)
			.withEnv("MARIADB_DATABASE", DB)
			.withExposedPorts(JDBCPORT);
  }

  @Override
  protected void synchronizePrideConfigWithContainerConfig() {
    prideConfig.setProperty(Config.USER, DBUSER);
    prideConfig.setProperty(Config.PASSWORD, DBPASSWORD);
		int exposedPort = container.getMappedPort(JDBCPORT);
    prideConfig.setProperty(Config.DB, "jdbc:mariadb://localhost:" + exposedPort + "/" + DB);
  }
}
