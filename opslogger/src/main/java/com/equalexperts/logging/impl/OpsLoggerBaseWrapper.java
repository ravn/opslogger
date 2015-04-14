package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;
import com.equalexperts.logging.OpsLogger;

/**
 * Wrap a OpsLoggerBase (which has no idea about enums) so the OpsLogger interface (which do) is exposed.
 * @param <T>
 */
public class OpsLoggerBaseWrapper<T extends Enum<T> & LogMessage> implements OpsLogger<T> {
    public OpsLoggerBase getOpsLoggerBase() {
        return opsLoggerBase;
    }

    private final OpsLoggerBase opsLoggerBase;

    public OpsLoggerBaseWrapper(OpsLoggerBase opsLoggerBase) {
        this.opsLoggerBase = opsLoggerBase;
    }

    @Override
    public void log(T message, Object... details) {
        opsLoggerBase.log(message, details);
    }

    @Override
    public void log(T message, Throwable cause, Object... details) {
        opsLoggerBase.log(message, cause, details);
    }

    @Override
    public void close() throws Exception {
        opsLoggerBase.close();
    }


}
