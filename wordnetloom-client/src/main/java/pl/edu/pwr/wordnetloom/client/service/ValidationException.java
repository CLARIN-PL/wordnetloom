package pl.edu.pwr.wordnetloom.client.service;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private Map<String, String> errors;

    public ValidationException(Map<String, String> errors){
        super();
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
