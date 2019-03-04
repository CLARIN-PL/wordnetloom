package pl.edu.pwr.wordnetloom.client.ui.relationtypeform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.GlobalWordnetRelationType;
import pl.edu.pwr.wordnetloom.client.model.RelationArgument;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeDirection;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTypeDialogScope;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelationTypeFormViewModel implements ViewModel {

    public static final String OPEN_PART_OF_SPEECH_DIALOG = "open_part_of_speech_dialog";
    public static final String OPEN_LEXICONS_DIALOG = "open_lexicons_dialog";

    @InjectScope
    RelationTypeDialogScope dialogScope;

    @Inject
    RemoteService service;

    private StringProperty name = new SimpleStringProperty();
    private StringProperty shortcut = new SimpleStringProperty();
    private StringProperty display = new SimpleStringProperty();
    private StringProperty lexicon = new SimpleStringProperty();
    private StringProperty posField = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private BooleanProperty interlingual = new SimpleBooleanProperty(false);
    private BooleanProperty autoReverse = new SimpleBooleanProperty(false);
    private ObjectProperty<javafx.scene.paint.Color> color = new SimpleObjectProperty<>();

    static final String NOTHING_SELECTED_MARKER = "----------";

    private ObservableList<String> directions;
    private final ObjectProperty<NodeDirection> nodeDirection = new SimpleObjectProperty<>();
    private final StringProperty selectedDirection = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> types;
    private final ObjectProperty<RelationArgument> type = new SimpleObjectProperty<>();
    private final StringProperty selectedType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> parents;
    private final ObjectProperty<RelationType> parent = new SimpleObjectProperty<>();
    private final StringProperty selectedParent = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> reverses;
    private final ObjectProperty<RelationType> reverse = new SimpleObjectProperty<>();
    private final StringProperty selectedReverse = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> globalWordnetRelationTypes = FXCollections.observableArrayList();
    private final ObjectProperty<GlobalWordnetRelationType> globalWordnetType = new SimpleObjectProperty<>();
    private final StringProperty selectedGlobalWordnet = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    // Don't inline this field. It's needed to prevent the list mapping from being garbage collected.
    private ItemList<NodeDirection> directionItemList;
    private ItemList<RelationArgument> typesItemList;
    private ItemList<GlobalWordnetRelationType> globalWordnetItemList;
    private ItemList<RelationType> parentItemList;
    private ItemList<RelationType> reverseItemList;

    private Command selectLexiconsCommand;
    private Command selectPartOfSpeechCommand;

    public void initialize() {
        initDirectionsList();
        initGlobalWordnetList();
        initTypesList();
        initParentRelationTypeList();
        initReverseRelationTypeList();

        selectPartOfSpeechCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                selectPartOfSpeech();
            }
        });
        selectLexiconsCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                selectLexicons();
            }
        });

        selectedDirection.addListener((obs, oldV, newV) -> {
            selectDirection(obs,oldV,newV, NOTHING_SELECTED_MARKER, nodeDirection);
        });

        selectedType.addListener((obs, oldV, newV) -> {
            selectArgument(obs,oldV,newV, NOTHING_SELECTED_MARKER, type);
        });

        selectedParent.addListener((obs, oldV, newV) -> {
            selectParent(obs,oldV,newV, NOTHING_SELECTED_MARKER, parent);
        });

        selectedReverse.addListener((obs, oldV, newV) -> {
            selectReverse(obs,oldV,newV, NOTHING_SELECTED_MARKER, reverse);
        });

        selectedGlobalWordnet.addListener((obs, oldV, newV) -> {
            selectGlobal(obs,oldV,newV, NOTHING_SELECTED_MARKER, globalWordnetType);
        });

        dialogScope.subscribe(RelationTypeDialogScope.SHOW_RELATION, (s, objects) -> {
            if (dialogScope.getRelationTypeToEdit() != null) {
                RelationType rt = service.getRelationType(dialogScope.getRelationTypeToEdit().getLinks().getSelf());
                dialogScope.setRelationTypeToEdit(rt);
                mapRelationType(rt);
            }
        });

        dialogScope.subscribe(RelationTypeDialogScope.SHOW_NEW_RELATION, (s, objects) -> {
            if (dialogScope.getRelationTypeToEdit() != null) {
                RelationType rt = dialogScope.getRelationTypeToEdit();
                dialogScope.setRelationTypeToEdit(rt);
                mapRelationType(rt);
            }
        });

        dialogScope.subscribe(RelationTypeDialogScope.REFRESH_MODEL, (s, objects) -> {
            mapRelationType(dialogScope.getRelationTypeToEdit());
        });

        dialogScope.subscribe(RelationTypeDialogScope.COMMIT, (s, objects) -> {
            commitChanges();
        });
    }

    public static void selectParent(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                      ObjectProperty<RelationType> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<RelationType> matching = RelationTypeService.getTopRelations().stream()
                    .filter(d -> newV.equals(d.getNameWithType()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public static void selectReverse(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                    ObjectProperty<RelationType> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<RelationType> matching =  RelationTypeService.getAllRelations().stream()
                    .filter(d -> newV.equals(d.getNameWithType()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public static void selectArgument(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                    ObjectProperty<RelationArgument> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<RelationArgument> matching = Stream.of(RelationArgument.values())
                    .filter(d -> newV.equals(d.name()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public static void selectGlobal(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                      ObjectProperty<GlobalWordnetRelationType> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<GlobalWordnetRelationType> matching = Stream.of(GlobalWordnetRelationType.values())
                    .filter(d -> newV.equals(d.name()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public static void selectDirection(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                    ObjectProperty<NodeDirection> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<NodeDirection> matching = NodeDirection.streamAll()
                    .filter(d -> newV.equals(d.getAsString()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    private void commitChanges() {
        RelationType rt = dialogScope.getRelationTypeToEdit();
        rt.setName(name.get());
        rt.setDisplay(display.get());
        rt.setDescription(description.get());
        rt.setShortName(shortcut.get());
        rt.setMultilingual(interlingual.get());
        rt.setAutoReverse(autoReverse.get());
        String webFormat = getFormatColor(getColor());
        rt.setColor(webFormat);
        rt.setGlobalWordnetRelationType(globalWordnetType.get());
        rt.setArgument(type.get());
        rt.setDirection(nodeDirection.get());
        rt.setReverseRelation(reverse.get());
        rt.setParentRelation(parent.get());
        dialogScope.setRelationTypeToEdit(rt);
    }

    public static String getFormatColor(Color c) {
        return String.format("#%02x%02x%02x",
                (int) (255 * c.getRed()),
                (int) (255 * c.getGreen()),
                (int) (255 * c.getBlue()));
    }

    public void selectPartOfSpeech() {
        commitChanges();
        publish(OPEN_PART_OF_SPEECH_DIALOG);
        dialogScope.publish(RelationTypeDialogScope.LOAD_PARTS_OF_SPEECH);
    }

    public void selectLexicons() {
        commitChanges();
        publish(OPEN_LEXICONS_DIALOG);
        dialogScope.publish(RelationTypeDialogScope.LOAD_LEXICONS);
    }

    private void initParentRelationTypeList() {
        parentItemList = new ItemList<>(RelationTypeService.getTopRelations(), RelationType::getNameWithType);
        ObservableList<String> mappedList = parentItemList.getTargetList();

        parents = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        parents.addListener((ListChangeListener<String>) p -> selectedParent.set(NOTHING_SELECTED_MARKER));
    }


    private void initReverseRelationTypeList() {
        reverseItemList = new ItemList<>(RelationTypeService.getAllRelations(), RelationType::getNameWithType);
        ObservableList<String> mappedList = reverseItemList.getTargetList();

        reverses = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        reverses.addListener((ListChangeListener<String>) p -> selectedReverse.set(NOTHING_SELECTED_MARKER));
    }

    private void initDirectionsList() {
        directionItemList = new ItemList<>(FXCollections.observableArrayList(Arrays.asList(NodeDirection.values())), NodeDirection::getAsString);
        ObservableList<String> mappedList = directionItemList.getTargetList();
        directions = createList(mappedList);
        directions.addListener((ListChangeListener<String>) p -> selectedDirection.set(NOTHING_SELECTED_MARKER));
    }

    private void initTypesList() {
        typesItemList = new ItemList<>(FXCollections.observableArrayList(Arrays.asList(RelationArgument.values())), RelationArgument::name);
        ObservableList<String> mappedList = typesItemList.getTargetList();
        types = createList(mappedList);
        types.addListener((ListChangeListener<String>) p -> selectedType.set(NOTHING_SELECTED_MARKER));
    }

    private ObservableList<String> createList(ObservableList<String> source) {
        final ObservableList<String> result = FXCollections.observableArrayList();
        result.addAll(source);
        // for sure there are better solutions for this but it's sufficient for our demo
        source.addListener((ListChangeListener<String>) c -> {
            result.clear();
            result.addAll(source);
        });
        return result;
    }


    private void initGlobalWordnetList() {
        globalWordnetItemList = new ItemList<>(FXCollections.observableArrayList(Arrays.asList(GlobalWordnetRelationType.values())), GlobalWordnetRelationType::name);
        ObservableList<String> mappedList = globalWordnetItemList.getTargetList();
        globalWordnetRelationTypes = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        globalWordnetRelationTypes.addListener((ListChangeListener<String>) p -> selectedDirection.set(NOTHING_SELECTED_MARKER));
    }

    private void mapRelationType(RelationType rt) {
        if(rt.getArgument() != null){
            selectedType.set(rt.getArgument().name());
        }
        name.set(rt.getName());
        shortcut.set(rt.getShortName());
        description.set(rt.getDescription());
        display.set(rt.getDisplay());
        if(rt.getMultilingual() != null) {
            interlingual.set(rt.getMultilingual());
        }
        posField.set(buildDictionaryString(rt.getAllowedPartsOfSpeech()));
        if(rt.getAutoReverse() != null) {
            autoReverse.set(rt.getAutoReverse());
        }
        lexicon.set(buildDictionaryString(rt.getAllowedLexicons()));
        if(rt.getColor() != null) {
            color.set(javafx.scene.paint.Color.web(rt.getColor()));
        }

        if (rt.getDirection() != null) {
            selectedDirection.set(rt.getDirection().name());
        }else{
            selectedDirection.set(NodeDirection.IGNORE.name());
        }

        if (rt.getGlobalWordnetRelationType() != null) {
            selectedGlobalWordnet.set(rt.getGlobalWordnetRelationType().name());
        }else{
            selectedGlobalWordnet.set(GlobalWordnetRelationType.other.name());
        }

        if(rt.getParentRelation() != null){
            selectedParent.set(rt.getParentRelation().getName());
        }else{
            selectedParent.set(NOTHING_SELECTED_MARKER);
        }

        if(rt.getReverseRelation() != null){
            selectedReverse.set(rt.getReverseRelation().getName());
        }else{
            selectedReverse.set(NOTHING_SELECTED_MARKER);
        }
    }

    private String buildDictionaryString(List<Dictionary> dics) {
        if (dics != null) {
            return dics.stream()
                    .map(Dictionary::getName)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getShortcut() {
        return shortcut.get();
    }

    public StringProperty shortcutProperty() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut.set(shortcut);
    }

    public String getDisplay() {
        return display.get();
    }

    public StringProperty displayProperty() {
        return display;
    }

    public void setDisplay(String display) {
        this.display.set(display);
    }

    public String getLexicon() {
        return lexicon.get();
    }

    public StringProperty lexiconProperty() {
        return lexicon;
    }

    public void setLexicon(String lexicon) {
        this.lexicon.set(lexicon);
    }

    public String getPosField() {
        return posField.get();
    }

    public StringProperty posFieldProperty() {
        return posField;
    }

    public void setPosField(String posField) {
        this.posField.set(posField);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public boolean isInterlingual() {
        return interlingual.get();
    }

    public BooleanProperty interlingualProperty() {
        return interlingual;
    }

    public void setInterlingual(boolean interlingual) {
        this.interlingual.set(interlingual);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObservableList<String> getDirections() {
        return directions;
    }

    public ItemList<NodeDirection> getDirectionItemList() {
        return directionItemList;
    }

    public void setDirectionItemList(ItemList<NodeDirection> directionItemList) {
        this.directionItemList = directionItemList;
    }

    public ItemList<GlobalWordnetRelationType> getGlobalWordnetItemList() {
        return globalWordnetItemList;
    }

    public void setGlobalWordnetItemList(ItemList<GlobalWordnetRelationType> globalWordnetItemList) {
        this.globalWordnetItemList = globalWordnetItemList;
    }

    public void setDirections(ObservableList<String> directions) {
        this.directions = directions;
    }

    public NodeDirection getNodeDirection() {
        return nodeDirection.get();
    }

    public ObjectProperty<NodeDirection> nodeDirectionProperty() {
        return nodeDirection;
    }

    public void setNodeDirection(NodeDirection nodeDirection) {
        this.nodeDirection.set(nodeDirection);
    }

    public String getSelectedDirection() {
        return selectedDirection.get();
    }

    public StringProperty selectedDirectionProperty() {
        return selectedDirection;
    }

    public void setSelectedDirection(String selectedDirection) {
        this.selectedDirection.set(selectedDirection);
    }

    public ObservableList<String> getGlobalWordnetRelationTypes() {
        return globalWordnetRelationTypes;
    }

    public void setGlobalWordnetRelationTypes(ObservableList<String> globalWordnetRelationTypes) {
        this.globalWordnetRelationTypes = globalWordnetRelationTypes;
    }

    public GlobalWordnetRelationType getGlobalWordnetType() {
        return globalWordnetType.get();
    }

    public ObjectProperty<GlobalWordnetRelationType> globalWordnetTypeProperty() {
        return globalWordnetType;
    }

    public void setGlobalWordnetType(GlobalWordnetRelationType globalWordnetType) {
        this.globalWordnetType.set(globalWordnetType);
    }

    public String getSelectedGlobalWordnet() {
        return selectedGlobalWordnet.get();
    }

    public StringProperty selectedGlobalWordnetProperty() {
        return selectedGlobalWordnet;
    }

    public void setSelectedGlobalWordnet(String selectedGlobalWordnet) {
        this.selectedGlobalWordnet.set(selectedGlobalWordnet);
    }

    public Command getSelectPartOfSpeechCommand() {
        return selectPartOfSpeechCommand;
    }

    public Command getSelectLexiconsCommand() {
        return selectLexiconsCommand;
    }

    public ObservableList<String> getTypes() {
        return types;
    }

    public void setTypes(ObservableList<String> types) {
        this.types = types;
    }

    public RelationArgument getType() {
        return type.get();
    }

    public ObjectProperty<RelationArgument> typeProperty() {
        return type;
    }

    public void setType(RelationArgument type) {
        this.type.set(type);
    }

    public String getSelectedType() {
        return selectedType.get();
    }

    public StringProperty selectedTypeProperty() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType.set(selectedType);
    }

    public ItemList<RelationArgument> getTypesItemList() {
        return typesItemList;
    }

    public void setTypesItemList(ItemList<RelationArgument> typesItemList) {
        this.typesItemList = typesItemList;
    }

    public ObservableList<String> getParents() {
        return parents;
    }

    public void setParents(ObservableList<String> parents) {
        this.parents = parents;
    }

    public RelationType getParent() {
        return parent.get();
    }

    public ObjectProperty<RelationType> parentProperty() {
        return parent;
    }

    public void setParent(RelationType parent) {
        this.parent.set(parent);
    }

    public String getSelectedParent() {
        return selectedParent.get();
    }

    public StringProperty selectedParentProperty() {
        return selectedParent;
    }

    public void setSelectedParent(String selectedParent) {
        this.selectedParent.set(selectedParent);
    }

    public ObservableList<String> getReverses() {
        return reverses;
    }

    public void setReverses(ObservableList<String> reverses) {
        this.reverses = reverses;
    }

    public RelationType getReverse() {
        return reverse.get();
    }

    public ObjectProperty<RelationType> reverseProperty() {
        return reverse;
    }

    public void setReverse(RelationType reverse) {
        this.reverse.set(reverse);
    }

    public String getSelectedReverse() {
        return selectedReverse.get();
    }

    public StringProperty selectedReverseProperty() {
        return selectedReverse;
    }

    public void setSelectedReverse(String selectedReverse) {
        this.selectedReverse.set(selectedReverse);
    }

    public ItemList<RelationType> getParentItemList() {
        return parentItemList;
    }

    public void setParentItemList(ItemList<RelationType> parentItemList) {
        this.parentItemList = parentItemList;
    }

    public ItemList<RelationType> getReverseItemList() {
        return reverseItemList;
    }

    public void setReverseItemList(ItemList<RelationType> reverseItemList) {
        this.reverseItemList = reverseItemList;
    }

    public void setSelectLexiconsCommand(Command selectLexiconsCommand) {
        this.selectLexiconsCommand = selectLexiconsCommand;
    }

    public void setSelectPartOfSpeechCommand(Command selectPartOfSpeechCommand) {
        this.selectPartOfSpeechCommand = selectPartOfSpeechCommand;
    }

    public boolean isAutoReverse() {
        return autoReverse.get();
    }

    public BooleanProperty autoReverseProperty() {
        return autoReverse;
    }

    public void setAutoReverse(boolean autoReverse) {
        this.autoReverse.set(autoReverse);
    }
}

