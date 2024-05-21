package pm.pride.testcontainers.factory;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Properties;

public class PostgreSQLContainerFactory extends AbstractJDBCContainerFactory<PostgreSQLContainer> {

  public PostgreSQLContainerFactory(Properties prideConfig) {
    super(prideConfig, new PostgreSQLContainer(parseImage(prideConfig)));
  }

}
