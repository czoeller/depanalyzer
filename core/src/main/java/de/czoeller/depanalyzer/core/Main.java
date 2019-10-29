package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.core.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        new Core().analyzePOM(Config.INSTANCE.getTargetPomFile());
    }

}
