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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 * This variant of Properties skips writing the date as first line to the properties file.
 */
public class CleanProperties extends Properties {

    private static final long serialVersionUID = -152923400347590934L;

    private static class StripFirstLineStream extends FilterOutputStream {

        private boolean firstLineSeen = false;

        public StripFirstLineStream(final OutputStream out) {
            super(out);
        }

        @Override
        public void write(final int b) throws IOException {
            if (firstLineSeen) {
                super.write(b);
            } else if (b == '\n') {
                firstLineSeen = true;
            }
        }

    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<>(super.keySet()));
    }

    @Override
    public void store(final OutputStream out, final String comments) throws IOException {
        super.store(new StripFirstLineStream(out), null);
    }
}