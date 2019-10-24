package de.czoeller.depanalyzer.analyzer.spotbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import org.apache.bcel.classfile.JavaClass;

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
        System.out.println("Finished SpotBugs analysis");
        System.out.println("Results: ");
        if(bugs.getCollection().isEmpty()) {
            System.out.println("No bugs found");
        } else {
            System.out.println(String.format("%d bugs found", bugs.getCollection().size()));
            for (BugInstance bug : bugs) {
                System.out.println(String.format("message: %s ", bug.getMessage()));
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
