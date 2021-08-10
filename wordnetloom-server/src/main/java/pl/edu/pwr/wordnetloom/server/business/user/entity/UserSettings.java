package pl.edu.pwr.wordnetloom.server.business.user.entity;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tbl_users_settings")
@Audited
public class UserSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private User user;

    @Column(name = "lexicon_marker")
    private Boolean lexiconMarker = true;

    @Column(name = "chosen_lexicons")
    private String chosenLexicons;

    @Column(name = "show_tool_tips")
    private Boolean showToolTips = true;

    public List<Long> getSelectedLexicons(){
        return Arrays.stream(chosenLexicons.split(";"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
