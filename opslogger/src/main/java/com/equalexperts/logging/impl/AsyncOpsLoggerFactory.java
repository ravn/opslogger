package com.equalexperts.logging.impl;

import com.equalexperts.logging.ContextSupplier;
import com.equalexperts.logging.LogMessage;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;

public class AsyncOpsLoggerFactory {

    private AsyncExecutor asyncExecutor = new AsyncExecutor(Executors.defaultThreadFactory());

    public <T extends Enum<T> & LogMessage> AsyncOpsLogger<T> build(InfrastructureFactory infrastructureFactory) throws IOException {
        ContextSupplier contextSupplier = infrastructureFactory.configureContextSupplier();
        Consumer<Throwable> errorHandler = infrastructureFactory.configureErrorHandler();
        Destination<T> destination = infrastructureFactory.configureDestination();
        return new AsyncOpsLogger<>(Clock.systemUTC(), contextSupplier, destination, errorHandler, new LinkedTransferQueue<>(), asyncExecutor);
    }

    void setAsyncExecutor(AsyncExecutor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }
}
