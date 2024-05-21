package pm.pride.testcontainers.factory;

import org.testcontainers.containers.JdbcDatabaseContainer;
import pm.pride.ResourceAccessor.Config;
import pm.pride.basic.DBConfigurator.TestConfig;

import java.util.Properties;

/** Base class for all factory classes used to create and start relational
 * databases as temporary test containers. See https://testcontainers.com/ for
 * general information about the approach.
 * <p>
 * The test containers project provides pre-defined modules for all databases
 * supported by PriDE. As the container classes of all these modules inherit
 * from a base class {@link JdbcDatabaseContainer}, the container instantiation
 * and configuration becomes very simple. It is usually not even necessary to
 * become familiar with the details of the underlying database docker images.
 * For special issues for some of the databases see
 * https://testcontainers.com/modules/?category=relational-database
 */
public abstract class AbstractJDBCContainerFactory<C extends JdbcDatabaseContainer> extends AbstractContainerFactory<C> {
  public AbstractJDBCContainerFactory(Properties prideConfig, C container) {
    super(prideConfig, container);
    configureContainerUserAndPassword();
  }

  private void configureContainerUserAndPassword() {
    String predefinedDBUsername = prideConfig.getProperty(Config.USER);
    if (predefinedDBUsername != null) {
      container.withUsername(predefinedDBUsername);
    }
    String predefinedDBPassword = prideConfig.getProperty(Config.PASSWORD);
    if (predefinedDBPassword != null) {
      container.withPassword(predefinedDBPassword);
    }
  }

  protected void synchronizePrideConfigWithContainerConfig() {
    prideConfig.setProperty(Config.USER, container.getUsername());
    prideConfig.setProperty(Config.PASSWORD, container.getPassword());
    prideConfig.setProperty(Config.DB, container.getJdbcUrl());
  }

  public Properties getDBConfig() {
    return prideConfig;
  }

}
