package pl.edu.pwr.wordnetloom.client.ui.synsetrelationform;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.Validator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.Synset;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetRelationScope;
import pl.edu.pwr.wordnetloom.client.ui.senserelationform.TestListItemViewModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SynsetRelationFormViewModel implements ViewModel {

    @Inject
    RemoteService service;

    private static final String NOTHING_SELECTED_MARKER = "---";

    private ObservableList<String> topRelationsTypes;
    private final ObjectProperty<RelationType> topRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedTopRelationType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private final StringProperty description = new SimpleStringProperty();

    private final StringProperty sourceSynset = new SimpleStringProperty();
    private final StringProperty targetSynset = new SimpleStringProperty();

    private ObservableList<String> subRelationsTypes;
    private final ObjectProperty<RelationType> subRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedSubRelationType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private final ReadOnlyBooleanWrapper subRelationTypeInputDisabled = new ReadOnlyBooleanWrapper();

    private ItemList<RelationType> topRelationTypeItemList;
    private ItemList<RelationType> subRelationTypeItemList;

    private ObservableList<TestListItemViewModel> testList = FXCollections.observableArrayList();
    private ObjectProperty<TestListItemViewModel> selectedTestListItem = new SimpleObjectProperty<>();

    private final CompositeValidator formValidator = new CompositeValidator();

    private Validator sourceSynsetValidator;
    private Validator targetSynsetValidator;
    private Validator topRelationValidator;
    private Validator subRelationValidator;

    private Command swapCommand;

    @Inject
    SynsetRelationScope synsetRelationScope;

    public SynsetRelationFormViewModel() {
        sourceSynsetValidator = new FunctionBasedValidator<>(
                sourceSynsetProperty(),
                ss -> ss != null && !ss.trim().isEmpty(),
                ValidationMessage.error("Source synset must be selected")
        );

        topRelationValidator = new FunctionBasedValidator<>(
                selectedTopRelationTypeProperty(),
                ss -> ss != null && !ss.equals(NOTHING_SELECTED_MARKER),
                ValidationMessage.error("Select relation type")
        );

        subRelationValidator = new FunctionBasedValidator<>(
                selectedSubRelationTypeProperty(),
                this::isSubRelationTypeValid,
                ValidationMessage.error("Select relation type")
        );

        targetSynsetValidator = new FunctionBasedValidator<>(
                targetSynsetProperty(),
                ss -> ss != null && !ss.trim().isEmpty(),
                ValidationMessage.error("Target synset must be selected")
        );

        formValidator.addValidators(
                sourceSynsetValidator, targetSynsetValidator,
                topRelationValidator, subRelationValidator
        );

    }

    private boolean isSubRelationTypeValid(String ss) {
        if (subRelationTypeItemList != null && subRelationTypeItemList.getSourceList().size() > 0
                && ss != null && ss.equals(NOTHING_SELECTED_MARKER)) {
            return false;
        }
        return true;
    }

    public void initialize() {
        initTopRelationTypeList();
        initSubRelationTypeList();

        loadSynsets();
        setSynsetLabels();
        swapCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                swapSynsets();
            }
        });

        selectedTopRelationType.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> matching = RelationTypeService.getTopSynsetRelationTypes()
                        .stream()
                        .filter(d -> newV.equals(d.getName()))
                        .findFirst();
                if (matching.isPresent()) {
                    topRelationType.set(matching.get());
                    description.set(matching.get().getDescription());
                    synsetRelationScope.setRelationType(matching.get());
                    if (matching.get().getSubrelations() != null && !matching.get().getSubrelations().isEmpty()) {
                        subRelationTypeItemList.getSourceList().clear();
                        subRelationTypeItemList.getSourceList().addAll(matching.get()
                                .getSubrelations());
                    } else {
                        subRelationTypeItemList.getSourceList().clear();
                    }
                    List<RelationTest> tests = service.getRelationTests(matching.get().getLinks().getTests());
                    testList.clear();
                    testList.addAll(prepareTestList(tests,
                            synsetRelationScope.getParentSynset().getSenses(),
                            synsetRelationScope.getChildSynset().getSenses()));
                }

            } else if (NOTHING_SELECTED_MARKER.equals(newV)) {
                topRelationType.set(null);
                subRelationTypeItemList.getSourceList().clear();
            }
            selectedSubRelationType.set(NOTHING_SELECTED_MARKER);
        });

        selectedSubRelationType.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> subdivisionOptional = RelationTypeService.getSynsetRelationTypes()
                        .stream()
                        .filter(subdivision -> subdivision.getName().equals(newV)).findFirst();

                if (subdivisionOptional.isPresent()) {
                    subRelationType.set(subdivisionOptional.get());
                    description.set(subdivisionOptional.get().getDescription());
                    synsetRelationScope.setRelationType(subdivisionOptional.get());
                    List<RelationTest> tests = service.getRelationTests(subdivisionOptional.get().getLinks().getTests());
                    testList.clear();
                    testList.addAll(prepareTestList(tests,
                            synsetRelationScope.getParentSynset().getSenses(),
                            synsetRelationScope.getChildSynset().getSenses()));
                } else {
                    subRelationType.set(null);
                }
            } else {
                subRelationType.set(null);
            }
        });

        subRelationTypeInputDisabled.bind(Bindings.size(subRelationTypeList()).lessThanOrEqualTo(1));
    }

    private List<TestListItemViewModel> prepareTestList(List<RelationTest> tests, List<Sense> parent, List<Sense> child) {

        List<TestListItemViewModel> testList = new ArrayList<>();
        List<Sense> p = new ArrayList<>();
        List<Sense> c = new ArrayList<>();

        parent.forEach(s -> p.add(service.findSense(s.getLinks().getSelf())));
        child.forEach(s -> c.add(service.findSense(s.getLinks().getSelf())));

        p.forEach(x -> c.forEach(y -> tests.forEach(t -> testList.add(new TestListItemViewModel(t, x, y)))));

        return testList;
    }

    private void swapSynsets() {
        synsetRelationScope.swapSynsets();

        setSynsetLabels();

        testList.clear();
        if (topRelationType.get() != null) {
            if (subRelationType.get() != null) {
                List<RelationTest> tests = service.getRelationTests(subRelationType.get().getLinks().getTests());
                testList.addAll(prepareTestList(tests,
                        synsetRelationScope.getParentSynset().getSenses(),
                        synsetRelationScope.getChildSynset().getSenses()));
            } else {
                List<RelationTest> tests = service.getRelationTests(topRelationType.get().getLinks().getTests());
                testList.addAll(prepareTestList(tests,
                        synsetRelationScope.getParentSynset().getSenses(),
                        synsetRelationScope.getChildSynset().getSenses()));
            }
        }
    }

    private void resetForm() {
        sourceSynset.set(null);
        targetSynset.set(null);
        testList.clear();
        topRelationType.set(null);
        subRelationType.set(null);
    }

    public void setSynsetLabels() {
        sourceSynset.set(constructSynsetLabel(synsetRelationScope.getParentSynset().getSenses()));
        targetSynset.set(constructSynsetLabel(synsetRelationScope.getChildSynset().getSenses()));
    }

    public void loadSynsets() {
        Synset parent = service.findSynset(synsetRelationScope.getFirstSelectedSynset());
        Synset child = service.findSynset(synsetRelationScope.getSecondSelectedSynset());
        synsetRelationScope.setParentSynset(parent);
        synsetRelationScope.setChildSynset(child);
    }

    private String constructSynsetLabel(List<Sense> senses) {
        return senses.stream()
                .map(Sense::getLabel)
                .collect(Collectors.joining(","));
    }

    private void initTopRelationTypeList() {
        topRelationTypeItemList = new ItemList<>(RelationTypeService.getTopSynsetRelationTypes(), RelationType::getName);
        ObservableList<String> mappedList = topRelationTypeItemList.getTargetList();

        topRelationsTypes = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        topRelationsTypes.addListener((ListChangeListener<String>) p -> selectedTopRelationType.set(NOTHING_SELECTED_MARKER));
    }

    private void initSubRelationTypeList() {
        subRelationTypeItemList = new ItemList<>(FXCollections.observableArrayList(), RelationType::getName);
        ObservableList<String> mappedList = subRelationTypeItemList.getTargetList();
        subRelationsTypes = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        subRelationsTypes.addListener((ListChangeListener<String>) p -> selectedSubRelationType.set(NOTHING_SELECTED_MARKER));
    }

    public Command getSwapCommand() {
        return swapCommand;
    }

    public ObservableList<String> topRelationTypeList() {
        return topRelationsTypes;
    }

    public Property<String> selectedTopRelationTypeProperty() {
        return selectedTopRelationType;
    }

    public ObservableList<String> subRelationTypeList() {
        return subRelationsTypes;
    }

    public Property<String> selectedSubRelationTypeProperty() {
        return selectedSubRelationType;
    }

    public boolean isSubRelationTypeInputDisabled() {
        return subRelationTypeInputDisabled.get();
    }

    public ReadOnlyBooleanWrapper subRelationTypeInputDisabledProperty() {
        return subRelationTypeInputDisabled;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<TestListItemViewModel> getTestList() {
        return testList;
    }

    public TestListItemViewModel getSelectedTestListItem() {
        return selectedTestListItem.get();
    }

    public ObjectProperty<TestListItemViewModel> selectedTestListItemProperty() {
        return selectedTestListItem;
    }

    public String getSourceSynset() {
        return sourceSynset.get();
    }

    public StringProperty sourceSynsetProperty() {
        return sourceSynset;
    }

    public String getTargetSynset() {
        return targetSynset.get();
    }

    public StringProperty targetSynsetProperty() {
        return targetSynset;
    }

}

