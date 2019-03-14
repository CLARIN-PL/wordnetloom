package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.UUID;

public class YiddishProperty implements Cloneable{

    private String tabId = UUID.randomUUID().toString();

    private Long id;

    @JsonProperty("variant_type")
    private VariantType variantType;

    @JsonProperty("latin_spelling")
    private String latinSpelling;

    @JsonProperty("yiddish_spelling")
    private String yiddishSpelling;

    @JsonProperty("yivo_spelling")
    private String yivoSpelling;

    private String meaning;

    @JsonProperty("etymological_root")
    private String etymologicalRoot;

    private String comment;

    private String context;

    private String etymology;

    private Dictionary age;

    @JsonProperty("grammatical_gender")
    private Dictionary grammaticalGender;

    @JsonProperty("lexical_characteristic")
    private Dictionary lexicalCharacteristic;

    private Dictionary status;

    private Dictionary style;

    private List<Dictionary> sources;

    @JsonProperty("semantic_fields")
    private List<YiddishSemanticField> semanticFields;

    private List<YiddishTranscription> transcriptions;

    private List<YiddishInflection> inflections;

    private List<YiddishParticle> particles;

    @JsonProperty("_links")
    private Links links;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(VariantType variantType) {
        this.variantType = variantType;
    }

    public String getLatinSpelling() {
        return latinSpelling;
    }

    public void setLatinSpelling(String latinSpelling) {
        this.latinSpelling = latinSpelling;
    }

    public String getYiddishSpelling() {
        return yiddishSpelling;
    }

    public void setYiddishSpelling(String yiddishSpelling) {
        this.yiddishSpelling = yiddishSpelling;
    }

    public String getYivoSpelling() {
        return yivoSpelling;
    }

    public void setYivoSpelling(String yivoSpelling) {
        this.yivoSpelling = yivoSpelling;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Dictionary getGrammaticalGender() {
        return grammaticalGender;
    }

    public void setGrammaticalGender(Dictionary grammaticalGender) {
        this.grammaticalGender = grammaticalGender;
    }

    public String getEtymologicalRoot() {
        return etymologicalRoot;
    }

    public void setEtymologicalRoot(String etymologicalRoot) {
        this.etymologicalRoot = etymologicalRoot;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getEtymology() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology = etymology;
    }

    public Dictionary getAge() {
        return age;
    }

    public void setAge(Dictionary age) {
        this.age = age;
    }

    public Dictionary getLexicalCharacteristic() {
        return lexicalCharacteristic;
    }

    public void setLexicalCharacteristic(Dictionary lexicalCharacteristic) {
        this.lexicalCharacteristic = lexicalCharacteristic;
    }

    public Dictionary getStatus() {
        return status;
    }

    public void setStatus(Dictionary status) {
        this.status = status;
    }

    public Dictionary getStyle() {
        return style;
    }

    public void setStyle(Dictionary style) {
        this.style = style;
    }

    public List<Dictionary> getSources() {
        return sources;
    }

    public void setSources(List<Dictionary> sources) {
        this.sources = sources;
    }

    public List<YiddishSemanticField> getSemanticFields() {
        return semanticFields;
    }

    public void setSemanticFields(List<YiddishSemanticField> semanticFields) {
        this.semanticFields = semanticFields;
    }

    public List<YiddishTranscription> getTranscriptions() {
        return transcriptions;
    }

    public void setTranscriptions(List<YiddishTranscription> transcriptions) {
        this.transcriptions = transcriptions;
    }

    public List<YiddishInflection> getInflections() {
        return inflections;
    }

    public void setInflections(List<YiddishInflection> inflections) {
        this.inflections = inflections;
    }

    public List<YiddishParticle> getParticles() {
        return particles;
    }

    public void setParticles(List<YiddishParticle> particles) {
        this.particles = particles;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YiddishProperty)) return false;

        YiddishProperty that = (YiddishProperty) o;

        if (tabId != null ? !tabId.equals(that.tabId) : that.tabId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (variantType != that.variantType) return false;
        if (latinSpelling != null ? !latinSpelling.equals(that.latinSpelling) : that.latinSpelling != null)
            return false;
        if (yiddishSpelling != null ? !yiddishSpelling.equals(that.yiddishSpelling) : that.yiddishSpelling != null)
            return false;
        if (yivoSpelling != null ? !yivoSpelling.equals(that.yivoSpelling) : that.yivoSpelling != null) return false;
        if (meaning != null ? !meaning.equals(that.meaning) : that.meaning != null) return false;
        if (etymologicalRoot != null ? !etymologicalRoot.equals(that.etymologicalRoot) : that.etymologicalRoot != null)
            return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        if (etymology != null ? !etymology.equals(that.etymology) : that.etymology != null) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (grammaticalGender != null ? !grammaticalGender.equals(that.grammaticalGender) : that.grammaticalGender != null)
            return false;
        if (lexicalCharacteristic != null ? !lexicalCharacteristic.equals(that.lexicalCharacteristic) : that.lexicalCharacteristic != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (style != null ? !style.equals(that.style) : that.style != null) return false;
        if (sources != null ? !sources.equals(that.sources) : that.sources != null) return false;
        if (semanticFields != null ? !semanticFields.equals(that.semanticFields) : that.semanticFields != null)
            return false;
        if (transcriptions != null ? !transcriptions.equals(that.transcriptions) : that.transcriptions != null)
            return false;
        if (inflections != null ? !inflections.equals(that.inflections) : that.inflections != null) return false;
        if (particles != null ? !particles.equals(that.particles) : that.particles != null) return false;
        return links != null ? links.equals(that.links) : that.links == null;
    }

    @Override
    public int hashCode() {
        int result = tabId != null ? tabId.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (variantType != null ? variantType.hashCode() : 0);
        result = 31 * result + (latinSpelling != null ? latinSpelling.hashCode() : 0);
        result = 31 * result + (yiddishSpelling != null ? yiddishSpelling.hashCode() : 0);
        result = 31 * result + (yivoSpelling != null ? yivoSpelling.hashCode() : 0);
        result = 31 * result + (meaning != null ? meaning.hashCode() : 0);
        result = 31 * result + (etymologicalRoot != null ? etymologicalRoot.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (etymology != null ? etymology.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (grammaticalGender != null ? grammaticalGender.hashCode() : 0);
        result = 31 * result + (lexicalCharacteristic != null ? lexicalCharacteristic.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (style != null ? style.hashCode() : 0);
        result = 31 * result + (sources != null ? sources.hashCode() : 0);
        result = 31 * result + (semanticFields != null ? semanticFields.hashCode() : 0);
        result = 31 * result + (transcriptions != null ? transcriptions.hashCode() : 0);
        result = 31 * result + (inflections != null ? inflections.hashCode() : 0);
        result = 31 * result + (particles != null ? particles.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "YiddishProperty{" +
                "tabId='" + tabId + '\'' +
                ", id=" + id +
                ", variantType=" + variantType +
                ", latinSpelling='" + latinSpelling + '\'' +
                ", yiddishSpelling='" + yiddishSpelling + '\'' +
                ", yivoSpelling='" + yivoSpelling + '\'' +
                ", meaning='" + meaning + '\'' +
                ", etymologicalRoot='" + etymologicalRoot + '\'' +
                ", etymology='" + etymology + '\'' +
                ", age=" + age +
                ", grammaticalGender=" + grammaticalGender +
                ", lexicalCharacteristic=" + lexicalCharacteristic +
                ", status=" + status +
                ", style=" + style +
                ", sources=" + sources +
                ", semanticFields=" + semanticFields +
                ", transcriptions=" + transcriptions +
                ", inflections=" + inflections +
                ", particles=" + particles +
                '}';
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
