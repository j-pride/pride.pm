package pm.pride.testcontainers.factory;

import org.testcontainers.containers.MySQLContainer;

import java.util.Properties;

public class MySQLContainerFactory extends AbstractJDBCContainerFactory<MySQLContainer> {

  public MySQLContainerFactory(Properties prideConfig) {
    super(prideConfig, new MySQLContainer(parseImage(prideConfig)));
  }

}
