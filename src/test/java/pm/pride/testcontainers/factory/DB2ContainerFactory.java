package pm.pride.testcontainers.factory;

import org.testcontainers.containers.Db2Container;

import java.util.Properties;

public class DB2ContainerFactory extends AbstractJDBCContainerFactory<Db2Container> {

  public DB2ContainerFactory(Properties prideConfig) {
    super(prideConfig, new Db2Container(parseImage(prideConfig)));
    container.acceptLicense();
  }

}
