package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public class Action {

    private String name;

    private String title;

    private String method;

    private String type;

    private URI href;

    @JsonProperty("query_param")
    private List<Field> queryParam;

    private List<Field> headers;

    private List<Field> fields;

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Field> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Field> headers) {
        this.headers = headers;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(List<Field> queryParam) {
        this.queryParam = queryParam;
    }
}
