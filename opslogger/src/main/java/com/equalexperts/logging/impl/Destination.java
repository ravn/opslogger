package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;

public interface Destination extends AutoCloseable {
    void beginBatch() throws Exception;

    void publish(LogicalLogRecord record) throws Exception;

    void endBatch() throws Exception;
}
