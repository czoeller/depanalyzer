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


import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public enum Config {
    INSTANCE;

    public static final String PROPERTIES_FILE = "app.properties";
    private final File targetPomFile;
    private final boolean hashChanged;

    Config() {
        final File currentDir = new File(System.getProperty("user.dir"));
        final Properties appProps = new CleanProperties();
        try {
            appProps.load(new FileInputStream(new File(currentDir, PROPERTIES_FILE)));

            String hash = calculateHash(appProps);
            String hashOld = getHash(appProps);
            hashChanged = !hash.equals(hashOld);
            targetPomFile = new File(getPropertySafely(appProps, "targetPomFile"));

            if(hashChanged) {
                appProps.setProperty("hash", hash);
                appProps.store(new FileOutputStream(PROPERTIES_FILE), null);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load Properties. Make sure there is a '" + PROPERTIES_FILE +"' file at " + currentDir.getAbsolutePath(), e);
        }
    }

    String calculateHash(Properties appProps) {
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<Object, Object> objectEntry : appProps.entrySet()) {
            if(objectEntry.getKey().equals("hash")) continue;
            buffer.append(objectEntry.getValue());
        }
        return Hashing.sha256().hashUnencodedChars(buffer.toString()).toString();
    }

    private String getHash(Properties properties) {
        final String property = properties.getProperty("hash");
        if (null == property) {
            return "";
        }
        return property;
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
