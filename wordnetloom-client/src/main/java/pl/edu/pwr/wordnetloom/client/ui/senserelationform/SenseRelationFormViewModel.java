package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.saxsys.mvvmfx.InjectScope;
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
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SenseRelationFormViewModel implements ViewModel {

    @Inject
    RemoteService service;

    private static final String NOTHING_SELECTED_MARKER = "---";

    private ObservableList<String> topRelationsTypes;
    private final ObjectProperty<RelationType> topRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedTopRelationType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private final StringProperty description = new SimpleStringProperty();

    private final StringProperty sourceSense = new SimpleStringProperty();
    private final StringProperty targetSense = new SimpleStringProperty();

    private ObservableList<String> subRelationsTypes;
    private final ObjectProperty<RelationType> subRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedSubRelationType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private final ReadOnlyBooleanWrapper subRelationTypeInputDisabled = new ReadOnlyBooleanWrapper();

    private ItemList<RelationType> topRelationTypeItemList;
    private ItemList<RelationType> subRelationTypeItemList;

    private ObservableList<TestListItemViewModel> testList = FXCollections.observableArrayList();
    private ObjectProperty<TestListItemViewModel> selectedTestListItem = new SimpleObjectProperty<>();

    private final CompositeValidator formValidator = new CompositeValidator();

    private Validator sourceSenseValidator;
    private Validator targetSenseValidator;
    private Validator topRelationValidator;
    private Validator subRelationValidator;

    private Command swapCommand;

    @InjectScope
    SenseRelationDialogScope senseRelationDialogScope;

    public SenseRelationFormViewModel() {
        sourceSenseValidator = new FunctionBasedValidator<>(
                sourceSenseProperty(),
                ss -> ss != null && ! ss.trim().isEmpty(),
                ValidationMessage.error("Source sense must be selected")
        );

        topRelationValidator = new FunctionBasedValidator<>(
                selectedTopRelationTypeProperty(),
                ss -> ss != null && ! ss.equals(NOTHING_SELECTED_MARKER),
                ValidationMessage.error("Select relation type")
        );

        subRelationValidator = new FunctionBasedValidator<>(
                selectedSubRelationTypeProperty(),
                this::isSubRelationTypeValid,
                ValidationMessage.error("Select relation type")
        );

        targetSenseValidator = new FunctionBasedValidator<>(
                targetSenseProperty(),
                ss -> ss != null && ! ss.trim().isEmpty(),
                ValidationMessage.error("Target sense must be selected")
        );

        formValidator.addValidators(
                sourceSenseValidator, targetSenseValidator,
                topRelationValidator,subRelationValidator
        );

    }

    private boolean isSubRelationTypeValid(String ss) {
        if(subRelationTypeItemList != null && subRelationTypeItemList.getSourceList().size() > 0
                && ss!= null && ss.equals(NOTHING_SELECTED_MARKER)){
            return false;
        }
        return true;
    }

    public void initialize() {
        initTopRelationTypeList();
        initSubRelationTypeList();
        senseRelationDialogScope.subscribe(SenseRelationDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        senseRelationDialogScope.subscribe(SenseRelationDialogScope.REFRESH_SENSES,
                (key, payload) -> setSenses());

        swapCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                swapSenses();
            }
        });

        selectedTopRelationType.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> matching = RelationTypeService.getTopSenseRelationTypes()
                        .stream()
                        .filter(d -> newV.equals(d.getName()))
                        .findFirst();
                if (matching.isPresent()) {
                    topRelationType.set(matching.get());
                    description.set(matching.get().getDescription());
                    senseRelationDialogScope.setRelationType(matching.get());
                    if (matching.get().getSubrelations() != null && !matching.get().getSubrelations().isEmpty()) {
                        subRelationTypeItemList.getSourceList().clear();
                        subRelationTypeItemList.getSourceList().addAll(matching.get()
                                .getSubrelations());
                    }else {
                        subRelationTypeItemList.getSourceList().clear();
                    }
                    List<RelationTest> tests = service.getRelationTests(matching.get().getLinks().getTests());
                    testList.clear();
                    testList.addAll(tests.stream()
                            .map(rt -> new TestListItemViewModel(rt,
                                    senseRelationDialogScope.getParentSense(),
                                    senseRelationDialogScope.getChildSense()))
                            .collect(Collectors.toList()));
                }

            } else if (NOTHING_SELECTED_MARKER.equals(newV)) {
                topRelationType.set(null);
                subRelationTypeItemList.getSourceList().clear();
            }
            selectedSubRelationType.set(NOTHING_SELECTED_MARKER);
        });

        selectedSubRelationType.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> subdivisionOptional = RelationTypeService.getSenseRelationTypes()
                        .stream()
                        .filter(subdivision -> subdivision.getName().equals(newV)).findFirst();

                if (subdivisionOptional.isPresent()) {
                    subRelationType.set(subdivisionOptional.get());
                    description.set(subdivisionOptional.get().getDescription());
                    senseRelationDialogScope.setRelationType(subdivisionOptional.get());
                    List<RelationTest> tests = service.getRelationTests(subdivisionOptional.get().getLinks().getTests());
                    testList.clear();
                    testList.addAll(tests.stream()
                            .map(rt -> new TestListItemViewModel(rt,
                                    senseRelationDialogScope.getParentSense(),
                                    senseRelationDialogScope.getChildSense()))
                            .collect(Collectors.toList()));
                } else {
                    subRelationType.set(null);
                }
            } else {
                subRelationType.set(null);
            }
        });

        subRelationTypeInputDisabled.bind(Bindings.size(subRelationTypeList()).lessThanOrEqualTo(1));
        senseRelationDialogScope.senseRelationFormValidProperty().bind(formValidator.getValidationStatus().validProperty());
    }

    private void swapSenses() {
        Sense p = senseRelationDialogScope.getParentSense();
        Sense c=  senseRelationDialogScope.getChildSense();
        senseRelationDialogScope.setChildSense(p);
        senseRelationDialogScope.setParentSense(c);
        setSenses();

        testList.clear();
        if(topRelationType.get() != null){
            if(subRelationType.get() != null){
                List<RelationTest> tests = service.getRelationTests(subRelationType.get().getLinks().getTests());
                testList.addAll(tests.stream()
                        .map(rt -> new TestListItemViewModel(rt,
                                senseRelationDialogScope.getParentSense(),
                                senseRelationDialogScope.getChildSense()))
                        .collect(Collectors.toList()));
            }else{
                List<RelationTest> tests = service.getRelationTests(topRelationType.get().getLinks().getTests());
                testList.addAll(tests.stream()
                        .map(rt -> new TestListItemViewModel(rt,
                                senseRelationDialogScope.getParentSense(),
                                senseRelationDialogScope.getChildSense()))
                        .collect(Collectors.toList()));
            }
        }
    }

    private void resetForm() {
        sourceSense.set(null);
        targetSense.set(null);
        testList.clear();
        topRelationType.set(null);
        subRelationType.set(null);
    }

    public void setSenses(){
        sourceSense.set(constructSenseLabel(senseRelationDialogScope.getParentSense()));
        targetSense.set(constructSenseLabel(senseRelationDialogScope.getChildSense()));
    }

    private String constructSenseLabel(Sense sense){
        StringBuilder sb = new StringBuilder();
        sb.append(sense.getLemma());
        sb.append(" ").append(sense.getVariant());
        if(sense.getDomain() != null){
            sb.append(" (")
                    .append(Dictionaries.getDictionaryItemById(Dictionaries.DOMAIN_DICTIONARY, sense.getDomain()))
                    .append(") ");
        }
        if(sense.getLexicon() != null){
            sb.append(Dictionaries.getDictionaryItemById(Dictionaries.LEXICON_DICTIONARY,sense.getLexicon()));
        }
        return sb.toString();
    }

    private void initTopRelationTypeList() {
        topRelationTypeItemList = new ItemList<>(RelationTypeService.getTopSenseRelationTypes(), RelationType::getName);
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

    public String getSourceSense() {
        return sourceSense.get();
    }

    public StringProperty sourceSenseProperty() {
        return sourceSense;
    }

    public String getTargetSense() {
        return targetSense.get();
    }

    public StringProperty targetSenseProperty() {
        return targetSense;
    }

}

