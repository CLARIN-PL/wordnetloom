package pl.edu.pwr.wordnetloom.client.model;

public class Jwt {

    private String token;

    public Jwt() {
    }

    public Jwt(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}