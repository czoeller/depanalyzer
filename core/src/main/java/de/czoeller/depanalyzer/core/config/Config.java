package de.czoeller.depanalyzer.core.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public enum Config {
    INSTANCE;

    private final File targetPomFile;

    Config() {
        String propertiesPath = "app.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(propertiesPath));
            String targetPomFileStr = getPropertySafely(appProps, "targetPomFile");
            targetPomFile = new File(targetPomFileStr);
        } catch (IOException e) {
            throw new RuntimeException("Could not load Properties. Make sure there is a app.properties file at '" + propertiesPath +"'", e);
        }
    }

    private String getPropertySafely(Properties properties, String key) {
        final String property = properties.getProperty(key);
        if (null == properties) {
            throw new RuntimeException("Could not load property with key '" + key + "'.");
        }
        return property;
    }

    public File getTargetPomFile() {
        return targetPomFile;
    }
    
}
