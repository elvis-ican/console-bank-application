package dev.elvislee.revature.project.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The Log4j class helps to provide a static method getLogger for other
 * classes to get a logger to log exception messages.
 */
public class Log4j {
    private static Logger logger = Logger.getLogger(Log4j.class);

    static {
        PropertyConfigurator.configure("c://lck/revature/rev_intellij_workspace/banking-application/" +
                "src/main/resources/log4j.properties");
    }

    public static Logger getLogger() {
        return logger;
    }

}
