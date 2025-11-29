package org.example.core.results;

/**
 * The AbstractResult class defines the required properties, that all kind of
 * results should possess in order to
 * give descriptive information about possible errors
 */
public abstract class AbstractResult {

    /**
     * Defines whether the result contains an error
     */
    protected boolean hasError;

    /**
     * Defines the error code to be returned with the result in case on an error
     */
    protected int errorCode;

    /**
     * Defines the error message to be returned with the result in case on an error
     */
    protected String errorMessage;

    /**
     * Defines the time needed {@link Long} to execute a CRUD operation in the
     * database <strong>in milliseconds</strong>
     */
    protected long databaseExecutionTimeInMs;

    /**
     * The default constructor, which sets the {@link AbstractResult#hasError} to
     * false by default
     */
    protected AbstractResult() {
        this.hasError = false;
    }

    /**
     * @return true if the result is empty and contains no information
     */
    public abstract boolean isEmpty();

    /**
     * Calculates the {@link AbstractResult#databaseExecutionTimeInMs} by
     * subtracting the time when the command was
     * executed from the time when the command was started
     */
    public final void setTimes(final long startTime, final long stopTime) {
        this.databaseExecutionTimeInMs = stopTime - startTime;
    }

    /**
     * @return the {@link AbstractResult#databaseExecutionTimeInMs}
     */
    public final long getDuration() {
        return this.databaseExecutionTimeInMs;
    }

    /**
     * @return true if the result contains an error
     */
    public final boolean hasError() {
        return this.hasError;
    }

    /**
     * Sets {@link AbstractResult#hasError} to true
     */
    public final void setError() {
        this.hasError = true;
    }

    /**
     * Sets {@link AbstractResult#hasError} to true and sets the
     * {@link AbstractResult#errorCode}
     * as well as {@link AbstractResult#errorMessage} to the given values, referring
     * that the result contains an
     * error
     *
     * @param errorCode    {@link Integer} the error code to be set
     * @param errorMessage {@link String} the error message, that should be returned
     *                     with the result
     */
    public final void setError(final int errorCode, final String errorMessage) {
        this.hasError = true;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode( )
    {
        return errorCode;
    }

    public String getErrorMessage( )
    {
        return errorMessage;
    }
}