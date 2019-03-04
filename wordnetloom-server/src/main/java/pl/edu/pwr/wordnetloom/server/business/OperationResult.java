package pl.edu.pwr.wordnetloom.server.business;

import java.util.HashMap;
import java.util.Map;

public class OperationResult<T> {

    private T entity;
    private Map<String, String> errors = new HashMap<>();

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String filed, String msg){
        errors.put(filed, msg);
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }
}
