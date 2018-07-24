package pl.edu.pwr.wordnetloom.client.plugins.lexeditor.frames;

import com.alee.laf.rootpane.WebFrame;
import pl.edu.pwr.wordnetloom.client.plugins.lexeditor.panel.EmotionsPropertiesPanel;
import pl.edu.pwr.wordnetloom.client.plugins.lexeditor.panel.LexicalUnitPropertiesPanel;
import pl.edu.pwr.wordnetloom.client.systems.ui.DialogWindow;
import pl.edu.pwr.wordnetloom.client.systems.ui.MButton;
import pl.edu.pwr.wordnetloom.client.utils.Labels;
import pl.edu.pwr.wordnetloom.client.workbench.interfaces.Workbench;
import pl.edu.pwr.wordnetloom.sense.model.Sense;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;

public class LexicalUnitPropertiesFrame extends DialogWindow{

    private JTabbedPane tabbedPane;
    private LexicalUnitPropertiesPanel lexicalUnitPropertiesPanel;
    private EmotionsPropertiesPanel emotionsPropertiesPanel;

    public LexicalUnitPropertiesFrame(WebFrame baseFrame, String title, int x, int y, int width, int height) {
        super(baseFrame, title, x, y, width, height);
        initComponents(baseFrame);
        createView();
    }

    private void createView()
    {
        setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        JButton saveButton = MButton.buildSaveButton()
                .withActionListener(e->save());
        JButton cancelButton = MButton.buildCancelButton()
                .withActionListener(e->cancel());

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void save() {
        if(tabbedPane.getSelectedComponent().equals(lexicalUnitPropertiesPanel)) {
            lexicalUnitPropertiesPanel.save();
        } else {
            emotionsPropertiesPanel.save();
        }
    }

    private void cancel() {
        // TODO można jeszcze wyświtlić komunikat, czy na pewno zamknąć okno
        // można zrobić sprawdzanie, czy
        setVisible(false);
    }

    private void initComponents(WebFrame frame) {
        tabbedPane = new JTabbedPane();
        lexicalUnitPropertiesPanel = new LexicalUnitPropertiesPanel(frame);
        emotionsPropertiesPanel = new EmotionsPropertiesPanel(frame);

        tabbedPane.add(lexicalUnitPropertiesPanel, Labels.PROPERTIES);
        tabbedPane.add(emotionsPropertiesPanel, Labels.EMOTIONS);
    }

    private void loadUnit(Sense unit) {
        lexicalUnitPropertiesPanel.setSense(unit);
        emotionsPropertiesPanel.load(unit);
    }

    public static void showModal(Workbench workbench, Sense unit) {
        final int WIDTH = 600;
        final int HEIGHT = 680;
        final String TITLE = Labels.UNIT_PROPERTIES;
        int x = workbench.getFrame().getX();
        int y = workbench.getFrame().getY();
        LexicalUnitPropertiesFrame frame = new LexicalUnitPropertiesFrame(workbench.getFrame(),TITLE, x, y, WIDTH, HEIGHT);
        frame.loadUnit(unit);
        frame.setVisible(true);
    }
}
