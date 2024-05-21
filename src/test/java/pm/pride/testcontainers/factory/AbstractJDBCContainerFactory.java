package pm.pride.testcontainers.factory;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;
import pm.pride.ResourceAccessor.Config;
import pm.pride.basic.DBConfigurator.TestConfig;

import java.util.Properties;

public class AbstractJDBCContainerFactory<C extends JdbcDatabaseContainer> {
  C container;
  Properties prideConfig;

  public AbstractJDBCContainerFactory(Properties prideConfig, C container) {
    this.prideConfig = prideConfig;
    this.container = container;
    synchronizeContainerAndPrideConfig();
  }

  private void synchronizeContainerAndPrideConfig() {
    String predefinedDBUsername = prideConfig.getProperty(Config.USER);
    if (predefinedDBUsername != null) {
      container.withUsername(predefinedDBUsername);
    }
    String predefinedDBPassword = prideConfig.getProperty(Config.PASSWORD);
    if (predefinedDBPassword != null) {
      container.withPassword(predefinedDBPassword);
    }
  }

  public void start() {
    System.err.println("Starting test container " + prideConfig.getProperty(TestConfig.IMAGE) + " ...");
    container.start();
    prideConfig.setProperty(Config.USER, container.getUsername());
    prideConfig.setProperty(Config.PASSWORD, container.getPassword());
    prideConfig.setProperty(Config.DB, container.getJdbcUrl());
  }

  public Properties getDBConfig() {
    return prideConfig;
  }

  protected static DockerImageName parseImage(Properties prideConfig) {
    String imageName = prideConfig.getProperty(TestConfig.IMAGE);
    return DockerImageName.parse(imageName);
  }
}
