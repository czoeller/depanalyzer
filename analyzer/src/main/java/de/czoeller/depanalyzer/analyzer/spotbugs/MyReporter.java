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
package de.czoeller.depanalyzer.analyzer.spotbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.JavaClass;

@Slf4j
class MyReporter extends TextUIBugReporter {

    private static final String BUG_CATEGORY_SECURITY = "SECURITY";
    private final BugCollection bugs;

    MyReporter(BugCollection bugs) {
        this.bugs = bugs;
        setPriorityThreshold(Detector.NORMAL_PRIORITY);
    }

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        BugPattern bugPattern = bugInstance.getBugPattern();
        if (isSecurityBug(bugPattern)) {
            bugs.add(bugInstance);
        }
    }

    private boolean isSecurityBug(BugPattern bugPattern) {
        return bugPattern.getCategory().equals(BUG_CATEGORY_SECURITY);
    }

    public void finish() {
        log.info("Finished SpotBugs analysis");
        log.info("Results: ");
        if(bugs.getCollection().isEmpty()) {
            log.info("No bugs found");
        } else {
            log.info("{} bugs found", bugs.getCollection().size());
            for (BugInstance bug : bugs) {
                log.info("message: {}}", bug.getMessage());
            }
        }
    }

    public void observeClass(JavaClass javaClass) {
        // Nothing to do
    }
    public void observeClass(ClassDescriptor classDescriptor) {
        // Nothing to do
    }

    public BugCollection getBugCollection() {
        return bugs;
    }

}
