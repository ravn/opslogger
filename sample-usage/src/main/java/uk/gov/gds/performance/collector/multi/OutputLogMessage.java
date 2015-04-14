package uk.gov.gds.performance.collector.multi;

import com.equalexperts.logging.LogMessage;

public enum OutputLogMessage implements LogMessage {
    Success("X-000000", "Successfully published %d records"),
    UnknownError("X-000001", "An unknown error occurred:"),
    CouldNotConnectToPerformancePlatform("X-000002", "Could not connect to the performance platform %s"),
    PerformancePlatformTestQueryFailed("X-000003", "Test query to the performance platform %s failed with response code %d"),
    NoResultsFoundForDateRange("X-000004", "No results found in the date range %s to %s"),
    InvalidConfigurationFile("X-000005", "Invalid configuration file format %s"),
    ConfigurationFileNotFound("X-000006", "Configuration file %s not found"),
    CouldNotConnectToDatabase("X-000007", "Could not connect to the database:"),
    AllConnectivityChecksPassed("X-000008", "All connectivity checks passed");

    //region LogMessage implementation
    private final String messageCode;
    private final String messagePattern;

    OutputLogMessage(String messageCode, String messagePattern) {
        this.messageCode = messageCode;
        this.messagePattern = messagePattern;
    }

    @Override
    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public String getMessagePattern() {
        return messagePattern;
    }
    //endregion
}
