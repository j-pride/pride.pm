package pm.pride.basic;

import pm.pride.ResourceAccessor.DBType;
import pm.pride.testcontainers.factory.AbstractContainerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** This class is responsible for providing a test database configuration for PriDE unit tests.
 * When using databases in test containers, it also makes sure that the appropriate container
 * is started before running any tests.
 * <p>
 * The class has the following strategy:
 * <ul>
 *   <li>If there is a system property pride.test.config.file defined refering to an existing file,
 *   it reads database configuration properties from that file</li>
 *   <li>If there is a system property pride.test.config.db defined, it tries to find a file
 *   config/[username].[db].test.config.properties or config/[db].test.config.properties</li>
 *   <li>This file may contain either a complete configuration or one of the properties
 *   pride.test.config.file or pride.test.config.db to address the configuration file indirectly
 *   the same way it can be expressed by the corresponding system properties above.</li>
 *   <li>The configuration may either refer to a locally installed database or a test container.
 *   A local database requires the properties pride.db (the DB URL), pride.user, and pride.password.
 *   A test container config requires pride.testcontainers.image and pride.testcontainers.factory
 *   instead. User, Password and URL are synchronized into the resulting application configuration
 *   after container start</li>
 *   <li>Providing pride.user and pride.password along with a test container config, will cause user
 *   and password to be used for container configuration. However, this is not supported by all
 *   database test containers and should not be used without a good reason.</li>
 * </ul>
 */
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

  private static AbstractContainerFactory testContainerFactory;

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
        testContainerFactory = (AbstractContainerFactory)
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
      String indirectConfigFileName = testConfig.getProperty(TestConfig.FILE);
      if (indirectConfigFileName != null) {
        configFileName = indirectConfigFileName;
      }
      else {
        String indirectConfigDBType = testConfig.getProperty(TestConfig.DB);
        if (indirectConfigDBType != null) {
          configFileName = findConfigFileFromDBType(indirectConfigDBType, currentUser);
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
