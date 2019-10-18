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
