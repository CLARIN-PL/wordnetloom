package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Inject;

public class YiddishPropertiesFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";

    @Inject
    RemoteService service;

    @Inject
    YiddishPropertiesFormViewModel yiddishPropertiesFormViewModel;

    public void initialize(){

    }
}

