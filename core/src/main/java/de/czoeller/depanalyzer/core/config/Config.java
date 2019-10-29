package de.czoeller.depanalyzer.core.config;


import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Properties;

public enum Config {
    INSTANCE;

    private final File targetPomFile;
    private final boolean hashChanged;

    Config() {
        String propertiesPath = "app.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(propertiesPath));

            long hash = FileUtils.sizeOf(new File(propertiesPath));
            long hashOld = getHash(appProps);
            hashChanged = hash != hashOld;
            targetPomFile = new File(getPropertySafely(appProps, "targetPomFile"));

            appProps.setProperty("hash", "" + hash);

            if(hashChanged)
                appProps.store(new FileOutputStream(propertiesPath), null);
        } catch (IOException e) {
            throw new RuntimeException("Could not load Properties. Make sure there is a app.properties file at '" + propertiesPath +"'", e);
        }
    }

    private long getHash(Properties properties) {
        final String property = properties.getProperty("hash");
        if (null == property) {
            return 0;
        }
        return Long.valueOf(property);
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

    public boolean hasChanged() {
        return hashChanged;
    }
}
