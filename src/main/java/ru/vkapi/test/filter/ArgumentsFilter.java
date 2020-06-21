package ru.vkapi.test.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgumentsFilter {
    Logger logger = LoggerFactory.getLogger(ArgumentsFilter.class);
    private static final int DEFAULT_NUMBER_THREAD_POOL = 100;
    private static final String DEFAULT_FILE_NAME = "Result";
    private String[] consoleArgs;

    public ArgumentsFilter(String[] consoleArgs) {
        this.consoleArgs = consoleArgs;
    }

    public void checkSizeArgs() {
        if (consoleArgs.length == 1) {
            logger.warn("Неверное количество аргументов");
//            throw new RuntimeException();
        }
    }

    public boolean checkMultiThreadOption() {
        return consoleArgs[0].equalsIgnoreCase("-m");
    }

    public Integer getNumberThreadPool() {
        try {
            return Integer.parseInt(consoleArgs[1]);
        } catch (NumberFormatException e) {
            return DEFAULT_NUMBER_THREAD_POOL;
        }
    }

    public String getPathToFile() {
        return checkMultiThreadOption() && consoleArgs.length == 3 ?
                consoleArgs[2] : (consoleArgs.length == 2 ? consoleArgs[1] : DEFAULT_FILE_NAME);
    }


}
