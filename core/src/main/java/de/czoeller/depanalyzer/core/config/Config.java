/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.czoeller.depanalyzer.core.config;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public enum Config {
    INSTANCE;

    private final File targetPomFile;
    private final boolean hashChanged;

    Config() {
        String propertiesPath = "app.properties";

        Properties appProps = new CleanProperties();
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
