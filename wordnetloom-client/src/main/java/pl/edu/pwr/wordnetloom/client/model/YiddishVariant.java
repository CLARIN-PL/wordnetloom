package pl.edu.pwr.wordnetloom.client.model;

public class YiddishVariant {

    private long id;

    private String variant_type;

    private String latin;

    private String yiddish;

    private String yivo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVariant_type() {
        return variant_type;
    }

    public void setVariant_type(String variant_type) {
        this.variant_type = variant_type;
    }

    public String getLatin() {
        return latin;
    }

    public void setLatin(String latin) {
        this.latin = latin;
    }

    public String getYiddish() {
        return yiddish;
    }

    public void setYiddish(String yiddish) {
        this.yiddish = yiddish;
    }

    public String getYivo() {
        return yivo;
    }

    public void setYivo(String yivo) {
        this.yivo = yivo;
    }
}
