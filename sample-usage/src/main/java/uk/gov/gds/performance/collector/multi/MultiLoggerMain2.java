package uk.gov.gds.performance.collector.multi;

import com.equalexperts.logging.OpsLogger;
import com.equalexperts.logging.OpsLoggerFactory;
import uk.gov.gds.performance.collector.ClassThatLogs;
import uk.gov.gds.performance.collector.CollectorLogMessage;

import java.nio.file.Paths;

public class MultiLoggerMain2 {
    public static void main(String... args) throws Exception {
        OpsLoggerFactory loggerFactory = new OpsLoggerFactory()
                .setDestination(System.out)
                .setStackTraceStoragePath(Paths.get("/tmp/stacktraces"));

        ClassThatLogs cls = new ClassThatLogs(loggerFactory.build());
        cls.foo();
        SubsystemClassThatLogs sctl = new SubsystemClassThatLogs(loggerFactory.build());
        sctl.foo();
    }
}
