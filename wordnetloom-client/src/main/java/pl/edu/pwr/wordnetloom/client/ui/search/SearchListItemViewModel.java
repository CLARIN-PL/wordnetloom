package pl.edu.pwr.wordnetloom.client.ui.search;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.image.Image;
import pl.edu.pwr.wordnetloom.client.config.ResourceProvider;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.model.SearchListItem;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.SenseTooltipCreator;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.SynsetTooltipCreator;

import javax.enterprise.event.Event;


public class SearchListItemViewModel implements ViewModel {

    public enum Type{
        SYNSET,
        SENSE
    }

    private RemoteService remoteService;

    private ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
    private ObjectProperty<Image> img = new SimpleObjectProperty<>();
    private SearchListItem searchListItem;

    Event<LoadGraphEvent> publisher;

    private Command openSynsetGraphInNewTabCommand;
    private Command openSenseGraphInNewTabCommand;

    private Type itemType;

    public SearchListItemViewModel(RemoteService remoteService,
                                   SearchListItem item,
                                   Type itemType,
                                   Event<LoadGraphEvent> publisher) {
        this.remoteService = remoteService;
        this.searchListItem = item;
        this.publisher = publisher;
        this.itemType = itemType;
        label.set(item.getLabel());
        img.set(ResourceProvider.getById(item.getLexicon()));

        openSynsetGraphInNewTabCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                if(publisher != null) {
                    publisher.fireAsync(new LoadGraphEvent(searchListItem.getLinks().getSynsetGraph(), true, false));
                }
            }
        });

        openSenseGraphInNewTabCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                if(publisher != null) {
                    publisher.fireAsync(new LoadGraphEvent(searchListItem.getLinks().getSenseGraph(), true,true));
                }
            }
        });
    }

    public SearchListItem getSearchListItem() {
        return searchListItem;
    }

    public ObservableStringValue labelProperty() {
        return label.getReadOnlyProperty();
    }

    public Command getOpenSenseGraphInNewTabCommand() {
        return openSenseGraphInNewTabCommand;
    }

    public Command getOpenSynsetGraphInNewTabCommand() {
        return openSynsetGraphInNewTabCommand;
    }

    public String getTooltipText(){
        if(itemType == Type.SENSE){
            return SenseTooltipCreator.create(searchListItem, remoteService);
        } else {
            return SynsetTooltipCreator.create(searchListItem, remoteService);
        }
    }

    public ObjectProperty<Image> imgProperty() {
        return img;
    }
}

