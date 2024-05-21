package pm.pride.testcontainers.factory;

import org.testcontainers.containers.OracleContainer;

import java.util.Properties;

public class OracleContainerFactory extends AbstractJDBCContainerFactory<OracleContainer> {

  public OracleContainerFactory(Properties prideConfig) {
    super(prideConfig, new OracleContainer(parseImage(prideConfig)));
  }

}
