package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YiddishProperty {

    private long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
    public String toString() {
        return "YiddishProperty{" +
                "id=" + id +
                ", variant_type=" + variantType +
                ", latinSpelling='" + latinSpelling + '\'' +
                ", yiddishSpelling='" + yiddishSpelling + '\'' +
                ", yivoSpelling='" + yivoSpelling + '\'' +
                ", meaning='" + meaning + '\'' +
                ", etymologicalRoot='" + etymologicalRoot + '\'' +
                ", comment='" + comment + '\'' +
                ", context='" + context + '\'' +
                ", etymology='" + etymology + '\'' +
                ", age=" + age +
                ", lexicalCharacteristic=" + lexicalCharacteristic +
                ", status=" + status +
                ", style=" + style +
                ", sources=" + sources +
                ", semanticFields=" + semanticFields +
                ", transcriptions=" + transcriptions +
                ", inflections=" + inflections +
                ", particles=" + particles +
                ", links=" + links +
                '}';
    }
}
