package pl.edu.pwr.wordnetloom.client.ui.searchsenseform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.Validator;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import pl.edu.pwr.wordnetloom.client.model.SearchFilter;
import pl.edu.pwr.wordnetloom.client.model.SearchList;
import pl.edu.pwr.wordnetloom.client.model.SearchListItem;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynonymyRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemViewModel;

import javax.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchSenseFormViewModel implements ViewModel {

    @Inject
    RemoteService remoteService;

    @InjectScope
    SenseRelationDialogScope senseRelationDialogScope;

    @InjectScope
    SynonymyRelationDialogScope synonymyRelationDialogScope;

    private final StringProperty searchTextFiled = new SimpleStringProperty();
    private final BooleanProperty progressOverlay = new SimpleBooleanProperty();
    private final BooleanProperty onlySensesWithoutSynset = new SimpleBooleanProperty(false);

    private ObservableList<SearchListItemViewModel> searchList = FXCollections.observableArrayList();
    private ObjectProperty<SearchListItemViewModel> selectedSearchListItem = new SimpleObjectProperty<>();

    private Command scrollCommand;
    private Command searchCommand;

    private final int SEARCH_LIMIT = 100;
    private int searchLimit = SEARCH_LIMIT;

    private SearchFilter filter = new SearchFilter();

    private final CompositeValidator formValidator = new CompositeValidator();

    private Validator selectedSenseValidator;

    public SearchSenseFormViewModel() {
        selectedSenseValidator =  new FunctionBasedValidator<>(
                selectedSearchListItemProperty(),
                Objects::nonNull,
                ValidationMessage.error("Sense must be selected")
        );

        formValidator.addValidators(
                selectedSenseValidator
        );
    }

    public void initialize() {
        senseRelationDialogScope.subscribe(SenseRelationDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        synonymyRelationDialogScope.subscribe(SynonymyRelationDialogScope.RESET_FORMS, (key, payload) -> resetForm());

        scrollCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                SearchFilter filterCopy = filter.clone();
                load(filterCopy);
            }
        });

        searchCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                search();
            }
        });

        selectedSearchListItem.addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                commitChanges();
            }
        });
        senseRelationDialogScope.senseSearchFormValidProperty()
                .bind(formValidator.getValidationStatus().validProperty());
        synonymyRelationDialogScope.senseSearchFormValidProperty()
                .bind(formValidator.getValidationStatus().validProperty());
    }

    private void resetForm() {
        senseRelationDialogScope.senseSearchFormValidProperty().setValue(false);
        synonymyRelationDialogScope.senseSearchFormValidProperty().setValue(false);
        searchTextFiled.set(null);
        searchList.clear();
        senseRelationDialogScope.setChildSense(null);
        synonymyRelationDialogScope.setSense(null);
    }

    public synchronized void load(SearchFilter searchFilter) {
        progressOverlay.setValue(true);

        Task listLoader = new Task<SearchList>() {

            {
                setOnSucceeded(workerStateEvent -> {
                    List<SearchListItem> items = getValue().getRows();
                    int startIndex = getValue().getStart();
                    int limit = getValue().getLimit();
                    for (int i = startIndex; i < startIndex + limit; i++) {
                        if (i < searchList.size()) {
                            searchList.remove(i, i + 1);
                            searchList.add(i, new SearchListItemViewModel(remoteService,
                                    items.get(i - startIndex), SearchListItemViewModel.Type.SENSE, null));
                        }
                    }
                    progressOverlay.setValue(false);
                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected SearchList call(){
                searchFilter.setLimit(searchLimit);
                return remoteService.search(searchFilter);
            }
        };
        Thread loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void commitChanges() {
        Sense sense = remoteService.findSense(selectedSearchListItem.get().getSearchListItem().getLinks().getSelf());
        senseRelationDialogScope.setChildSense(sense);
        synonymyRelationDialogScope.setSense(sense);
        senseRelationDialogScope.publish(SenseRelationDialogScope.REFRESH_SENSES);
        synonymyRelationDialogScope.publish(SynonymyRelationDialogScope.REFRESH_SENSES);
    }

    private void search() {

        searchList.clear();
        progressOverlay.setValue(true);

        Task listLoader = new Task<SearchList>() {
            {
                setOnSucceeded(workerStateEvent -> {

                    searchList.addAll(getValue().getRows()
                            .stream()
                            .map(i -> new SearchListItemViewModel(remoteService, i, SearchListItemViewModel.Type.SENSE, null))
                            .collect(Collectors.toList()));

                    int insertedItems = searchList.size();
                    int allItems = getValue().getSize();

                    IntStream.range(0, allItems - insertedItems)
                            .forEach(i -> searchList.add(
                                    new SearchListItemViewModel(remoteService, new SearchListItem(""),SearchListItemViewModel.Type.SENSE, null)));

                    progressOverlay.setValue(false); // stop displaying the loading indicator
                });

                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected SearchList call() {
                filter.setLimit(searchLimit);
                filter.setStart(0);
                filter.setSynsetMode(false);
                filter.setLemma(searchTextFiled.get());
                filter.setSensesWithoutSynset(onlySensesWithoutSynset.get());
                return remoteService.search(filter);
            }
        };

        Thread loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    public void setSearchLimit(int limit){
        searchLimit = limit;
    }

    public Property<Boolean> progressOverlayProperty() {
        return progressOverlay;
    }
    public Property<String> searchTextProperty() {
        return searchTextFiled;
    }
    public ObservableList<SearchListItemViewModel> searchListProperty() {
        return searchList;
    }
    public ObjectProperty<SearchListItemViewModel> selectedSearchListItemProperty() {
        return selectedSearchListItem;
    }

    public Command getScrollCommand(int start, int limit) {
        filter.setStart(start);
        filter.setLimit(limit);
        return scrollCommand;
    }

    public Command getSearchCommand(int limit) {
        filter.setLimit(limit);
        return searchCommand;
    }

    public Property<Boolean> onlySensesWithoutSynsetProperty() {
        return this.onlySensesWithoutSynset;
    }
}

