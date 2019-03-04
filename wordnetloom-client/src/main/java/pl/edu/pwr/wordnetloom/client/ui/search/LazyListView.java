package pl.edu.pwr.wordnetloom.client.ui.search;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.*;

// TODO add comments
public class LazyListView<T> extends ListView<T>{

    private final int DELAY = 500;

    public interface LoadListener{
        void onScroll(int startIndex, int limit);
    }

    private ObservableList<T> listItems;

    private int limit = 100;
    private int delay = DELAY;
    private LoadListener listener;
    private Set<Integer> loaded;
    private Set<Integer> events;
    private boolean scrollerInitialize = false;

    public LazyListView(){
        super();
        loaded = new HashSet<>();
        events = new HashSet<>();

        this.addEventFilter(ScrollEvent.ANY, event -> onScrollEvent());
    }

    public void setListItems(ObservableList<T> list){
        setItems(list);
        listItems = list;
        addObservableListListener(list);
        loaded.add(0);
    }

    private void addObservableListListener(ObservableList<T> list){
        list.addListener((ListChangeListener<T>) c -> {
            while(c.next()){
                if((c.wasReplaced() || c.wasAdded()) && !scrollerInitialize){
                    scrollerInitialize = initScrollBarListener();
                }
            }
        });
    }

    private boolean initScrollBarListener(){
        for(Node node : this.lookupAll(".scroll-bar:vertical")){
            if(node instanceof ScrollBar){
                final ScrollBar scrollBar = (ScrollBar) node;
                scrollBar.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> onScrollEvent());
                return true;
            }
        }
        return false;
    }

    private void onScrollEvent() {
        if(listener != null){
            tryStartLoading();
        }
    }

    private void tryStartLoading(){
        Set<Integer> visiblePages = getVisiblePages();
        for(Integer page : visiblePages){
            if(canLoadPage(page)){
                startEvent(page);
            }
        }
    }

    private int getPage(int index) {
        assert limit != 0;
        int page =  index/limit;
        return page >= 0 ? page : 0;
    }

    private boolean canLoadPage(int page) {
        return !loaded.contains(page) && !events.contains(page);
    }

    private void startEvent(int page){
        assert listener != null;
        events.add(page);
        int startIndex = page * limit;
        listener.onScroll(startIndex, limit);
        loaded.add(page);
        events.remove(page);
    }

    private Set<Integer> getVisiblePages() {
        final int PADDING = 10;
        Set<Integer> visiblePages = new HashSet<>();
        final ListViewSkin<?> viewSkin = (ListViewSkin<?>) this.getSkin();
        final VirtualFlow<?> virtualFlow = (VirtualFlow<?>) viewSkin.getChildren().get(0);
        if(virtualFlow == null || virtualFlow.getFirstVisibleCell() == null){
            return visiblePages;
        }
        int first = virtualFlow.getFirstVisibleCell().getIndex();
        int last = virtualFlow.getLastVisibleCell().getIndex();
        int firstPage = getPage(first - PADDING);
        int lastPage = getPage(last + PADDING);

        for(int page = firstPage; page < lastPage + 1; page++){
            visiblePages.add(page);
        }
        return visiblePages;
    }

    public void reset(){
        getItems().clear();
        loaded.clear();
        events.clear();
        loaded.add(0);

        scrollerInitialize = false;
    }

    public void setDelay(int delay){
        this.delay = delay;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }

    public void setLoadListener(LoadListener listener){
        this.listener = listener;
    }

    public int getLimit(){
        return limit;
    }
}
