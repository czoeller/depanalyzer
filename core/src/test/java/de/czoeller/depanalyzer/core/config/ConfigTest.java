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

import com.google.common.collect.Maps;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @Mock
    private Properties propertiesMock;

    private Config config;

    @StaticResource(searchClasspath = true)
    private static final String propertiesFile = Config.PROPERTIES_FILE;

    @BeforeEach
    void setUp()  throws IOException, URISyntaxException{
        try(FileOutputStream fos = new FileOutputStream(new File(Config.PROPERTIES_FILE))) {
            Files.copy(Paths.get(getClass().getClassLoader()
                                       .getResource(propertiesFile)
                                       .toURI()), fos);
            this.config = Config.INSTANCE;
        }
    }

    @AfterEach
    void tearDown()  throws IOException {
        Files.delete(new File(Config.PROPERTIES_FILE).toPath());
    }

    @Nested
    @DisplayName("HashProperty")
    class HashProperty {
        @BeforeEach
        void setUp() {
            Set<Map.Entry<Object, Object>> properties = Sets.newLinkedHashSet(
                    Maps.immutableEntry("hash", "dedededed"),
                    Maps.immutableEntry("targetPomFile", "pom.xml")
            );
            when(propertiesMock.entrySet()).thenReturn(properties);
        }

        @DisplayName("hashIsConstantForGivenEntries")
        @Test
        void hashIsConstantForGivenEntries() {
            assertThat(config.calculateHash(propertiesMock)).isEqualTo("119ad874dd2e7f283441d50200950bc45533257609f2f85ed581ac821e8d8ca1");
            assertThat(config.calculateHash(propertiesMock)).isEqualTo("119ad874dd2e7f283441d50200950bc45533257609f2f85ed581ac821e8d8ca1");
        }
    }
}