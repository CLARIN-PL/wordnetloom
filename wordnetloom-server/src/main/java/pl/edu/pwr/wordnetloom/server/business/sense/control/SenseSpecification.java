package pl.edu.pwr.wordnetloom.server.business.sense.control;

import pl.edu.pwr.wordnetloom.server.business.SearchFilter;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.SourceDictionary;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.yiddish.entity.*;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SenseSpecification {

    public static Specification<Sense> byFilter(SearchFilter filter, List<Long> userLexicons) {

        return (root, query, cb) -> {

            List<Predicate> criteria = new ArrayList<>();

            if (filter.getLemma() != null && !filter.getLemma().isEmpty()) {
                if(filter.getLexicon() != null && filter.getLexicon() == 4L){
                    Predicate lemmas = filterAcrossLemmas(filter.getLemma(), root, cb);
                    if (lemmas != null) criteria.add(lemmas);
                }else {
                    criteria.add(byLemmaLike(filter.getLemma()).toPredicate(root, query, cb));
                }
            }

            if (filter.getPartOfSpeechId() != null) {
                criteria.add(byPartOfSpeech(filter.getPartOfSpeechId()).toPredicate(root, query, cb));
            }

            if (filter.getDomainId() != null) {
                criteria.add(byDomain(filter.getDomainId()).toPredicate(root, query, cb));
            }

            if (filter.getLexicon() != null) {
                criteria.add(byLexiconId(filter.getLexicon()).toPredicate(root, query, cb));
            }

            if(filter.getStatusId() != null){
                criteria.add(byStatus(filter.getStatusId()).toPredicate(root, query, cb));
            }

            if(filter.getSenseWithoutSynset() != null && filter.getSenseWithoutSynset()){
                criteria.add(synsetIsNull().toPredicate(root, query, cb));
            }

            if (filter.getRelationTypeId() != null) {
                    criteria.addAll(bySenseRelationType(filter.getRelationTypeId(), root, cb));
            }

            Expression<Long> expression = root.get("lexicon").get("id");
            Predicate userLexiconsPredicate = expression.in(userLexicons);
            criteria.add(userLexiconsPredicate);

            Predicate attributes = filterSenseAttributes(filter, root, cb);
            if (attributes != null) criteria.add(attributes);

            Predicate yiddish = filterYiddishExtension(filter, root, cb);
            if (yiddish != null) criteria.add(yiddish);

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static Predicate filterAcrossLemmas(String lemma, Root<Sense> root, CriteriaBuilder cb) {
        CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<YiddishSenseExtension> yiddishRoot = subquery.from(YiddishSenseExtension.class);
        subquery.select(yiddishRoot.get("sense").get("id"));
        List<Predicate> predicates = new ArrayList<>();

        Predicate latinPredicate =  cb.like(yiddishRoot.get("latinSpelling"),"%"+ lemma+"%");
        Predicate yivoPredicate =  cb.like(yiddishRoot.get("yivoSpelling"), "%"+lemma+"%");
        Predicate yiddishPredicate =  cb.like(yiddishRoot.get("yiddishSpelling"), "%"+lemma+"%");

        Join<YiddishSenseExtension, Transcription> transJoin = yiddishRoot.join("transcriptions");
        Predicate transPredicate =  cb.like(transJoin.get("phonography"), "%"+lemma +"%");

        Predicate orPredicate = cb.or(latinPredicate,yivoPredicate,yiddishPredicate, transPredicate);
        predicates.add(orPredicate);

        subquery.where(cb.and(predicates.toArray(new Predicate[0])));
        return cb.in(root.get("id")).value(subquery);
    }

    public static Predicate filterYiddishRootParticles(SearchFilter filter, Root<YiddishSenseExtension> root, CriteriaBuilder cb) {
        if (filter.getParticleRoot() != null && !filter.getParticleRoot().isEmpty()) {
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Subquery<Long> subquery = query.subquery(Long.class);

            Root<RootParticle> particleRoot = subquery.from(RootParticle.class);

            subquery.select(particleRoot.get("extension").get("id"));
            List<Predicate> predicates = new ArrayList<>();


            Predicate sourcePredicate = cb.equal(particleRoot.get("root"), filter.getParticleRoot());
            predicates.add(sourcePredicate);

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static Predicate filterYiddishConstituentParticles(SearchFilter filter, Root<YiddishSenseExtension> root, CriteriaBuilder cb) {
        if (filter.getParticleConstituent() != null && !filter.getParticleConstituent().isEmpty()) {
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Subquery<Long> subquery = query.subquery(Long.class);

            Root<ConstituentParticle> particleRoot = subquery.from(ConstituentParticle.class);

            subquery.select(particleRoot.get("extension").get("id"));
            List<Predicate> predicates = new ArrayList<>();

            Predicate sourcePredicate = cb.equal(particleRoot.get("constituent"), filter.getParticleConstituent());
            predicates.add(sourcePredicate);

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static Predicate filterYiddishPrefixParticles(SearchFilter filter, Root<YiddishSenseExtension> root, CriteriaBuilder cb) {
        if (filter.getParticlePrefix() != null) {
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Subquery<Long> subquery = query.subquery(Long.class);

            Root<PrefixParticle> particleRoot = subquery.from(PrefixParticle.class);

            subquery.select(particleRoot.get("extension").get("id"));
            List<Predicate> predicates = new ArrayList<>();

            Predicate sourcePredicate = cb.equal(particleRoot.get("prefix").get("id"), filter.getParticlePrefix());
            predicates.add(sourcePredicate);

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static Predicate filterYiddishSuffixParticles(SearchFilter filter, Root<YiddishSenseExtension> root, CriteriaBuilder cb) {
        if (filter.getParticleSuffix() != null) {
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Subquery<Long> subquery = query.subquery(Long.class);

            Root<SuffixParticle> particleRoot = subquery.from(SuffixParticle.class);

            subquery.select(particleRoot.get("extension").get("id"));
            List<Predicate> predicates = new ArrayList<>();

            Predicate sourcePredicate = cb.equal(particleRoot.get("suffix").get("id"), filter.getParticleSuffix());
            predicates.add(sourcePredicate);

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static Predicate filterYiddishExtension(SearchFilter filter, Root<Sense> root, CriteriaBuilder cb) {
        if (filter.getEtymologicalRoot() != null || filter.getAgeId() != null
                || (filter.getEtymology() !=null && !filter.getEtymology().isEmpty())
                || filter.getGrammaticalGenderId() != null || filter.getYiddishStatusId() != null
                || filter.getInflectionId() != null || filter.getLexicalCharacteristicId() != null
                || filter.getSourceId() != null || filter.getStyleId() != null
                || filter.getYiddishDomainId() != null || filter.getYiddishDomainModificationId() != null
                || (filter.getParticleRoot() != null && !filter.getParticleRoot().isEmpty())
                || (filter.getParticleConstituent() != null && !filter.getParticleConstituent().isEmpty())
                || filter.getParticlePrefix() != null || filter.getParticleSuffix() !=null) {

            CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<YiddishSenseExtension> yiddishRoot = subquery.from(YiddishSenseExtension.class);
            subquery.select(yiddishRoot.get("sense").get("id"));
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEtymologicalRoot() != null && !filter.getEtymologicalRoot().equals("")) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("etymologicalRoot"), filter.getEtymologicalRoot());
                predicates.add(yiddishPredicate);
            }

            if (filter.getAgeId() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("age").get("id"), filter.getAgeId());
                predicates.add(yiddishPredicate);
            }

            if (filter.getGrammaticalGenderId() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("grammaticalGender").get("id"), filter.getGrammaticalGenderId());
                predicates.add(yiddishPredicate);
            }

            if (filter.getLexicalCharacteristicId() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("lexicalCharacteristic").get("id"), filter.getLexicalCharacteristicId());
                predicates.add(yiddishPredicate);
            }

            if (filter.getStyleId() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("style").get("id"), filter.getStyleId());
                predicates.add(yiddishPredicate);
            }

            if (filter.getYiddishStatusId() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("status").get("id"), filter.getYiddishStatusId());
                predicates.add(yiddishPredicate);
            }

            if (filter.getEtymology() != null) {
                Predicate yiddishPredicate = cb.equal(yiddishRoot.get("etymology"), filter.getEtymology());
                predicates.add(yiddishPredicate);
            }

            if (filter.getSourceId() != null) {
                Join<YiddishSenseExtension, SourceDictionary> sourceJoin = yiddishRoot.join("source");
                Predicate sourcePredicate = cb.equal(sourceJoin.get("id"), filter.getSourceId());
                predicates.add(sourcePredicate);
            }

            if (filter.getInflectionId() != null) {
                Join<YiddishSenseExtension, Inflection> sourceJoin = yiddishRoot.join("inflection");
                Predicate sourcePredicate = cb.equal(sourceJoin.get("inflectionDictionary").get("id"), filter.getInflectionId());
                predicates.add(sourcePredicate);
            }

            if (filter.getYiddishDomainId() != null || filter.getYiddishDomainModificationId() != null) {
                Join<YiddishSenseExtension, YiddishDomain> sourceJoin = yiddishRoot.join("yiddishDomains");
                if (filter.getYiddishDomainId() != null) {
                    Predicate sourcePredicate = cb.equal(sourceJoin.get("domain").get("id"), filter.getYiddishDomainId());
                    predicates.add(sourcePredicate);
                }
                if (filter.getYiddishDomainModificationId() != null) {
                    Predicate sourcePredicate = cb.equal(sourceJoin.get("modifier").get("id"), filter.getYiddishDomainModificationId());
                    predicates.add(sourcePredicate);
                }
            }

            Predicate particlesRoot = filterYiddishRootParticles(filter, yiddishRoot, cb);
            if (particlesRoot != null) predicates.add(particlesRoot);

            Predicate particlesConst = filterYiddishConstituentParticles(filter, yiddishRoot, cb);
            if (particlesConst != null) predicates.add(particlesConst);

            Predicate particlesPrefix = filterYiddishPrefixParticles(filter, yiddishRoot, cb);
            if (particlesPrefix != null) predicates.add(particlesPrefix);

            Predicate particlesSuffix = filterYiddishSuffixParticles(filter, yiddishRoot, cb);
            if (particlesSuffix != null) predicates.add(particlesSuffix);

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }


    public static Predicate filterSenseAttributes(SearchFilter filter, Root<Sense> root, CriteriaBuilder cb) {

        if (filter.getRegisterId() != null || filter.getComment() != null || filter.getDefinition() != null
                || filter.getExample() != null) {

            CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<SenseAttributes> senseAttributesRoot = subquery.from(SenseAttributes.class);
            subquery.select(senseAttributesRoot.get("sense").get("id"));

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getRegisterId() != null) {
                Predicate registerPredicate = cb.equal(senseAttributesRoot.get("register").get("id"), filter.getRegisterId());
                predicates.add(registerPredicate);
            }

            if (filter.getDefinition() != null && !filter.getDefinition().isEmpty()) {
                Predicate definitionPredicate = cb.like(senseAttributesRoot.get("definition"), "%" + filter.getDefinition() + "%");
                predicates.add(definitionPredicate);
            }

            if (filter.getComment() != null && !filter.getComment().isEmpty()) {
                Predicate commentPredicate = cb.like(senseAttributesRoot.get("comment"), "%" + filter.getComment() + "%");
                predicates.add(commentPredicate);
            }

            if (filter.getExample() != null) {
                Join<SenseAttributes, SenseExample> senseExampleJoin = senseAttributesRoot.join("examples");
                Predicate examplePredicate = cb.like(senseExampleJoin.get("examples"), filter.getExample());
                predicates.add(examplePredicate);
            }

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static List<Predicate> bySenseRelationType(UUID rel, Root<Sense> root, CriteriaBuilder cb) {
        Join<Sense, SenseRelation> outgoing = root.join("outgoingRelations", JoinType.LEFT);
        Predicate outgoingRelationsPredicate = cb.equal(outgoing.get("relationType").get("id"), rel);
        return Collections.singletonList(outgoingRelationsPredicate);
    }

    public static Specification<Sense> byPartOfSpeech(Long posId) {
        return (root, query, cb) -> cb.equal(root.get("partOfSpeech").get("id"), posId);
    }

    public static Specification<Sense> byStatus(Long statusId) {
        return (root, query, cb) -> cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<Sense> synsetIsNull() {
        return (root, query, cb) -> cb.isNull(root.get("synset").get("id"));
    }

    public static Specification<Sense> byDomain(Long domainId) {
        return (root, query, cb) -> cb.equal(root.get("domain").get("id"), domainId);
    }

    public static Specification<Sense> byWord(Word word) {
        return (root, query, cb) -> cb.equal(root.get("word"), word);
    }

    public static Specification<Sense> byLemma(String lemma) {
        return (root, query, cb) -> cb.equal(root.get("word").get("word"), lemma);
    }

    public static Specification<Sense> byLemmaLike(String lemma) {
        return (root, query, cb) -> cb.like(root.get("word").get("word"), lemma + "%");
    }

    public static Specification<Sense> byLemmaLikeContains(String lemma) {
        if (lemma == null || lemma.isEmpty()) {
            return byLemmaLike("%");
        } else {
            return byLemmaLike("%" + lemma + "%");
        }
    }

    public static Specification<Sense> byVarinat(Integer variant) {
        return (root, query, cb) -> cb.equal(root.get("variant"), variant);
    }

    public static Specification<Sense> byLexiconId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("lexicon").get("id"), id);
    }



}
