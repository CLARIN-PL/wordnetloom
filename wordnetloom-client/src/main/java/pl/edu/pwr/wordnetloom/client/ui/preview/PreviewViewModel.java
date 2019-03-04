package pl.edu.pwr.wordnetloom.client.ui.preview;

import de.saxsys.mvvmfx.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreviewViewModel implements ViewModel {

    @Inject
    SatelliteGraphController satelliteGraphController;

    public SatelliteGraphController getSatelliteController() {
        return satelliteGraphController;
    }
}
