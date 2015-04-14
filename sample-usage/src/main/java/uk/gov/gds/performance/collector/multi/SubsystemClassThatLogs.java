package uk.gov.gds.performance.collector.multi;

import com.equalexperts.logging.OpsLogger;
import uk.gov.gds.performance.collector.CollectorLogMessage;

public class SubsystemClassThatLogs {
    private final OpsLogger<OutputLogMessage> logger;

    public SubsystemClassThatLogs(OpsLogger<OutputLogMessage> logger) {
        this.logger = logger;
    }

    public void foo() {
        logger.log(OutputLogMessage.Success, 42);
    }

    public void bar() {
        RuntimeException e = new RuntimeException();
        logger.log(OutputLogMessage.UnknownError, e);
        throw e;
    }

    public void baz() {

    }
}
