package cz.vsb.genetics.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class Util {
    public static void exitError(String message, Logger log) {
        if (StringUtils.isNoneBlank(message))
            log.error(message + "\n");

        System.exit(1);
    }
}
