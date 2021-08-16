package pl.edu.pwr.wordnetloom.server.business.user.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserSettings {

    private Boolean lexiconMarker = true;
    private String chosenLexicons;
    private Boolean showToolTips = true;

    public List<Long> getSelectedLexicons(){
        return Arrays.stream(chosenLexicons.split(";"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public Boolean getLexiconMarker() {
        return lexiconMarker;
    }

    public void setLexiconMarker(Boolean lexiconMarker) {
        this.lexiconMarker = lexiconMarker;
    }

    public String getChosenLexicons() {
        return chosenLexicons;
    }

    public void setChosenLexicons(String chosenLexicons) {
        this.chosenLexicons = chosenLexicons;
    }

    public Boolean getShowToolTips() {
        return showToolTips;
    }

    public void setShowToolTips(Boolean showToolTips) {
        this.showToolTips = showToolTips;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "lexiconMarker=" + lexiconMarker +
                ", chosenLexicons='" + chosenLexicons + '\'' +
                ", showToolTips=" + showToolTips +
                '}';
    }
}
