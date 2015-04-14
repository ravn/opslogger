package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;
import com.equalexperts.logging.OpsLogger;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** OpsLogger which writes each entry directly to the Destination */

public class BasicOpsLogger implements OpsLoggerBase {
    private final Clock clock;
    private final Consumer<Throwable> errorHandler;
    private final Destination destination;
    private final Lock lock;
    private final Supplier<Map<String,String>> correlationIdSupplier;

    public BasicOpsLogger(Clock clock, Supplier<Map<String, String>> correlationIdSupplier, Destination destination, Lock lock, Consumer<Throwable> errorHandler) {
        this.clock = clock;
        this.correlationIdSupplier = correlationIdSupplier;
        this.destination = destination;
        this.lock = lock;
        this.errorHandler = errorHandler;
    }

    @Override
    public void close() throws Exception {
        destination.close();
    }

    @Override
    public void log(LogMessage message, Object... details) {
        try {
            LogicalLogRecord record = constructLogRecord(message, Optional.empty(), details);
            publish(record);
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }

    @Override
    public void log(LogMessage message, Throwable cause, Object... details) {
        try {
            LogicalLogRecord record = constructLogRecord(message, Optional.of(cause), details);
            publish(record);
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }

    private LogicalLogRecord constructLogRecord(LogMessage message, Optional<Throwable> o, Object... details) {
        return new LogicalLogRecord(clock.instant(), correlationIdSupplier.get(), message, o, details);
    }

    private void publish(LogicalLogRecord record) throws Exception {
        lock.lock();
        try {
            destination.beginBatch();
            try {
                destination.publish(record);
            } finally {
                destination.endBatch();
            }
        } finally {
            lock.unlock();
        }
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

    public Lock getLock() {
        return lock;
    }

    public Consumer<Throwable> getErrorHandler() { return errorHandler; }
}