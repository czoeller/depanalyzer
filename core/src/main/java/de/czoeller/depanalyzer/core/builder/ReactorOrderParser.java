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
package de.czoeller.depanalyzer.core.builder;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Scanner;

public class ReactorOrderParser {

    List<String> parseModulesInReactorOrder(String mavenLog) {
        final Scanner scanner = new Scanner(mavenLog);
        final List<String> modules = Lists.newArrayList();
        boolean nowModules = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // ] ends the debug level output followed by a msg
            final String[] lineParts = line.split("]");
            if(lineParts.length >= 2) {
                final String msg = line.split("]")[1].trim();
                if(line.contains("Reactor Build Order:")) {
                    nowModules = true;
                } else if(!msg.isEmpty() && nowModules) {
                    // first line after Reactor Build Order: is empty followed by the modules in order
                    modules.add(msg);
                } else if(msg.isEmpty() && !modules.isEmpty()) {
                    // done
                    break;
                }
                // process the line
            }
        }
        return modules;
    }

}
