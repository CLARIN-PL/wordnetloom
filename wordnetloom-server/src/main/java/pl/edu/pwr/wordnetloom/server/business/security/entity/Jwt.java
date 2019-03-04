package pl.edu.pwr.wordnetloom.server.business.security.entity;

public class Jwt {

    private String token;

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