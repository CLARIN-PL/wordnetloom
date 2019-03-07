package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.VariantType;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperty;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemViewModel;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YiddishPropertiesFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";

    private StringProperty yivo = new SimpleStringProperty();
    private StringProperty latin = new SimpleStringProperty();
    private StringProperty yiddish = new SimpleStringProperty();
    private StringProperty transcription = new SimpleStringProperty();
    private StringProperty etymologicalRoot = new SimpleStringProperty();
    private StringProperty meaning = new SimpleStringProperty();
    private StringProperty comment = new SimpleStringProperty();
    private StringProperty context = new SimpleStringProperty();
    private StringProperty etymology = new SimpleStringProperty();
    private StringProperty inflection = new SimpleStringProperty();

    private ObservableList<String> variantTypes;
    private final ObjectProperty<VariantType> variantType = new SimpleObjectProperty<>();
    private final StringProperty selectedVariantType = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> grammaticalGenders;
    private final ObjectProperty<Dictionary> grammaticalGender = new SimpleObjectProperty<>();
    private final StringProperty selectedGrammaticalGender = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> semanticFields;
    private final ObjectProperty<Dictionary> semanticField = new SimpleObjectProperty<>();
    private final StringProperty selectedSemanticField = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> semanticFieldMods;
    private final ObjectProperty<Dictionary> semanticFieldMod = new SimpleObjectProperty<>();
    private final StringProperty selectedSemanticFieldMod = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> statuses;
    private final ObjectProperty<Dictionary> status = new SimpleObjectProperty<>();
    private final StringProperty selectedStatus = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> styles;
    private final ObjectProperty<Dictionary> style = new SimpleObjectProperty<>();
    private final StringProperty selectedStyle = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> ages;
    private final ObjectProperty<Dictionary> age = new SimpleObjectProperty<>();
    private final StringProperty selectedAge = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> sources;
    private final ObjectProperty<Dictionary> source = new SimpleObjectProperty<>();
    private final StringProperty selectedSource = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> lexicalCharacteristics;
    private final ObjectProperty<Dictionary> lexicalCharacteristic = new SimpleObjectProperty<>();
    private final StringProperty selectedLexicalCharacteristic = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> inflectionsCmb;
    private final ObjectProperty<Dictionary> inflectionCmb = new SimpleObjectProperty<>();
    private final StringProperty selectedInflectionCmb = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

//    public ComboBox particleTypeCombo, particleCombo;
    //  public ComboBox transcriptionType;

    private ItemList<VariantType> variantTypeItemList;
    private ItemList<Dictionary> ageItemList;
    private ItemList<Dictionary> grammaticalGenderItemList;
    private ItemList<Dictionary> semanticFieldItemList;
    private ItemList<Dictionary> semanticFieldModItemList;
    private ItemList<Dictionary> statusesItemList;
    private ItemList<Dictionary> stylesItemList;
    private ItemList<Dictionary> sourcesItemList;
    private ItemList<Dictionary> inflectionsItemList;
    private ItemList<Dictionary> lexicalCharacteristicsItemList;

    private ObservableList<DictionaryListItemViewModel> sourceList = FXCollections.observableArrayList();
    private ObjectProperty<DictionaryListItemViewModel> selectedSourceListItem = new SimpleObjectProperty<>();

    private ObservableList<SemanticFieldListItemViewModel> semanticFiledList = FXCollections.observableArrayList();
    private ObjectProperty<SemanticFieldListItemViewModel> selectedSemanticFieldListItem = new SimpleObjectProperty<>();

    private ObservableList<InflectionListItemViewModel> inflectionFiledList = FXCollections.observableArrayList();
    private ObjectProperty<InflectionListItemViewModel> selectedInflectionListItem = new SimpleObjectProperty<>();

    private ObservableList<ParticleListItemViewModel> particleList = FXCollections.observableArrayList();
    private ObjectProperty<ParticleListItemViewModel> selectedParticleListItem = new SimpleObjectProperty<>();

    private ObservableList<TranscriptionListItemViewModel> transcriptionList = FXCollections.observableArrayList();
    private ObjectProperty<TranscriptionListItemViewModel> selectedTranscriptionListItem = new SimpleObjectProperty<>();


    private StringProperty root = new SimpleStringProperty();

    private Command addVariantCommand;
    private Command removeVariantCommand;

    private Command addTranscriptionCommand;
    private Command removeTranscriptionCommand;

    private Command addInflectionCommand;
    private Command removeInflectionCommand;

    private Command addSemanticFieldCommand;
    private Command removeSemanticFieldCommand;

    private Command addSourceCommand;
    private Command removeSourceCommand;

    private Command addParticleCommand;
    private Command removeParticleCommand;

    public void initialize(){
        initVariantsList();
        initAgeItemList();
        initAgeItemList();
        initGrammaticalGenderList();
        initLexicalCharacteristicsItemList();
        initSemanticFieldItemList();
        initSemanticFieldModItemList();
        initSourcesItemList();
        initYiddishStatusesItemList();
        initStylesList();
        initInflectionItemList();

        addVariantCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addVariant();
            }
        });

        removeVariantCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeVariant();
            }
        });

        addInflectionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addInflection();
            }
        });

        removeInflectionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeInflection();
            }
        });

        addSemanticFieldCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addVSemanticField();
            }
        });

        removeSemanticFieldCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeVSemanticField();
            }
        });

        addTranscriptionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addTranscription();
            }
        });

        removeTranscriptionCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeTranscription();
            }
        });

        addSourceCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addSource();
            }
        });

        removeSourceCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeSource();
            }
        });

        addParticleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addParticle();
            }
        });

        removeParticleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeParticle();
            }
        });

        selectedVariantType.addListener((obs, oldV, newV) -> {
            selectVariantType(obs,oldV,newV, NOTHING_SELECTED_MARKER, variantType);
        });

        selectedAge.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.AGES_DICTIONARY, NOTHING_SELECTED_MARKER, age);
        });

        selectedGrammaticalGender.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.GRAMMATICAL_GENDERS_DICTIONARY,
                    NOTHING_SELECTED_MARKER, grammaticalGender);
        });

        selectedLexicalCharacteristic.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.LEXICAL_CHARACTERISTICS_DICTIONARY,
                    NOTHING_SELECTED_MARKER, lexicalCharacteristic);
        });

        selectedStatus.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_STATUSES_DICTIONARY,
                    NOTHING_SELECTED_MARKER, status);
        });

        selectedStyle.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.STYLES_DICTIONARY,
                    NOTHING_SELECTED_MARKER, style);
        });

        selectedSemanticField.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_DOMAINS_DICTIONARY,
                    NOTHING_SELECTED_MARKER, semanticField);
        });

        selectedSemanticFieldMod.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_DOMAIN_MODIFIERS_DICTIONARY,
                    NOTHING_SELECTED_MARKER, semanticFieldMod);
        });

        selectedSource.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.SOURCES_DICTIONARY,
                    NOTHING_SELECTED_MARKER, source);
        });

        selectedInflectionCmb.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.INFLECTIONS_DICTIONARY,
                    NOTHING_SELECTED_MARKER, inflectionCmb);
        });
    }

    private void removeParticle() {
    }

    private void removeSource() {
    }

    private void removeTranscription() {
    }

    private void removeVSemanticField() {
    }

    private void removeInflection() {
    }

    private void addParticle() {
    }

    private void addSource() {
    }

    private void addTranscription() {
    }

    private void addVSemanticField() {
    }

    private void addInflection() {
    }

    private void removeVariant() {
    }

    private void addVariant() {
    }

    public void setYiddishProperty(YiddishProperty yp){

        yivo.set(yp.getYivoSpelling());
        latin.set(yp.getLatinSpelling());
        yiddish.set(yp.getYiddishSpelling());
        comment.set(yp.getComment());
        context.set(yp.getContext());
        meaning.set(yp.getMeaning());
        etymologicalRoot.set(yp.getEtymologicalRoot());
        etymology.set(yp.getEtymology());

        if (yp.getVariantType() != null) {
            selectedVariantType.set(yp.getVariantType().name());
        }

        if (yp.getAge() != null) {
            selectedAge.set(yp.getAge().getName());
        }

        if(yp.getGrammaticalGender() != null){
            selectedGrammaticalGender.set(yp.getGrammaticalGender().getName());
        }

        if(yp.getStatus() != null){
            selectedStatus.set(yp.getStatus().getName());
        }

        if(yp.getStyle() != null){
            selectedStyle.set(yp.getStyle().getName());
        }

        if(yp.getLexicalCharacteristic() != null){
            selectedLexicalCharacteristic.set(yp.getLexicalCharacteristic().getName());
        }

        if(yp.getSources() != null) {
            sourceList.addAll(yp.getSources()
                    .stream()
                    .map(DictionaryListItemViewModel::new)
                    .collect(Collectors.toList()));
        }

        if(yp.getSemanticFields() != null) {
            semanticFiledList.addAll(yp.getSemanticFields()
                    .stream()
                    .map(SemanticFieldListItemViewModel::new)
                    .collect(Collectors.toList()));
        }
        if(yp.getInflections() != null) {
            inflectionFiledList.addAll(yp.getInflections()
                    .stream()
                    .map(InflectionListItemViewModel::new)
                    .collect(Collectors.toList()));
        }

        if(yp.getTranscriptions() != null) {
            transcriptionList.addAll(yp.getTranscriptions()
                    .stream()
                    .map(TranscriptionListItemViewModel::new)
                    .collect(Collectors.toList()));
        }

        if(yp.getParticles() != null) {
            particleList.addAll(yp.getParticles()
                    .stream()
                    .map(ParticleListItemViewModel::new)
                    .collect(Collectors.toList()));
        }
    }

    private void initVariantsList() {
        variantTypeItemList = new ItemList<>(FXCollections.observableArrayList(Arrays.asList(VariantType.values())), VariantType::name);
        ObservableList<String> mappedList = variantTypeItemList.getTargetList();
        variantTypes = createList(mappedList);
        variantTypes.addListener((ListChangeListener<String>) p -> selectedVariantType.set(NOTHING_SELECTED_MARKER));
    }

    private void initAgeItemList() {
        ageItemList = Dictionaries.initDictionaryItemList(Dictionaries.AGES_DICTIONARY);
        ObservableList<String> mappedList = ageItemList.getTargetList();
        ages = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        ages.addListener((ListChangeListener<String>) p -> selectedAge.set(NOTHING_SELECTED_MARKER));
    }

    private void initInflectionItemList() {
        inflectionsItemList = Dictionaries.initDictionaryItemList(Dictionaries.INFLECTIONS_DICTIONARY);
        ObservableList<String> mappedList = inflectionsItemList.getTargetList();
        inflectionsCmb = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        inflectionsCmb.addListener((ListChangeListener<String>) p -> selectedInflectionCmb.set(NOTHING_SELECTED_MARKER));
    }

    private void initLexicalCharacteristicsItemList() {
        lexicalCharacteristicsItemList = Dictionaries.initDictionaryItemList(Dictionaries.LEXICAL_CHARACTERISTICS_DICTIONARY);
        ObservableList<String> mappedList = lexicalCharacteristicsItemList.getTargetList();
        lexicalCharacteristics = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        lexicalCharacteristics.addListener((ListChangeListener<String>) p -> selectedLexicalCharacteristic.set(NOTHING_SELECTED_MARKER));
    }


    private void initSourcesItemList() {
        sourcesItemList = Dictionaries.initDictionaryItemList(Dictionaries.SOURCES_DICTIONARY);
        ObservableList<String> mappedList = sourcesItemList.getTargetList();
        sources = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        sources.addListener((ListChangeListener<String>) p -> selectedSource.set(NOTHING_SELECTED_MARKER));
    }

    private void initYiddishStatusesItemList() {
        statusesItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_STATUSES_DICTIONARY);
        ObservableList<String> mappedList = statusesItemList.getTargetList();
        statuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        statuses.addListener((ListChangeListener<String>) p -> selectedStatus.set(NOTHING_SELECTED_MARKER));
    }

    private void initSemanticFieldModItemList() {
        semanticFieldModItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_DOMAIN_MODIFIERS_DICTIONARY);
        ObservableList<String> mappedList = semanticFieldModItemList.getTargetList();
        semanticFieldMods = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        semanticFieldMods.addListener((ListChangeListener<String>) p -> selectedSemanticFieldMod.set(NOTHING_SELECTED_MARKER));
    }

    private void initSemanticFieldItemList() {
        semanticFieldItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_DOMAINS_DICTIONARY);
        ObservableList<String> mappedList = semanticFieldItemList.getTargetList();
        semanticFields = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        semanticFields.addListener((ListChangeListener<String>) p -> selectedSemanticField.set(NOTHING_SELECTED_MARKER));
    }

    private void initGrammaticalGenderList() {
        grammaticalGenderItemList = Dictionaries.initDictionaryItemList(Dictionaries.GRAMMATICAL_GENDERS_DICTIONARY);
        ObservableList<String> mappedList = grammaticalGenderItemList.getTargetList();
        grammaticalGenders = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        grammaticalGenders.addListener((ListChangeListener<String>) p -> selectedGrammaticalGender.set(NOTHING_SELECTED_MARKER));
    }

    private void initStylesList() {
        stylesItemList = Dictionaries.initDictionaryItemList(Dictionaries.STYLES_DICTIONARY);
        ObservableList<String> mappedList = stylesItemList.getTargetList();
        statuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        statuses.addListener((ListChangeListener<String>) p -> selectedStyle.set(NOTHING_SELECTED_MARKER));
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

    public static void selectVariantType(ObservableValue ods, String oldV, String newV, String NOTHING_SELECTED,
                                       ObjectProperty<VariantType> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<VariantType> matching = Stream.of(VariantType.values())
                    .filter(d -> newV.equals(d.name()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public String getYivo() {
        return yivo.get();
    }

    public StringProperty yivoProperty() {
        return yivo;
    }

    public void setYivo(String yivo) {
        this.yivo.set(yivo);
    }

    public String getLatin() {
        return latin.get();
    }

    public StringProperty latinProperty() {
        return latin;
    }

    public void setLatin(String latin) {
        this.latin.set(latin);
    }

    public String getYiddish() {
        return yiddish.get();
    }

    public StringProperty yiddishProperty() {
        return yiddish;
    }

    public void setYiddish(String yiddish) {
        this.yiddish.set(yiddish);
    }

    public String getTranscription() {
        return transcription.get();
    }

    public StringProperty transcriptionProperty() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription.set(transcription);
    }

    public String getMeaning() {
        return meaning.get();
    }

    public StringProperty meaningProperty() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning.set(meaning);
    }

    public String getEtymology() {
        return etymology.get();
    }

    public StringProperty etymologyProperty() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology.set(etymology);
    }

    public ObservableList<DictionaryListItemViewModel> getSourceList() {
        return sourceList;
    }

    public void setSourceList(ObservableList<DictionaryListItemViewModel> sourceList) {
        this.sourceList = sourceList;
    }

    public String getInflection() {
        return inflection.get();
    }

    public StringProperty inflectionProperty() {
        return inflection;
    }

    public void setInflection(String inflection) {
        this.inflection.set(inflection);
    }

    public String getEtymologicalRoot() {
        return etymologicalRoot.get();
    }

    public StringProperty etymologicalRootProperty() {
        return etymologicalRoot;
    }

    public void setEtymologicalRoot(String etymologicalRoot) {
        this.etymologicalRoot.set(etymologicalRoot);
    }

    public String getComment() {
        return comment.get();
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public String getContext() {
        return context.get();
    }

    public StringProperty contextProperty() {
        return context;
    }

    public void setContext(String context) {
        this.context.set(context);
    }

    public String getRoot() {
        return root.get();
    }

    public StringProperty rootProperty() {
        return root;
    }

    public void setRoot(String root) {
        this.root.set(root);
    }

    public ObservableList<String> getVariantTypes() {
        return variantTypes;
    }

    public void setVariantTypes(ObservableList<String> variantTypes) {
        this.variantTypes = variantTypes;
    }

    public VariantType getVariantType() {
        return variantType.get();
    }

    public ObjectProperty<VariantType> variantTypeProperty() {
        return variantType;
    }

    public void setVariantType(VariantType variantType) {
        this.variantType.set(variantType);
    }

    public String getSelectedVariantType() {
        return selectedVariantType.get();
    }

    public StringProperty selectedVariantTypeProperty() {
        return selectedVariantType;
    }

    public void setSelectedVariantType(String selectedVariantType) {
        this.selectedVariantType.set(selectedVariantType);
    }

    public ObservableList<String> getAges() {
        return ages;
    }

    public void setAges(ObservableList<String> ages) {
        this.ages = ages;
    }

    public Dictionary getAge() {
        return age.get();
    }

    public ObjectProperty<Dictionary> ageProperty() {
        return age;
    }

    public void setAge(Dictionary age) {
        this.age.set(age);
    }

    public String getSelectedAge() {
        return selectedAge.get();
    }

    public StringProperty selectedAgeProperty() {
        return selectedAge;
    }

    public void setSelectedAge(String selectedAge) {
        this.selectedAge.set(selectedAge);
    }

    public ObservableList<String> getGrammaticalGenders() {
        return grammaticalGenders;
    }

    public void setGrammaticalGenders(ObservableList<String> grammaticalGenders) {
        this.grammaticalGenders = grammaticalGenders;
    }

    public Dictionary getGrammaticalGender() {
        return grammaticalGender.get();
    }

    public ObjectProperty<Dictionary> grammaticalGenderProperty() {
        return grammaticalGender;
    }

    public void setGrammaticalGender(Dictionary grammaticalGender) {
        this.grammaticalGender.set(grammaticalGender);
    }

    public String getSelectedGrammaticalGender() {
        return selectedGrammaticalGender.get();
    }

    public StringProperty selectedGrammaticalGenderProperty() {
        return selectedGrammaticalGender;
    }

    public void setSelectedGrammaticalGender(String selectedGrammaticalGender) {
        this.selectedGrammaticalGender.set(selectedGrammaticalGender);
    }

    public ObservableList<String> getSemanticFields() {
        return semanticFields;
    }

    public void setSemanticFields(ObservableList<String> semanticFields) {
        this.semanticFields = semanticFields;
    }

    public Dictionary getSemanticField() {
        return semanticField.get();
    }

    public ObjectProperty<Dictionary> semanticFieldProperty() {
        return semanticField;
    }

    public void setSemanticField(Dictionary semanticField) {
        this.semanticField.set(semanticField);
    }

    public String getSelectedSemanticField() {
        return selectedSemanticField.get();
    }

    public StringProperty selectedSemanticFieldProperty() {
        return selectedSemanticField;
    }

    public void setSelectedSemanticField(String selectedSemanticField) {
        this.selectedSemanticField.set(selectedSemanticField);
    }

    public ObservableList<String> getSemanticFieldMods() {
        return semanticFieldMods;
    }

    public void setSemanticFieldMods(ObservableList<String> semanticFieldMods) {
        this.semanticFieldMods = semanticFieldMods;
    }

    public Dictionary getSemanticFieldMod() {
        return semanticFieldMod.get();
    }

    public ObjectProperty<Dictionary> semanticFieldModProperty() {
        return semanticFieldMod;
    }

    public void setSemanticFieldMod(Dictionary semanticFieldMod) {
        this.semanticFieldMod.set(semanticFieldMod);
    }

    public String getSelectedSemanticFieldMod() {
        return selectedSemanticFieldMod.get();
    }

    public StringProperty selectedSemanticFieldModProperty() {
        return selectedSemanticFieldMod;
    }

    public void setSelectedSemanticFieldMod(String selectedSemanticFieldMod) {
        this.selectedSemanticFieldMod.set(selectedSemanticFieldMod);
    }

    public ObservableList<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(ObservableList<String> statuses) {
        this.statuses = statuses;
    }

    public Dictionary getStatus() {
        return status.get();
    }

    public ObjectProperty<Dictionary> statusProperty() {
        return status;
    }

    public void setStatus(Dictionary status) {
        this.status.set(status);
    }

    public String getSelectedStatus() {
        return selectedStatus.get();
    }

    public StringProperty selectedStatusProperty() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus.set(selectedStatus);
    }

    public ObservableList<String> getStyles() {
        return styles;
    }

    public void setStyles(ObservableList<String> styles) {
        this.styles = styles;
    }

    public Dictionary getStyle() {
        return style.get();
    }

    public ObjectProperty<Dictionary> styleProperty() {
        return style;
    }

    public void setStyle(Dictionary style) {
        this.style.set(style);
    }

    public String getSelectedStyle() {
        return selectedStyle.get();
    }

    public StringProperty selectedStyleProperty() {
        return selectedStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle.set(selectedStyle);
    }

    public ObservableList<String> getSources() {
        return sources;
    }

    public void setSources(ObservableList<String> sources) {
        this.sources = sources;
    }

    public Dictionary getSource() {
        return source.get();
    }

    public ObjectProperty<Dictionary> sourceProperty() {
        return source;
    }

    public void setSource(Dictionary source) {
        this.source.set(source);
    }

    public String getSelectedSource() {
        return selectedSource.get();
    }

    public StringProperty selectedSourceProperty() {
        return selectedSource;
    }

    public void setSelectedSource(String selectedSource) {
        this.selectedSource.set(selectedSource);
    }

    public ObservableList<String> getLexicalCharacteristics() {
        return lexicalCharacteristics;
    }

    public void setLexicalCharacteristics(ObservableList<String> lexicalCharacteristics) {
        this.lexicalCharacteristics = lexicalCharacteristics;
    }

    public Dictionary getLexicalCharacteristic() {
        return lexicalCharacteristic.get();
    }

    public ObjectProperty<Dictionary> lexicalCharacteristicProperty() {
        return lexicalCharacteristic;
    }

    public void setLexicalCharacteristic(Dictionary lexicalCharacteristic) {
        this.lexicalCharacteristic.set(lexicalCharacteristic);
    }

    public String getSelectedLexicalCharacteristic() {
        return selectedLexicalCharacteristic.get();
    }

    public StringProperty selectedLexicalCharacteristicProperty() {
        return selectedLexicalCharacteristic;
    }

    public void setSelectedLexicalCharacteristic(String selectedLexicalCharacteristic) {
        this.selectedLexicalCharacteristic.set(selectedLexicalCharacteristic);
    }

    public ItemList<VariantType> getVariantTypeItemList() {
        return variantTypeItemList;
    }

    public void setVariantTypeItemList(ItemList<VariantType> variantTypeItemList) {
        this.variantTypeItemList = variantTypeItemList;
    }

    public ItemList<Dictionary> getAgeItemList() {
        return ageItemList;
    }

    public void setAgeItemList(ItemList<Dictionary> ageItemList) {
        this.ageItemList = ageItemList;
    }

    public ItemList<Dictionary> getGrammaticalGenderItemList() {
        return grammaticalGenderItemList;
    }

    public void setGrammaticalGenderItemList(ItemList<Dictionary> grammaticalGenderItemList) {
        this.grammaticalGenderItemList = grammaticalGenderItemList;
    }

    public ItemList<Dictionary> getSemanticFieldItemList() {
        return semanticFieldItemList;
    }

    public void setSemanticFieldItemList(ItemList<Dictionary> semanticFieldItemList) {
        this.semanticFieldItemList = semanticFieldItemList;
    }

    public ItemList<Dictionary> getSemanticFieldModItemList() {
        return semanticFieldModItemList;
    }

    public void setSemanticFieldModItemList(ItemList<Dictionary> semanticFieldModItemList) {
        this.semanticFieldModItemList = semanticFieldModItemList;
    }

    public ItemList<Dictionary> getStatusesItemList() {
        return statusesItemList;
    }

    public void setStatusesItemList(ItemList<Dictionary> statusesItemList) {
        this.statusesItemList = statusesItemList;
    }

    public ItemList<Dictionary> getStylesItemList() {
        return stylesItemList;
    }

    public void setStylesItemList(ItemList<Dictionary> stylesItemList) {
        this.stylesItemList = stylesItemList;
    }

    public ItemList<Dictionary> getSourcesItemList() {
        return sourcesItemList;
    }

    public void setSourcesItemList(ItemList<Dictionary> sourcesItemList) {
        this.sourcesItemList = sourcesItemList;
    }

    public ItemList<Dictionary> getLexicalCharacteristicsItemList() {
        return lexicalCharacteristicsItemList;
    }

    public void setLexicalCharacteristicsItemList(ItemList<Dictionary> lexicalCharacteristicsItemList) {
        this.lexicalCharacteristicsItemList = lexicalCharacteristicsItemList;
    }

    public ObservableList<String> getInflectionsCmb() {
        return inflectionsCmb;
    }

    public void setInflectionsCmb(ObservableList<String> inflectionsCmb) {
        this.inflectionsCmb = inflectionsCmb;
    }

    public Dictionary getInflectionCmb() {
        return inflectionCmb.get();
    }

    public ObjectProperty<Dictionary> inflectionCmbProperty() {
        return inflectionCmb;
    }

    public void setInflectionCmb(Dictionary inflectionCmb) {
        this.inflectionCmb.set(inflectionCmb);
    }

    public String getSelectedInflectionCmb() {
        return selectedInflectionCmb.get();
    }

    public StringProperty selectedInflectionCmbProperty() {
        return selectedInflectionCmb;
    }

    public void setSelectedInflectionCmb(String selectedInflectionCmb) {
        this.selectedInflectionCmb.set(selectedInflectionCmb);
    }

    public ItemList<Dictionary> getInflectionsItemList() {
        return inflectionsItemList;
    }

    public void setInflectionsItemList(ItemList<Dictionary> inflectionsItemList) {
        this.inflectionsItemList = inflectionsItemList;
    }

    public DictionaryListItemViewModel getSelectedSourceListItem() {
        return selectedSourceListItem.get();
    }

    public ObjectProperty<DictionaryListItemViewModel> selectedSourceListItemProperty() {
        return selectedSourceListItem;
    }

    public void setSelectedSourceListItem(DictionaryListItemViewModel selectedSourceListItem) {
        this.selectedSourceListItem.set(selectedSourceListItem);
    }

    public ObservableList<SemanticFieldListItemViewModel> getSemanticFiledList() {
        return semanticFiledList;
    }

    public void setSemanticFiledList(ObservableList<SemanticFieldListItemViewModel> semanticFiledList) {
        this.semanticFiledList = semanticFiledList;
    }

    public SemanticFieldListItemViewModel getSelectedSemanticFieldListItem() {
        return selectedSemanticFieldListItem.get();
    }

    public ObjectProperty<SemanticFieldListItemViewModel> selectedSemanticFieldListItemProperty() {
        return selectedSemanticFieldListItem;
    }

    public void setSelectedSemanticFieldListItem(SemanticFieldListItemViewModel selectedSemanticFieldListItem) {
        this.selectedSemanticFieldListItem.set(selectedSemanticFieldListItem);
    }

    public ObservableList<InflectionListItemViewModel> getInflectionFiledList() {
        return inflectionFiledList;
    }

    public void setInflectionFiledList(ObservableList<InflectionListItemViewModel> inflectionFiledList) {
        this.inflectionFiledList = inflectionFiledList;
    }

    public InflectionListItemViewModel getSelectedInflectionListItem() {
        return selectedInflectionListItem.get();
    }

    public ObjectProperty<InflectionListItemViewModel> selectedInflectionListItemProperty() {
        return selectedInflectionListItem;
    }

    public void setSelectedInflectionListItem(InflectionListItemViewModel selectedInflectionListItem) {
        this.selectedInflectionListItem.set(selectedInflectionListItem);
    }

    public ObservableList<ParticleListItemViewModel> getParticleList() {
        return particleList;
    }

    public void setParticleList(ObservableList<ParticleListItemViewModel> particleList) {
        this.particleList = particleList;
    }

    public ParticleListItemViewModel getSelectedParticleListItem() {
        return selectedParticleListItem.get();
    }

    public ObjectProperty<ParticleListItemViewModel> selectedParticleListItemProperty() {
        return selectedParticleListItem;
    }

    public void setSelectedParticleListItem(ParticleListItemViewModel selectedParticleListItem) {
        this.selectedParticleListItem.set(selectedParticleListItem);
    }

    public ObservableList<TranscriptionListItemViewModel> getTranscriptionList() {
        return transcriptionList;
    }

    public void setTranscriptionList(ObservableList<TranscriptionListItemViewModel> transcriptionList) {
        this.transcriptionList = transcriptionList;
    }

    public TranscriptionListItemViewModel getSelectedTranscriptionListItem() {
        return selectedTranscriptionListItem.get();
    }

    public ObjectProperty<TranscriptionListItemViewModel> selectedTranscriptionListItemProperty() {
        return selectedTranscriptionListItem;
    }

    public void setSelectedTranscriptionListItem(TranscriptionListItemViewModel selectedTranscriptionListItem) {
        this.selectedTranscriptionListItem.set(selectedTranscriptionListItem);
    }

    public Command getAddVariantCommand() {
        return addVariantCommand;
    }

    public void setAddVariantCommand(Command addVariantCommand) {
        this.addVariantCommand = addVariantCommand;
    }

    public Command getRemoveVariantCommand() {
        return removeVariantCommand;
    }

    public void setRemoveVariantCommand(Command removeVariantCommand) {
        this.removeVariantCommand = removeVariantCommand;
    }

    public Command getAddTranscriptionCommand() {
        return addTranscriptionCommand;
    }

    public void setAddTranscriptionCommand(Command addTranscriptionCommand) {
        this.addTranscriptionCommand = addTranscriptionCommand;
    }

    public Command getRemoveTranscriptionCommand() {
        return removeTranscriptionCommand;
    }

    public void setRemoveTranscriptionCommand(Command removeTranscriptionCommand) {
        this.removeTranscriptionCommand = removeTranscriptionCommand;
    }

    public Command getAddInflectionCommand() {
        return addInflectionCommand;
    }

    public void setAddInflectionCommand(Command addInflectionCommand) {
        this.addInflectionCommand = addInflectionCommand;
    }

    public Command getRemoveInflectionCommand() {
        return removeInflectionCommand;
    }

    public void setRemoveInflectionCommand(Command removeInflectionCommand) {
        this.removeInflectionCommand = removeInflectionCommand;
    }

    public Command getAddSemanticFieldCommand() {
        return addSemanticFieldCommand;
    }

    public void setAddSemanticFieldCommand(Command addSemanticFieldCommand) {
        this.addSemanticFieldCommand = addSemanticFieldCommand;
    }

    public Command getRemoveSemanticFieldCommand() {
        return removeSemanticFieldCommand;
    }

    public void setRemoveSemanticFieldCommand(Command removeSemanticFieldCommand) {
        this.removeSemanticFieldCommand = removeSemanticFieldCommand;
    }

    public Command getAddSourceCommand() {
        return addSourceCommand;
    }

    public void setAddSourceCommand(Command addSourceCommand) {
        this.addSourceCommand = addSourceCommand;
    }

    public Command getRemoveSourceCommand() {
        return removeSourceCommand;
    }

    public void setRemoveSourceCommand(Command removeSourceCommand) {
        this.removeSourceCommand = removeSourceCommand;
    }

    public Command getAddParticleCommand() {
        return addParticleCommand;
    }

    public void setAddParticleCommand(Command addParticleCommand) {
        this.addParticleCommand = addParticleCommand;
    }

    public Command getRemoveParticleCommand() {
        return removeParticleCommand;
    }

    public void setRemoveParticleCommand(Command removeParticleCommand) {
        this.removeParticleCommand = removeParticleCommand;
    }
}

