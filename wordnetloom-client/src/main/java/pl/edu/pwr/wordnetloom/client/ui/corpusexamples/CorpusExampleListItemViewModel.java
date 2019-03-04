package pl.edu.pwr.wordnetloom.client.ui.corpusexamples;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import java.util.Arrays;

public class CorpusExampleListItemViewModel implements ViewModel {

    private String example;
    private ObservableList<Node> children = FXCollections.observableArrayList();

    public CorpusExampleListItemViewModel(String example) {
        this.example = example;
        String[] text = example.split("_");
        Arrays.asList(text).forEach(t -> {
            Text txt = new Text(t);
            if (t.contains(" ")) {
                txt.setStyle("-fx-font: normal 12 System;");
            } else {
                txt.setStyle("-fx-font: normal bold 12 System; ");
            }
            children.add(txt);
        });
    }

    public String getCorpusExampleListItem() {
        return example;
    }

    public ObservableList<Node> getChildren() {
        return children;
    }
}

