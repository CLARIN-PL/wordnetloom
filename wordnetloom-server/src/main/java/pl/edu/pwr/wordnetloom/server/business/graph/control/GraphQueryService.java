package pl.edu.pwr.wordnetloom.server.business.graph.control;

import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringsQueryService;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.*;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Stateless
public class GraphQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    SynsetQueryService synsetQueryService;

    @Inject
    SenseQueryService senseQueryService;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LocalisedStringsQueryService loc;

    @Inject
    UserFinder userFinder;

    @Inject
    LexiconQueryService lexiconQueryService;

    @Inject
    RelationTypeQueryService relationTypeQueryService;

    private static final int NUMBER_OF_EXPANDED_NODES = 4;
    private List<Object[]> fetchSynsetGraphNode(UUID id) {

        List<Long> lexicons  = userFinder.getCurrentUser()
                .map( u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        String query = "SELECT  ANY_VALUE(t.np),t.c,t.f,t.t,t.lemma,t.domain,t.pos,t.lex FROM (SELECT rt1.node_position AS position," +
                "BIN_TO_UUID(r1.child_synset_id) AS c, " +
                "CASE WHEN r1.child_synset_id is null THEN null ELSE rt1.short_display_text_id END AS f,"+
                "CASE WHEN r2.parent_synset_id is null THEN null ELSE rt2.short_display_text_id END AS t,"+
                "concat(w.word, ' ', s.variant) AS lemma, dom1.name_id AS domain," +
                "s.part_of_speech_id AS pos, s.lexicon_id AS lex FROM tbl_synset_relation r1 " +
                "LEFT JOIN tbl_relation_type rt1 ON r1.synset_relation_type_id = rt1.id " +
                "LEFT JOIN tbl_relation_type rt2 ON rt1.reverse_relation_type_id = rt2.id " +
                "LEFT JOIN tbl_synset_relation r2 ON (r2.child_synset_id = r1.parent_synset_id AND r2.parent_synset_id = r1.child_synset_id AND r2.synset_relation_type_id = rt1.reverse_relation_type_id) " +
                "LEFT JOIN tbl_sense s ON s.synset_id = r1.child_synset_id " +
                "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                "WHERE r1.parent_synset_id = ?1 AND s.synset_position = ?2 AND rt1.node_position != 'IGNORE' " +
                "UNION SELECT " +
                "CASE WHEN rt1.node_position  = 'LEFT' THEN IF(rt1.id = rt2.id,'LEFT','RIGHT') " +
                "WHEN rt1.node_position  = 'RIGHT' THEN IF(rt1.id = rt2.id,'RIGHT','LEFT') " +
                "WHEN rt1.node_position  = 'TOP' THEN 'BOTTOM' " +
                "WHEN rt1.node_position  = 'BOTTOM' THEN 'TOP' " +
                "WHEN rt1.node_position  = 'IGNORE' THEN 'IGNORE' END AS position," +
                "BIN_TO_UUID(r1.parent_synset_id) AS c, " +
                "CASE WHEN r2.child_synset_id is null THEN null ELSE rt2.short_display_text_id END AS f,"+
                "CASE WHEN r1.parent_synset_id is null THEN null ELSE rt1.short_display_text_id END AS t,"+
                "concat(w.word, ' ', s.variant) AS lemma, dom1.name_id AS domain," +
                "s.part_of_speech_id AS pos,s.lexicon_id AS lex FROM tbl_synset_relation r1 " +
                "LEFT JOIN tbl_relation_type rt1 ON r1.synset_relation_type_id = rt1.id " +
                "LEFT JOIN tbl_relation_type rt2 ON rt1.reverse_relation_type_id = rt2.id " +
                "LEFT JOIN tbl_synset_relation r2 ON (r2.child_synset_id = r1.parent_synset_id AND r2.parent_synset_id = r1.child_synset_id AND rt1.reverse_relation_type_id = r2.synset_relation_type_id) " +
                "LEFT JOIN tbl_sense s ON s.synset_id = r1.parent_synset_id " +
                "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                "WHERE r1.child_synset_id = ?1 AND s.synset_position = ?2 AND rt1.node_position != 'IGNORE') AS t " +
                "WHERE t.lex in (?3) group by t.c,t.f,t.t,t.lemma,t.domain,t.pos,t.lex "+
                "ORDER BY t.position DESC, t.lemma, t.domain";

        return em.createNativeQuery(query)
                .setParameter(1, id)
                .setParameter(2, Synset.SYNSET_HEAD_POSITION)
                .setParameter(3, lexicons)
                .getResultList();
    }

    private List<Object[]> fetchSenseGraphNode(UUID id) {
        List<Long> lexicons  = userFinder.getCurrentUser()
                .map( u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        String query = "SELECT  ANY_VALUE(t.np),t.c,t.f,t.t,t.lemma,t.domain,t.pos,t.lex FROM (SELECT rt1.node_position as np, BIN_TO_UUID(r1.child_sense_id) AS c," +
                "CASE WHEN r1.parent_sense_id is null THEN null ELSE rt1.short_display_text_id END AS f,"+
                "CASE WHEN r2.child_sense_id is null THEN null ELSE rt2.short_display_text_id END AS t,"+
                "concat(w.word, ' ', s.variant) AS lemma,"+
                "dom1.name_id as domain,"+
                "s.part_of_speech_id AS pos,"+
                "s.lexicon_id AS lex "+
                "FROM tbl_sense_relation r1 " +
                "LEFT JOIN tbl_relation_type rt1 ON r1.sense_relation_type_id = rt1.id " +
                "LEFT JOIN tbl_relation_type rt2 ON rt1.reverse_relation_type_id = rt2.id " +
                "LEFT JOIN tbl_sense_relation r2 ON (r2.child_sense_id = r1.parent_sense_id AND r2.parent_sense_id = r1.child_sense_id AND r2.sense_relation_type_id = rt1.reverse_relation_type_id) "+
                "LEFT JOIN tbl_sense s ON s.id = r1.child_sense_id " +
                "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                "WHERE r1.parent_sense_id = ?1 AND rt1.node_position != 'IGNORE' " +
                "UNION SELECT " +
                "CASE WHEN rt1.node_position  = 'LEFT' THEN IF(rt1.id = rt2.id,'LEFT','RIGHT') " +
                "WHEN rt1.node_position  = 'RIGHT' THEN IF(rt1.id = rt2.id,'RIGHT','LEFT') " +
                "WHEN rt1.node_position  = 'TOP' THEN 'BOTTOM' " +
                "WHEN rt1.node_position  = 'BOTTOM' THEN 'TOP' " +
                "WHEN rt1.node_position  = 'IGNORE' THEN 'IGNORE' " +
                "END AS position," +
                "BIN_TO_UUID(r1.parent_sense_id) AS c," +
                "CASE WHEN r2.child_sense_id is null THEN null ELSE rt2.short_display_text_id END AS f,"+
                "CASE WHEN r1.parent_sense_id is null THEN null ELSE rt1.short_display_text_id END AS t,"+
                "concat(w.word, ' ', s.variant) AS lemma," +
                "dom1.name_id as domain,"+
                "s.part_of_speech_id AS pos," +
                "s.lexicon_id AS lex "+
                "FROM tbl_sense_relation r1 " +
                "LEFT JOIN tbl_relation_type rt1 ON r1.sense_relation_type_id = rt1.id " +
                "LEFT JOIN tbl_relation_type rt2 ON rt1.reverse_relation_type_id = rt2.id " +
                "LEFT JOIN tbl_sense_relation r2 ON (r2.child_sense_id = r1.parent_sense_id AND r2.parent_sense_id = r1.child_sense_id AND rt1.reverse_relation_type_id = r2.sense_relation_type_id) " +
                "LEFT JOIN tbl_sense s ON s.id = r1.parent_sense_id " +
                "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                "WHERE r1.child_sense_id = ?1 AND rt1.node_position != 'IGNORE') t "+
                "WHERE t.lex in (?2) group by t.c,t.f,t.t,t.lemma,t.domain,t.pos,t.lex";

        return em.createNativeQuery(query)
                .setParameter(1, id)
                .setParameter(2, lexicons)
                .getResultList();
    }

    private NodeExpanded hiddenNode(final UUID parentId, final NodeHidden source, Locale locale, boolean isSynset) {

        List<Object[]> result;
        if (isSynset) {
            result = fetchSynsetGraphNode(source.getId());
        } else {
            result = fetchSenseGraphNode(source.getId());
        }

        NodeExpanded node = new NodeExpanded(new RootNode(source.getId(),source.getLex(), source.getPos(), source.getLabel(), isSynset), source.getRel());

        result.stream()
                .map(o ->  buildNodeHidden(locale,o[0],o[1],o[2],o[3],o[4],o[5],o[6],o[7],isSynset))
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(NodeHidden::getPosition))
                .forEach((k, v) -> {
                    if (k.equals("TOP")) {
                        loadHidden(parentId, v, node::addTopHidden);
                    }
                    if (k.equals("BOTTOM")) {
                        loadHidden(parentId, v, node::addBottomHidden);
                    }
                    if (k.equals("RIGHT")) {
                        loadHidden(parentId, v, node::addRightHidden);
                    }
                    if (k.equals("LEFT")) {
                        loadHidden(parentId, v, node::addLeftHidden);
                    }
                });
        return node;
    }

    private void loadHidden(UUID parentId, List<NodeHidden> v, Consumer<NodeHidden> consumer) {
        v.stream()
                .filter(i -> !i.getId().equals(parentId))
                .forEach(consumer);
    }


    public NodeExpanded synsetGraph(final UUID id, Locale locale) {
        List<Object[]> result = fetchSynsetGraphNode(id);
        RootNode rn = synsetQueryService.findSynsetHead(id)
                .map(s -> new RootNode(s.getId(),
                        s.getLexicon().getId(),
                        s.getSenses().stream().findFirst().get().getPartOfSpeech().getId(),
                        entityBuilder.buildLabel(s, locale),true) )
                .orElse(new RootNode(true));
        NodeExpanded node = new NodeExpanded(rn, null);
        return graph(result, node, locale, true);
    }

    public List<NodeExpanded> pathToHyperonym(final UUID synsetId, Locale locale){
        final String HYPERONYM_NAME = "hype";
        Optional<RelationType> hyperonymRelation = relationTypeQueryService.findRelationTypeByName(HYPERONYM_NAME, locale);
        if(!hyperonymRelation.isPresent()){
            return new ArrayList<>();
        }

        Stack<UUID> stack = new Stack<>();
        stack.add(synsetId);
        UUID currentId;
        Set<UUID> usedIds = new HashSet<>();
        usedIds.add(new UUID(0,0));
        List<UUID> pathIds = new ArrayList<>();
        while(!stack.isEmpty()){
            currentId = stack.pop();
            pathIds.add(currentId);
            List<UUID> relatedSynsets = synsetQueryService.getRelatedSynsets(currentId, hyperonymRelation.get().getId());
            relatedSynsets.forEach(id ->{
                if (!usedIds.contains(id)) {
                    stack.push(id);
                }
                usedIds.add(id);
            });
        }
        return pathIds.stream()
                .map(synId -> getNodeExpanded(synId, locale))
                .collect(Collectors.toList());
    }

    private NodeExpanded getNodeExpanded(final UUID id, final Locale locale){
        List<Object[]> result = fetchSynsetGraphNode(id);
        RootNode rootNode = synsetQueryService.findSynsetHead(id)
                .map(sense -> new RootNode(sense.getId(),
                        sense.getLexicon().getId(),
                        sense.getSenses().stream().findFirst().get().getPartOfSpeech().getId(),
                        entityBuilder.buildLabel(sense, locale),true))
                .orElse(new RootNode(true));
        NodeExpanded node = new NodeExpanded(rootNode, null);
        return graph(result, node, locale, true);
    }

    public NodeExpanded senseGraph(final UUID id, Locale locale) {
        List<Object[]> result = fetchSenseGraphNode(id);
        Sense s = senseQueryService.findHeadSense(id).get();
        NodeExpanded node = new NodeExpanded(new RootNode(s.getId(),
                s.getLexicon().getId(),
                s.getPartOfSpeech().getId(),
                createSenseLabel(s, locale), false), null);
        return graph(result, node, locale, false);
    }

    private String createSenseLabel(Sense sense, Locale locale) {
        return sense.getWord().getWord()+" "+sense.getVariant()+ " (" + loc.find(sense.getDomain().getName(), locale) + ")";
    }

    private NodeHidden buildNodeHidden(Locale locale, Object position, Object target, Object firstRelation, Object secondRelation,
                                       Object label, Object domain, Object pos,Object lexicon,boolean isSynset) {
        String r1 = firstRelation != null ? loc.find(((BigInteger) firstRelation).longValue(), locale) : null;
        String r2 = secondRelation != null ? loc.find(((BigInteger) secondRelation).longValue(), locale) : null;
        String dom = domain != null ? "(" + loc.find(((BigInteger) domain).longValue(), locale) + ")" : "";
        return new NodeHidden(position, target, r1, r2, label, dom, pos, lexicon, isSynset);
    }

    private NodeExpanded graph(final List<Object[]> result, final NodeExpanded node, Locale locale, boolean isSynset) {

        result.stream()
                .map(o ->  buildNodeHidden(locale,o[0],o[1],o[2],o[3],o[4],o[5],o[6],o[7], isSynset))
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(NodeHidden::getPosition))
                .forEach((k, v) -> {
                    if (k.equals("TOP")) {
                        v.stream()
                                .limit(NUMBER_OF_EXPANDED_NODES)
                                .forEach(i -> node.addTopExpanded(hiddenNode(node.getId(), i, locale, isSynset)));
                        v.stream()
                                .skip(NUMBER_OF_EXPANDED_NODES)
                                .forEach(node::addTopHidden);
                    }
                    if (k.equals("BOTTOM")) {
                        v.stream()
                                .limit(NUMBER_OF_EXPANDED_NODES)
                                .forEach(i -> node.addBottomExpanded(hiddenNode(node.getId(), i, locale, isSynset)));
                        v.stream()
                                .skip(NUMBER_OF_EXPANDED_NODES).forEach(node::addBottomHidden);
                    }
                    if (k.equals("RIGHT")) {
                        v.stream()
                                .limit(NUMBER_OF_EXPANDED_NODES)
                                .forEach(i -> node.addRightExpanded(hiddenNode(node.getId(), i, locale, isSynset)));
                        v.stream()
                                .skip(NUMBER_OF_EXPANDED_NODES).forEach(node::addRightHidden);
                    }
                    if (k.equals("LEFT")) {
                        v.stream()
                                .limit(NUMBER_OF_EXPANDED_NODES)
                                .forEach(i -> node.addLeftExpanded(hiddenNode(node.getId(), i, locale, isSynset)));
                        v.stream()
                                .skip(NUMBER_OF_EXPANDED_NODES).forEach(node::addLeftHidden);
                    }
                });
        return node;
    }

}
