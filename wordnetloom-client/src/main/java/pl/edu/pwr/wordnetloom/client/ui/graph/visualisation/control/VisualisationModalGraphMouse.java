package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import edu.uci.ics.jung.visualization.control.*;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

import java.awt.event.ItemEvent;

public final class VisualisationModalGraphMouse extends AbstractModalGraphMouse
        implements ModalGraphMouse, ItemSelectable {

    @Inject
    VisualisationPopupGraphMousePlugin popupPlugin;

    @Inject
    PickingGraphMousePlugin pickingPlugin;

    @Inject
    TranslatingGraphMousePlugin translatingPlugin;

    GraphController graphController;

    public VisualisationModalGraphMouse(){
        super(1.1f, 1 / 1.1f);
    }

    public void setGraphController(GraphController gc){
        this.graphController = gc;
    }

    @Override
    public void loadPlugins() {

        scalingPlugin = new ScalingGraphMousePlugin(new ViewScalingControl(), 0, in, out);
        popupPlugin.setGraphController(graphController);

        add(scalingPlugin);
        add(popupPlugin);

        setUniversalMode();
    }

    /**
     * setter for the Mode.
     *
     * @param mode
     */
    @Override
    public void setMode(Mode mode) {
        if (this.mode != mode) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                    this.mode, ItemEvent.DESELECTED));
            this.mode = mode;
            switch (mode) {
                case TRANSFORMING:
                    setTransformingMode();
                    break;
                case PICKING:
                    setPickingMode();
                    break;
            }
            if (modeBox != null) {
                modeBox.setSelectedItem(mode);
            }
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
        }
    }

    @Override
    protected void setPickingMode() {
        remove(translatingPlugin);
        add(pickingPlugin);
    }

    @Override
    protected void setTransformingMode() {
        remove(pickingPlugin);
        add(translatingPlugin);
    }

    /**
     * special mouse mode
     */
    public void setUniversalMode() {
        add(pickingPlugin);
        add(translatingPlugin);
    }

    /**
     * @return the modeBox.
     */
    @Override
    public JComboBox getModeComboBox() {
        if (modeBox == null) {
            modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING, Mode.EDITING, Mode.ANNOTATING});
            modeBox.addItemListener(getModeListener());
        }
        modeBox.setSelectedItem(mode);
        return modeBox;
    }

}
