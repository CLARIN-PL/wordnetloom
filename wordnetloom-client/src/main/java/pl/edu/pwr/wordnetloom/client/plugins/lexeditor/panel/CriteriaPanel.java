package pl.edu.pwr.wordnetloom.client.plugins.lexeditor.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import pl.edu.pwr.wordnetloom.client.systems.enums.RelationTypes;
import pl.edu.pwr.wordnetloom.client.systems.misc.CustomDescription;
import pl.edu.pwr.wordnetloom.client.systems.ui.ComboBoxPlain;
import pl.edu.pwr.wordnetloom.client.systems.ui.DomainComboBox;
import pl.edu.pwr.wordnetloom.client.systems.ui.LabelExt;
import pl.edu.pwr.wordnetloom.client.systems.ui.LexiconComboBox;
import pl.edu.pwr.wordnetloom.client.systems.ui.PartOfSpeechComboBox;
import pl.edu.pwr.wordnetloom.client.systems.ui.TextFieldPlain;
import pl.edu.pwr.wordnetloom.client.utils.Labels;
import pl.edu.pwr.wordnetloom.domain.model.Domain;
import pl.edu.pwr.wordnetloom.lexicon.model.Lexicon;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;
import pl.edu.pwr.wordnetloom.relationtype.model.SenseRelationType;
import pl.edu.pwr.wordnetloom.relationtype.model.SynsetRelationType;
import se.datadosen.component.RiverLayout;

public abstract class CriteriaPanel extends JPanel {

    private static final long serialVersionUID = 4649824763750406980L;
    public static final String STANDARD_VALUE_FILTER = "";
    private int SCROLL_PANE_HEIGHT = 400;
    public static final int MAX_ITEMS_COUNT = 500;

    private JTextField searchTextField;
    private LexiconComboBox lexiconComboBox;
    private DomainComboBox domainComboBox;
    private PartOfSpeechComboBox partsOfSpeachComboBox;
    private ComboBoxPlain<SynsetRelationType> synsetRelationsComboBox;
    private ComboBoxPlain<SenseRelationType> senseRelationsComboBox;
    private JCheckBox limitResultCheckBox;

    public CriteriaPanel(int scrollHeight) {
        this.SCROLL_PANE_HEIGHT = scrollHeight;
        initialize();
    }

    private void initialize() {
        setLayout(new RiverLayout());
        setMaximumSize(new Dimension(0, SCROLL_PANE_HEIGHT));
        setMinimumSize(new Dimension(0, SCROLL_PANE_HEIGHT));
        setPreferredSize(new Dimension(0, SCROLL_PANE_HEIGHT));

        lexiconComboBox = new LexiconComboBox(Labels.VALUE_ALL);
        lexiconComboBox.setPreferredSize(new Dimension(150, 20));
        lexiconComboBox.addActionListener((ActionEvent e) -> {
            Lexicon lex = lexiconComboBox.retriveComboBoxItem();
            if (lex != null) {
                domainComboBox.filterDomainsByLexicon(lex, true);
            } else {
                domainComboBox.allDomains(true);
            }
            refreshRelations();
        });

        searchTextField = new TextFieldPlain(STANDARD_VALUE_FILTER);

        partsOfSpeachComboBox = new PartOfSpeechComboBox(Labels.VALUE_ALL);
        partsOfSpeachComboBox.showUbyItems();
        partsOfSpeachComboBox.setPreferredSize(new Dimension(150, 20));
        partsOfSpeachComboBox.addItemListener((ItemEvent e) -> {
            PartOfSpeech pos = partsOfSpeachComboBox.retriveComboBoxItem();
            Lexicon lex = lexiconComboBox.retriveComboBoxItem();
            if (pos != null && lex != null) {
                domainComboBox.filterDomainByUbyPosAndLexcion(pos, lex, true);
            } else if (lex != null && pos == null) {
                domainComboBox.filterDomainsByLexicon(lex, true);
            } else if (pos != null && lex == null) {
                domainComboBox.filterDomainByUbyPos(pos, true);
            } else {
                domainComboBox.allDomains(true);
            }
        });

        domainComboBox = new DomainComboBox(Labels.VALUE_ALL);
        domainComboBox.allDomains(true);
        domainComboBox.setPreferredSize(new Dimension(150, 20));

        synsetRelationsComboBox = createSynsetRelationsComboBox();
        synsetRelationsComboBox.setPreferredSize(new Dimension(150, 20));

        senseRelationsComboBox = createSenseRelationsComboBox();
        senseRelationsComboBox.setPreferredSize(new Dimension(150, 20));

        limitResultCheckBox = createLimitResultSearch();

    }

    protected abstract void initializeFormPanel();

    public abstract CriteriaDTO getCriteria();

    public abstract void restoreCriteria(CriteriaDTO criteria);

    protected void addLimit() {
        add("br left", limitResultCheckBox);
    }

    protected void addSynsetRelationTypes() {
        add("br", new LabelExt(Labels.RELATIONS_COLON, 'r', synsetRelationsComboBox));
        add("br hfill", synsetRelationsComboBox);
        refreshRelations();
    }

    protected void addSenseRelationTypes() {
        add("br", new LabelExt(Labels.RELATIONS_COLON, 'r', senseRelationsComboBox));
        add("br hfill", senseRelationsComboBox);
        refreshRelations();
    }

    protected void addDomain() {
        add("br", new LabelExt(Labels.DOMAIN_COLON, 'd', domainComboBox));
        add("br hfill", domainComboBox);
    }

    protected void addPartsOfSpeach() {
        add("br", new LabelExt(Labels.PARTS_OF_SPEECH_COLON, 'm', partsOfSpeachComboBox));
        add("br hfill", partsOfSpeachComboBox);
    }

    protected void addLexicon() {
        add("br", new LabelExt(Labels.LEXICON_COLON, 'l', lexiconComboBox));
        add("br hfill", lexiconComboBox);
    }

    protected void addSearch() {
        add("", new LabelExt(Labels.SEARCH_COLON, 'w', searchTextField));
        add("br hfill", searchTextField);
    }

    private ComboBoxPlain<SynsetRelationType> createSynsetRelationsComboBox() {
        ComboBoxPlain<SynsetRelationType> combo = new ComboBoxPlain<>();
        combo.addItem(new CustomDescription<>(Labels.VALUE_ALL, null));
        combo.setPreferredSize(new Dimension(150, 20));
        return combo;
    }

    private ComboBoxPlain<SenseRelationType> createSenseRelationsComboBox() {
        ComboBoxPlain<SenseRelationType> combo = new ComboBoxPlain<>();
        combo.addItem(new CustomDescription<>(Labels.VALUE_ALL, null));
        combo.setPreferredSize(new Dimension(150, 20));
        return combo;
    }

    private JCheckBox createLimitResultSearch() {
        JCheckBox limitResult = new JCheckBox(String.format(Labels.LIMIT_TO, "" + MAX_ITEMS_COUNT));
        limitResult.setSelected(true);
        return limitResult;
    }

    public void refreshPartOfSpeech() {
        int selected = partsOfSpeachComboBox.getSelectedIndex();
        if (selected != -1) {
            partsOfSpeachComboBox.setSelectedIndex(selected);
        }
    }

    public void refreshDomain() {
        int selected = domainComboBox.getSelectedIndex();
        if (selected != -1) {
            domainComboBox.setSelectedIndex(selected);
        }
    }

    public void refreshRelations() {
        RelationTypes.refresh();
//        List<RelationType> relations = RemoteUtils.relationTypeRemote.dbGetLeafs(relationArgument, LexiconManager.getInstance().getLexicons());
//        int selected = relationsComboBox.getSelectedIndex();
//
//        relationsComboBox.removeAllItems();
//        relationsComboBox.addItem(new CustomDescription<>(Labels.VALUE_ALL, null));
//
//        if (lexiconComboBox.retriveComboBoxItem() != null) {
//            for (RelationType relation : relations) {
//                if (Objects.equals(relation.getLexicon().getId(), lexiconComboBox.retriveComboBoxItem().getId())) {
//                    RelationType currentRelation = RelationTypes.get(relation.getId()).getRelationType();
//                    relationsComboBox.addItem(new CustomDescription<>(RelationTypes.getFullNameFor(currentRelation.getId()), currentRelation));
//                }
//            }
//        } else {
//            for (RelationType relation : relations) {
//                RelationType currentRelation = RelationTypes.get(relation.getId()).getRelationType();
//                relationsComboBox.addItem(new CustomDescription<>(RelationTypes.getFullNameFor(currentRelation.getId()), currentRelation));
//            }
//        }
//
//        if (selected != -1) {
//            relationsComboBox.setSelectedIndex(selected);
//        }
    }

    public void resetFields() {
        searchTextField.setText("");
        domainComboBox.setSelectedIndex(0);
        partsOfSpeachComboBox.setSelectedIndex(0);
        synsetRelationsComboBox.setSelectedIndex(0);
        senseRelationsComboBox.setSelectedIndex(0);
        lexiconComboBox.setSelectedIndex(0);
    }

    public JTextField getSearchTextField() {
        return searchTextField;
    }

    public ComboBoxPlain<Domain> getDomainComboBox() {
        return domainComboBox;
    }

    public ComboBoxPlain<SynsetRelationType> getSynsetRelationTypeComboBox() {
        return synsetRelationsComboBox;
    }

    public ComboBoxPlain<SenseRelationType> getSenseRelationTypeComboBox() {
        return senseRelationsComboBox;
    }

    public JCheckBox getLimitResultCheckBox() {
        return limitResultCheckBox;
    }

    public LexiconComboBox getLexiconComboBox() {
        return lexiconComboBox;
    }

    public PartOfSpeechComboBox getPartsOfSpeachComboBox() {
        return partsOfSpeachComboBox;
    }

}
