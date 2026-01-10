package org.example.core.results;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({"errorCode", "errorMessage", "duration", "hasError", "empty"})
public class NoContentResult extends AbstractResult {

    /**
     * Default constructor to instantiate an empty result with no content
     */
    public Long id;


    public NoContentResult() {
        super();
    }

    public NoContentResult(int errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

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