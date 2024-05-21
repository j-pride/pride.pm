package pm.pride.testcontainers.factory;

import org.testcontainers.containers.MSSQLServerContainer;

import java.util.Properties;

public class MSSQLServerContainerFactory extends AbstractJDBCContainerFactory<MSSQLServerContainer> {

  public MSSQLServerContainerFactory(Properties prideConfig) {
    super(prideConfig, new MSSQLServerContainer(parseImage(prideConfig)));
    // The following line is required according to the test container documentation for SQL Server
    // See https://testcontainers.com/modules/mssql/
    container.acceptLicense();
  }

}
