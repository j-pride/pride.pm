package pm.pride.testcontainers.factory;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;
import pm.pride.basic.DBConfigurator.TestConfig;

import java.util.Properties;

/** This class has only been separated in case that PriDE has to support
 * a test container'ed database which has no appropriate module based on
 * {@link JdbcDatabaseContainer} available. In this case the factory can not
 * be derived from {@link AbstractJDBCContainerFactory}. See factory class
 * {@link MariaDBGenericContainerFactory} as an example. It can be used as
 * an alternative for {@link MariaDBContainerFactory} just to show the concept.
 */
public abstract class AbstractContainerFactory<C extends GenericContainer> {
  protected final Properties prideConfig;
  protected final C container;

  public AbstractContainerFactory(Properties prideConfig, C container) {
    this.prideConfig = prideConfig;
    this.container = container;
  }

  public Properties getDBConfig() {
    return prideConfig;
  }

  public void start() {
    System.err.println("Starting test container " + prideConfig.getProperty(TestConfig.IMAGE) + "...");
    container.start();
    synchronizePrideConfigWithContainerConfig();
  }

  protected static DockerImageName parseImage(Properties prideConfig) {
    String imageName = prideConfig.getProperty(TestConfig.IMAGE);
    return DockerImageName.parse(imageName);
  }

  abstract protected void synchronizePrideConfigWithContainerConfig();

  public void stop() {
    container.stop();
  }
}