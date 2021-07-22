package dev.elvislee.revature.project.util;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;

public class TestLogging {

    public static void main(String[] args) {
        Logger logger = Log4j.getLogger();

        try {
            throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            logger.error("File not found");
        }
    }
}
