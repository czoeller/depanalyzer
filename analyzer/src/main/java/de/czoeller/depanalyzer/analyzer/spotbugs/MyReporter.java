package de.czoeller.depanalyzer.analyzer.spotbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.JavaClass;

@Slf4j
public class MyReporter extends TextUIBugReporter {

    private final BugCollection bugs;
    private int i;

    MyReporter(BugCollection bugs) {
        this.bugs = bugs;
        setPriorityThreshold(Detector.NORMAL_PRIORITY);
    }

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        new Trigger().fun();
        BugPattern bugPattern = bugInstance.getBugPattern();
        i++;
        if (bugPattern.getCategory().equals("SECURITY")) {
            bugs.add(bugInstance);
        }

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
        // TODO Auto-generated method stub

    }
    public void observeClass(ClassDescriptor classDescriptor) {
        // TODO Auto-generated method stub
    }

    public BugCollection getBugCollection() {
        return bugs;
    }

}
