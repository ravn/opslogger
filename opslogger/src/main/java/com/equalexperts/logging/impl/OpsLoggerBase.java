package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;

/** OpsLogger is the interface loggers show to the application */

public interface OpsLoggerBase extends AutoCloseable {
    /** Log message using message.getMessagePattern as the format and details as the format arguments.
     * @param message enum to log.
     * @param details format string arguments to message.getMessagePattern()
     * */
    void log(LogMessage message, Object... details);

    /** Log message using message.getMessagePattern as the format and details as the format arguments, with
     * the processed cause added.
     * @param message enum to log.
     * @param details format string arguments to message.getMessagePattern()
     * @param cause stack trace to process and include in the log message.
     *
     * */
    void log(LogMessage message, Throwable cause, Object... details);
}