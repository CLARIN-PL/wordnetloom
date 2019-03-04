package pl.edu.pwr.wordnetloom.client.ui.senserelations;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeItemObject {

    private StringProperty label = new SimpleStringProperty();

    private Object item;

    private TreeItemType type;

    public TreeItemObject(String label, Object item, TreeItemType type) {
        this.label.set(label);
        this.item = item;
        this.type = type;
    }

    @Override
    public String toString() {
        return label.get();
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public Object getItem() {
        return item;
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public TreeItemType getType() {
        return type;
    }

    public void setType(TreeItemType type) {
        this.type = type;
    }
}
