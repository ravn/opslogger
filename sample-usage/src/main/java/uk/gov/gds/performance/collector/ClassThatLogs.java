package uk.gov.gds.performance.collector;

import com.equalexperts.logging.OpsLogger;

public class ClassThatLogs {
    private final OpsLogger<CollectorLogMessages> logger;

    public ClassThatLogs(OpsLogger<CollectorLogMessages> logger) {
        this.logger = logger;
    }

    public void foo() {
        logger.log(CollectorLogMessages.SUCCESS, 42);
    }

    public void bar() {
        RuntimeException e = new RuntimeException();
        logger.log(CollectorLogMessages.UNKNOWN_ERROR, e);
        throw e;
    }

    public void baz() {

    }
}
