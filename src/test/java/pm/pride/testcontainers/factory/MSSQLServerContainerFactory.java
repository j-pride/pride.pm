package pm.pride.testcontainers.factory;

import org.testcontainers.containers.MSSQLServerContainer;

import java.util.Properties;

public class MSSQLServerContainerFactory extends AbstractJDBCContainerFactory<MSSQLServerContainer> {

  public MSSQLServerContainerFactory(Properties prideConfig) {
    super(prideConfig, new MSSQLServerContainer(parseImage(prideConfig)));
    container.acceptLicense();
  }

}
