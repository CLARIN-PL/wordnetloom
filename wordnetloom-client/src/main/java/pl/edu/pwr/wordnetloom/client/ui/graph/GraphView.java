package pl.edu.pwr.wordnetloom.client.ui.graph;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GraphView implements FxmlView<GraphViewModel> {

    @InjectViewModel
    private GraphViewModel viewModel;

    public final static String SELECT_LAST_TAB = "select_last_tab";

    @InjectContext
    Context context;

    @FXML
    private TabPane tabs;

    public void initialize() {
        viewModel.subscribe(SELECT_LAST_TAB,(s, objects) -> {
            tabs.getSelectionModel().selectLast();
        });

        ObservableList<Tab> list = tabs.getTabs();
        viewModel.merge(list, viewModel.graphTabsList());
        viewModel.selectedGraphTabProperty().bind(tabs.getSelectionModel().selectedItemProperty());

        tabs.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tabs.requestLayout();
                tabs.requestFocus();
            }
        });
    }


}
