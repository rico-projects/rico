package dev.rico;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sample {

    private static Logger LOG = LogManager.getLogger(Sample.class);

    public static void main(String[] args) {
        LOG.error("Hi Error");
        LOG.info("Hi Info");
        LOG.trace("Hi Trace");
    }

}
