package com.equalexperts.logging.impl;

import com.equalexperts.logging.LogMessage;

import java.io.PrintStream;

/**
 * A Destination which formats LogicalLogRecords with the provided stackTraceProcessor and prints it to <code>output</code>.
 * Also knows that if output is System.out or System.err, it should not be closed when done.
 */

public class OutputStreamDestination implements Destination {
    private final PrintStream output;
    private final StackTraceProcessor stackTraceProcessor;

    public OutputStreamDestination(PrintStream output, StackTraceProcessor stackTraceProcessor) {
        this.output = output;
        this.stackTraceProcessor = stackTraceProcessor;
    }

    @Override
    public void beginBatch() throws Exception {

    }

    @Override
    public void publish(LogicalLogRecord record) throws Exception {
        output.println(record.format(stackTraceProcessor));
    }

    @Override
    public void endBatch() throws Exception {

    }

    @Override
    public void close() throws Exception {
        if (!streamIsSpecial()) {
            output.close();
        }
    }

    private boolean streamIsSpecial() {
        return (output == System.out) || (output == System.err);
    }

    public PrintStream getOutput() {
        return output;
    }

    public StackTraceProcessor getStackTraceProcessor() {
        return stackTraceProcessor;
    }
}
