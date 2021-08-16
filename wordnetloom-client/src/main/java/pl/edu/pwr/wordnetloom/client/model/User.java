package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class User {

    @JsonIgnore
    private String username = "";

    @JsonIgnore
    private String password = "";

    @JsonIgnore
    private Language language;

    @JsonIgnore
    private String token;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("show_tooltips")
    private Boolean showTooltips;

    @JsonProperty("show_markers")
    private Boolean showMarkers;

    @JsonProperty("lexicons")
    private String lexicons;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getShowTooltips() {
        return showTooltips;
    }

    public void setShowTooltips(Boolean showTooltips) {
        this.showTooltips = showTooltips;
    }

    public Boolean getShowMarkers() {
        return showMarkers;
    }

    public void setShowMarkers(Boolean showMarkers) {
        this.showMarkers = showMarkers;
    }

    public String getLexicons() {
        return lexicons;
    }

    public List<Long> getLexiconsIds() {
        return Arrays.stream(lexicons.split(";"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public void setLexicons(String lexicons) {
        this.lexicons = lexicons;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", language=" + language +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", showTooltips=" + showTooltips +
                ", showMarkers=" + showMarkers +
                ", lexicons='" + lexicons + '\'' +
                '}';
    }
}
