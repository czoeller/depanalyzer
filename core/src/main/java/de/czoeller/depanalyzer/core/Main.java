package de.czoeller.depanalyzer.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {

    public static void main(String[] args) {
        new Core().analyzePOM(new File("C:\\Users\\noex_\\IdeaProjects\\MasterthesisAnalyse\\velocity-engine\\pom.xml"));
    }

}
