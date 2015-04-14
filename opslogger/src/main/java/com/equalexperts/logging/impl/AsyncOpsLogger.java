package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;
import com.equalexperts.logging.OpsLogger;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Asynchronous OpsLogger which puts the record to be logged in a transferQueue and
 * returns immediately which allows for better performance at the expense of not
 * necessarily having everything logged if the JVM shuts down unexpectedly.
 * A background thread is responsible for emptying the transferQueue.
 */

public class AsyncOpsLogger implements OpsLoggerBase {

    static final int MAX_BATCH_SIZE = 100;
    private final Future<?> processingThread;
    private final LinkedTransferQueue<Optional<LogicalLogRecord>> transferQueue;
    private final Clock clock;
    private final Supplier<Map<String, String>> correlationIdSupplier;
    private final Destination destination;
    private final Consumer<Throwable> errorHandler;

    public AsyncOpsLogger(Clock clock, Supplier<Map<String, String>> correlationIdSupplier, Destination destination, Consumer<Throwable> errorHandler, LinkedTransferQueue<Optional<LogicalLogRecord>> transferQueue, AsyncExecutor executor) {
        this.clock = clock;
        this.correlationIdSupplier = correlationIdSupplier;
        this.destination = destination;
        this.errorHandler = errorHandler;
        this.transferQueue = transferQueue;
        processingThread = executor.execute(this::process);
    }

    @Override
    public void log(LogMessage message, Object... details) {
        try {
            LogicalLogRecord record = new LogicalLogRecord(clock.instant(), correlationIdSupplier.get(), message, Optional.empty(), details);
            transferQueue.put(Optional.of(record));
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }

    @Override
    public void log(LogMessage message, Throwable cause, Object... details) {
        try {
            LogicalLogRecord record = new LogicalLogRecord(clock.instant(), correlationIdSupplier.get(), message, Optional.of(cause), details);
            transferQueue.put(Optional.of(record));
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }

    @Override
    public void close() throws Exception {
        try {
            transferQueue.put(Optional.empty()); //an empty optional is the shutdown signal
            processingThread.get();
        } finally {
            destination.close();
        }
    }

    private void process() {
        /*
            An empty optional on the queue is the shutdown signal
         */
        boolean run = true;
        do {
            try {
                List<Optional<LogicalLogRecord>> messages = waitForNextBatch();
                List<LogicalLogRecord> logRecords = messages.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());

                if (logRecords.size() < messages.size()) {
                    run = false; //shutdown signal detected
                }
                processBatch(logRecords);
            } catch (Throwable t) {
                errorHandler.accept(t);
            }
        } while (run);
    }

    private void processBatch(List<LogicalLogRecord> batch) throws Exception {
        if (batch.isEmpty()) {
            return;
        }
        destination.beginBatch();
        for (LogicalLogRecord record : batch) {
            try {
                destination.publish(record);
            } catch (Throwable t) {
                errorHandler.accept(t);
            }
        }
        destination.endBatch();
    }

    private List<Optional<LogicalLogRecord>> waitForNextBatch() throws InterruptedException {
        List<Optional<LogicalLogRecord>> result = new ArrayList<>();
        result.add(transferQueue.take()); //a blocking operation
        transferQueue.drainTo(result, MAX_BATCH_SIZE - 1);
        return result;
    }

    public Clock getClock() {
        return clock;
    }

    public Destination getDestination() {
        return destination;
    }

    public Supplier<Map<String, String>> getCorrelationIdSupplier() {
        return correlationIdSupplier;
    }

    public Consumer<Throwable> getErrorHandler() {
        return errorHandler;
    }

    public LinkedTransferQueue<Optional<LogicalLogRecord>> getTransferQueue() {
        return transferQueue;
    }
}
