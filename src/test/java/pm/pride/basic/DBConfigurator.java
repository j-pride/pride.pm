package pm.pride.basic;

import org.testcontainers.containers.GenericContainer;
import pm.pride.ResourceAccessor.DBType;
import pm.pride.testcontainers.factory.AbstractJDBCContainerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBConfigurator {

  public interface TestConfig {
    String DB = "pride.test.config.db";
    String FILE = "pride.test.config.file";
    String IMAGE = "pride.testcontainers.image";
    String FACTORY = "pride.testcontainers.factory";
    String CONFIG_DIR = "config/";
    String FILE_SUFFIX = ".test.config.properties";
    String DEFAULT_TEST_CONFIG_FILE = CONFIG_DIR + DBType.HSQL + FILE_SUFFIX;
  }

  private static AbstractJDBCContainerFactory testContainerFactory;

  private static void startDB() {
    // Beispiel für die Verwendung von GenericContainer statt des spezialisierten MariaDBContainer
//		if (container == null) {
//			container = new GenericContainer<>(DockerImageName.parse("mariadb:latest"))
//				//.withEnv("MARIADB_ROOT_PASSWORD", "admin")
//				.withEnv("MARIADB_USER", "test")
//				.withEnv("MARIADB_PASSWORD", "test")
//				.withEnv("MARIADB_DATABASE", "test")
//				.withExposedPorts(3306);
//			container.start();
//		}
//		int exposedPort = container.getMappedPort(3306);
//		return "jdbc:mariadb://localhost:" + exposedPort + "/test";
  }

  static Properties determineDatabaseTestConfiguration() throws IOException, ReflectiveOperationException {
    String configFileName = System.getProperty(TestConfig.FILE);
    if (!isValidConfigFile(configFileName)) {
      configFileName = findConfigFileIndirectly();
    }
    if (!isValidConfigFile(configFileName)) {
      System.err.println("Can't determine database configuration - tried to read from file: " + configFileName);
      System.err.println("Loading default in memory configuration: " + TestConfig.DEFAULT_TEST_CONFIG_FILE);
      configFileName = TestConfig.DEFAULT_TEST_CONFIG_FILE;
    }
    Properties testConfig = readConfigFile(configFileName);
    testConfig = startTestContainer(testConfig);
    return testConfig;
  }

  private static Properties startTestContainer(Properties testConfig) throws ReflectiveOperationException {
    String containerImage = testConfig.getProperty(TestConfig.IMAGE);
    if (containerImage != null) {
      if (testContainerFactory == null) {
        String containerFactoryClassName = testConfig.getProperty(TestConfig.FACTORY);
        Class<?> containerFactoryClass = Class.forName(containerFactoryClassName);
        testContainerFactory = (AbstractJDBCContainerFactory)
          containerFactoryClass.getConstructor(Properties.class).newInstance(testConfig);
        testContainerFactory.start();
      }
      return testContainerFactory.getDBConfig();
    }
    return testConfig;
  }

  private static Properties readConfigFile(String configFileName) throws IOException {
    Properties testConfig = new Properties();
    try (FileInputStream fis = new FileInputStream(configFileName)) {
      testConfig.load(fis);
    }
    return testConfig;
  }

  private static boolean isValidConfigFile(String configFileName) {
    return configFileName != null && new File(configFileName).exists();
  }

  private static String findConfigFileIndirectly() throws IOException {
    String configFileName;
    String currentUser = System.getProperty("user.name");
    String configDBType = System.getProperty(TestConfig.DB);
    if (configDBType != null) {
      configFileName = findConfigFileFromDBType(configDBType, currentUser);
    }
    else {
      configFileName = TestConfig.CONFIG_DIR + currentUser + TestConfig.FILE_SUFFIX;
      Properties testConfig = readConfigFile(configFileName);
      configFileName = testConfig.getProperty(TestConfig.FILE);
      if (configFileName == null) {
        configDBType = testConfig.getProperty(TestConfig.DB);
        if (configDBType != null) {
          configFileName = findConfigFileFromDBType(configDBType, currentUser);
        }
      }
    }
    return configFileName;
  }

  private static String findConfigFileFromDBType(String configDBType, String currentUser) {
    String configFileName = TestConfig.CONFIG_DIR + currentUser + "." + configDBType + TestConfig.FILE_SUFFIX;
    if (!isValidConfigFile(configFileName)) {
      configFileName = TestConfig.CONFIG_DIR + configDBType + TestConfig.FILE_SUFFIX;
    }
    return configFileName;
  }
}
