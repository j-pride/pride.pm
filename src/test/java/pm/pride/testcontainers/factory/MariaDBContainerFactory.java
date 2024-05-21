package pm.pride.testcontainers.factory;

import org.testcontainers.containers.MariaDBContainer;

import java.util.Properties;

public class MariaDBContainerFactory extends AbstractJDBCContainerFactory<MariaDBContainer> {

  public MariaDBContainerFactory(Properties prideConfig) {
    super(prideConfig, new MariaDBContainer(parseImage(prideConfig)));
  }

}
