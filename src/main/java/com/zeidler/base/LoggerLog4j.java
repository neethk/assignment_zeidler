package com.zeidler.base;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Paths;

public class LoggerLog4j {
    public static Logger logger = Logger.getLogger(LoggerLog4j.class);

    public Logger initiateLogger() {
        try {
            PropertyConfigurator.configure(Paths.get("").toAbsolutePath().toString() + "/src/main/resources/log4j.properties");
        } catch (Exception e) {
            e.printStackTrace();
            logger = (Logger) LogManager.getLogger("Main Test");
        }
        return logger;
    }
}
