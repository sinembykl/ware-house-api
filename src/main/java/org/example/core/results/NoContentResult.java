package org.example.core.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Updated NoContentResult to properly handle error states and JSON visibility.
 * The ignore list now allows errorCode and errorMessage for API transparency.
 */
@JsonIgnoreProperties({"duration", "empty"}) // Removed errorCode and errorMessage from ignore list
public class NoContentResult extends AbstractResult {

    public Long id;

    /**
     * Default constructor for successful operations with no content.
     */
    public NoContentResult() {
        super(); // hasError is false by default
    }

    /**
     * Error constructor that properly flips the hasError flag.
     * * @param errorCode    The HTTP-friendly error code (e.g., 400, 404)
     * @param errorMessage The description of what went wrong
     */
    public NoContentResult(int errorCode, String errorMessage) {
        super();
        // Use the parent method to set hasError = true
        this.setError(errorCode, errorMessage);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}