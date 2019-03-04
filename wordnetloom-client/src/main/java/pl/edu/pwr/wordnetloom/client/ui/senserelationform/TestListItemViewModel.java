package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.model.Sense;

import java.util.Arrays;


public class TestListItemViewModel implements ViewModel {

    private RelationTest relationTest;
    private Boolean testPasses = true;
    private ObservableList<Node> children = FXCollections.observableArrayList();

    public TestListItemViewModel(RelationTest rt, Sense parent, Sense child) {
        this.relationTest = rt;
        parseTest(rt,parent,child);
    }

    public TestListItemViewModel(RelationTest rt) {
        this.relationTest = rt;
        children.add(new Text(rt.getTest()));
    }

    public RelationTest getRelationTest() {
        return relationTest;
    }

    public Boolean isTestPasses() {
        return testPasses;
    }

    private String parseTest(RelationTest rt, Sense parent, Sense child) {

        String test = rt.getTest();
        test = test.replace("<","[TAG]<");
        test = test.replace(">",">[TAG]");
        String[] tokens = test.split("\\[TAG\\]");

        Arrays.asList(tokens).forEach(t ->{
            if(isTag(t)){
                if(isXTag(t)){
                    String form = findForm(parent.getLemma(), getTag(t));
                    Text text = new Text(form);
                    text.setFill(Color.BLUE);
                    children.add(text);
                }
                else if(isYTag(t)){
                    String form = findForm(child.getLemma(), getTag(t));
                    Text text = new Text(form);
                    text.setFill(Color.BLUE);
                    children.add(text);
                }
            }else{
                children.add(new Text(t));
            }
        });

        if ((rt.getSenseAPos() != null && !rt.getSenseAPos().equals(parent.getPartOfSpeech()))
                || (rt.getSenseBPos() != null && !rt.getSenseBPos().equals(child.getPartOfSpeech()))) {

            Text text = new Text(" [POS Error]");
            text.setFill(Color.RED);
            children.add(text);
            testPasses = false;
        } else {
            Text text = new Text(" [POS OK]");
            text.setFill(Color.GREEN);
            children.add(text);
        }

        return "<html>" + test+ "</html>";
    }

    private String getTag(String tag){
        if(isXTag(tag)){
            tag = tag.replace("<x#", "");
            return tag.replace("%>", "");
        }
        if(isYTag(tag)){
            tag = tag.replace("<y#", "");
            return tag.replace("%>", "");
        }
        return tag;
    }

    private boolean isTag(String tag){
        return tag.startsWith("<") && tag.endsWith(">");
    }

    private boolean isXTag(String tag){
        return isTag(tag) && tag.startsWith("<x#");
    }

    private boolean isYTag(String tag){
        return isTag(tag) && tag.startsWith("<y#");
    }

    private static String findForm(String unit, String tag) {

        String suffix = "";
        if (unit.endsWith("się")) {
            unit = unit.substring(0, unit.length() - 4);
            suffix = " się";
        }

        String form = null;//RemoteService.wordFormServiceRemote.findFormByLemmaAndTag(unit, tag);

        if (form != null) {
            return form + suffix;
        }
        return unit + suffix;
    }

    public ObservableList<Node> getChildren() {
        return children;
    }
}

