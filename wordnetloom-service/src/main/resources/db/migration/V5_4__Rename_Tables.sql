ALTER TABLE application_localised_string
RENAME TO tbl_application_localised_string;

ALTER TABLE corpus_example
RENAME TO tbl_corpus_example;

ALTER TABLE dictionaries
RENAME TO tbl_dictionaries;

ALTER TABLE domain
RENAME TO tbl_domain;

ALTER TABLE lexicon
RENAME TO tbl_lexicon;

ALTER TABLE part_of_speech
RENAME TO tbl_part_of_speech;

ALTER TABLE relation_tests
RENAME TO tbl_relation_tests;

ALTER TABLE relation_type
RENAME TO tbl_relation_type;

ALTER TABLE relation_type_allowed_lexicons
RENAME TO tbl_relation_type_allowed_lexicons;

ALTER TABLE relation_type_allowed_parts_of_speech
RENAME TO tbl_relation_type_allowed_parts_of_speech;

ALTER TABLE sense
RENAME TO tbl_sense;

ALTER TABLE sense_attributes
RENAME TO tbl_sense_attributes;

ALTER TABLE sense_examples
RENAME TO tbl_sense_examples;

ALTER TABLE sense_relation
RENAME TO tbl_sense_relation;

ALTER TABLE tbl_sense_relation
RENAME COLUMN relation_type_id TO sense_relation_type_id;

ALTER TABLE synset
RENAME TO tbl_synset;

ALTER TABLE synset_attributes
RENAME TO tbl_synset_attributes;

ALTER TABLE synset_examples
RENAME TO tbl_synset_examples;

ALTER TABLE synset_relation
RENAME TO tbl_synset_relation;

ALTER TABLE word
RENAME TO tbl_word;

ALTER TABLE word_form
RENAME TO tbl_word_form;

ALTER TABLE sense_emotions
RENAME TO tbl_sense_emotions;

ALTER TABLE sense_valuations
RENAME TO tbl_sense_valuations;

ALTER TABLE emotional_annotations
RENAME TO tbl_emotional_annotations;
