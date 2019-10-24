package de.czoeller.depanalyzer.analyzer.spotbugs;

import java.beans.XMLDecoder;

public class Trigger {

    /**
     * Used to test detection of find-sec-bugs
     */
    public void fun() {
        new XMLDecoder(System.in);
    }
}
