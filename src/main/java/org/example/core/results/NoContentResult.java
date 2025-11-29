package org.example.core.results;

public class NoContentResult extends AbstractResult {

    /**
     * Default constructor to instantiate an empty result with no content
     */
    public NoContentResult() {
        super();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}