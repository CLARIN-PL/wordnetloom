package pl.edu.pwr.wordnetloom.client.ui.relationtests;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.edu.pwr.wordnetloom.client.model.Links;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTestDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTypeDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.senserelationform.TestListItemViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class RelationTestsViewModel implements ViewModel {

    public static final String OPEN_RELATION_TEST_DIALOG = "open_test_dialog";
    private ObservableList<TestListItemViewModel> testList = FXCollections.observableArrayList();
    private ObjectProperty<TestListItemViewModel> selectedTestListItem = new SimpleObjectProperty<>();

    @InjectScope
    RelationTypeDialogScope dialogScope;

    @InjectScope
    RelationTestDialogScope relationTestDialogScope;

    @Inject
    RemoteService service;

    private Command addCommand;
    private Command editCommand;
    private Command deleteCommand;

    private RelationType relationType;

    public void initialize() {

        dialogScope.relationTypeToEditProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadTests(newValue);
            }
        });

        relationTestDialogScope.subscribe(RelationTestDialogScope.RELOAD_TESTS, (s, objects) -> {
            loadTests(relationType);
        });

        addCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                add();
            }
        });
        editCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                edit();
            }
        });
        deleteCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                delete();
            }
        });
    }

    private void loadTests(RelationType relationType) {
        this.relationType = relationType;
        testList.clear();
        if (relationType.getLinks() != null && relationType.getLinks().getTests() != null) {
            List<RelationTest> tests = service.getRelationTests(relationType.getLinks().getTests());
            testList.addAll(tests.stream()
                    .map(TestListItemViewModel::new)
                    .collect(Collectors.toList()));
        }
    }

    public void add() {
        RelationTest rt = new RelationTest();
        rt.setTest("<x#%> <y#%>");
        rt.setLinks(new Links());
        rt.getLinks().setTests(relationType.getLinks().getTests());
        relationTestDialogScope.setRelationTestToEdit(rt);
        publish(OPEN_RELATION_TEST_DIALOG);
        relationTestDialogScope.publish(RelationTestDialogScope.OK_BEFORE_COMMIT);
    }


    public void edit() {
        RelationTest rt = selectedTestListItem.get().getRelationTest();
        if(rt != null) {
            relationTestDialogScope.setRelationTestToEdit(rt);
            publish(OPEN_RELATION_TEST_DIALOG);
            relationTestDialogScope.publish(RelationTestDialogScope.OK_BEFORE_COMMIT);
        }
    }

    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? You will remove test.",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (selectedTestListItem.get() != null && selectedTestListItem.get().getRelationTest() != null) {
                service.delete(selectedTestListItem.get().getRelationTest().getLinks().getSelf());
                loadTests(relationType);
            }
        }
    }

    public ObservableList<TestListItemViewModel> getTestList() {
        return testList;
    }

    public void setTestList(ObservableList<TestListItemViewModel> testList) {
        this.testList = testList;
    }

    public TestListItemViewModel getSelectedTestListItem() {
        return selectedTestListItem.get();
    }

    public ObjectProperty<TestListItemViewModel> selectedTestListItemProperty() {
        return selectedTestListItem;
    }

    public void setSelectedTestListItem(TestListItemViewModel selectedTestListItem) {
        this.selectedTestListItem.set(selectedTestListItem);
    }

    public Command getAddCommand() {
        return addCommand;
    }

    public Command getEditCommand() {
        return editCommand;
    }

    public Command getDeleteCommand() {
        return deleteCommand;
    }
}

