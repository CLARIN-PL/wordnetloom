package pl.edu.pwr.wordnetloom.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.language.bm.Languages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Language {
    Polski("pl", new Locale("pl")),
    English("en", Locale.ENGLISH);

    private final static ObservableList<Language> languages = FXCollections.observableArrayList();

    private final String abbreviation;
    private final Locale locale;

    static {
        languages.addAll(Language.values());
    }

    Language(String abbreviation, Locale locale) {
        this.abbreviation = abbreviation;
        this.locale = locale;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName(){ return  name();}

    public Locale getLocale() { return  locale;}

    public static Language get(String abbreviation) {
        return languages
                .stream()
                .filter(l -> l.abbreviation.equals(abbreviation))
                .findFirst().orElse(Polski);
    }

    public static ObservableList<Language> getLanguages() { return  languages; }
}
