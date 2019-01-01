package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor;
import pm.pride.ResourceAccessorJSE;
import pm.pride.ResourceAccessor.Config;

public class ResourceAccessorExampleConfig {
	public static final String EXAMPLES_CONFIG_FILE_NAME = "config/pride.examples.config.properties";
	
	static ResourceAccessorJSE accessor;
	
	public static ResourceAccessor initPriDE() throws Exception {
		if (accessor == null) {
			Properties initProperties = assembleConfigFromFileAndSystemProperties();
			accessor = new ResourceAccessorJSE(initProperties);
			DatabaseFactory.setResourceAccessor(accessor);
			if (initProperties.containsKey(Config.DB)) {
				DatabaseFactory.setDatabaseName(initProperties.getProperty(Config.DB));
			}
		}
		return accessor;
	}

	private static Properties assembleConfigFromFileAndSystemProperties() {
		Properties initProperties = new Properties();
		
		try {
			InputStream configFile = new FileInputStream(EXAMPLES_CONFIG_FILE_NAME);
			initProperties.load(configFile);
		}
		catch(FileNotFoundException fnfx) {
			System.err.println("Config file " + EXAMPLES_CONFIG_FILE_NAME + " not found, going ahead with system properties only");
		}
		catch(IOException iox) {
			System.err.println("Error reading config file " + EXAMPLES_CONFIG_FILE_NAME + ": " + iox.getMessage() + ", going ahead with system properties only");
		}
		
		Properties systemProperties = System.getProperties();
		for (Object key: systemProperties.keySet()) {
			String keyName = key.toString();
			if (keyName.startsWith(Config.PREFIX)) {
				initProperties.put(key, systemProperties.get(key));
			}
		}
		
		return initProperties;
	}
}
