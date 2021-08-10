# Use only for empty audit tables!

INSERT INTO REVINFO (REV, REVTSTMP, user_email)
VALUES (1, 0, 'init');

insert INTO hibernate_sequence
values ( 2 );

INSERT INTO part_of_speech_allowed_domain_AUD (lexicon_allowed_part_of_speech_id, domain_id, REV, REVTYPE)
    SELECT lexicon_allowed_part_of_speech_id, domain_id, 1, 0
    FROM part_of_speech_allowed_domain;

INSERT INTO tbl_application_localised_string_AUD (id, language, value, REV, REVTYPE)
    SELECT id, language, value, 1, 0
    FROM tbl_application_localised_string;

INSERT INTO tbl_corpus_example_AUD (id, text, word, REV, REVTYPE)
    SELECT id, text, word, 1, 0
    FROM tbl_corpus_example;

INSERT INTO tbl_dictionaries_AUD (id, dtype, description_id, is_default, name_id, color, REV, REVTYPE)
    SELECT id, dtype, description_id, is_default, name_id, color, 1, 0
    FROM tbl_dictionaries;

INSERT INTO tbl_domain_AUD (id, description_id, name_id, REV, REVTYPE)
    SELECT id, description_id, name_id, 1, 0
    FROM tbl_domain;

INSERT INTO tbl_lexicon_AUD (id, citation, confidence_score, email, identifier, language_name, language_shortcut, lexicon_version, license, name, reference_url, REV, REVTYPE)
    SELECT id, citation, confidence_score, email, identifier, language_name, language_shortcut, lexicon_version, license, name, reference_url, 1, 0
    FROM tbl_lexicon;

INSERT INTO tbl_lexicon_allowed_part_of_speech_AUD (id, lexicon_id, part_of_speech_id, REV, REVTYPE)
    SELECT id, lexicon_id, part_of_speech_id, 1, 0
    FROM tbl_lexicon_allowed_part_of_speech;

INSERT INTO tbl_part_of_speech_AUD (id, color, name_id, REV, REVTYPE)
    SELECT id, color, name_id, 1, 0
    FROM tbl_part_of_speech;

INSERT INTO tbl_relation_tests_AUD (id, position, test, relation_type_id, element_A_part_of_speech_id, element_B_part_of_speech_id, REV, REVTYPE)
    SELECT id, position, test, relation_type_id, element_A_part_of_speech_id, element_B_part_of_speech_id, 1, 0
    FROM tbl_relation_tests;

INSERT INTO tbl_relation_type_allowed_lexicons_AUD (relation_type_id, lexicon_id, REV, REVTYPE)
    SELECT relation_type_id, lexicon_id, 1, 0
    FROM tbl_relation_type_allowed_lexicons;

INSERT INTO tbl_relation_type_allowed_parts_of_speech_AUD (relation_type_id, part_of_speech_id, REV, REVTYPE)
    SELECT relation_type_id, part_of_speech_id, 1, 0
    FROM tbl_relation_type_allowed_parts_of_speech;

INSERT INTO tbl_relation_type_AUD (id, auto_reverse, color, description_id, display_text_id, global_wordnet_relation_type, multilingual, name_id, node_position, relation_argument, short_display_text_id, parent_relation_type_id, reverse_relation_type_id, REV, REVTYPE)
    SELECT id, auto_reverse, color, description_id, display_text_id, global_wordnet_relation_type, multilingual, name_id, node_position, relation_argument, short_display_text_id, parent_relation_type_id, reverse_relation_type_id, 1, 0
    FROM tbl_relation_type;

INSERT INTO tbl_sense_attributes_AUD (sense_id, comment, definition, error_comment, link, user_id, register_id, REV, REVTYPE)
    SELECT sense_id, comment, definition, error_comment, link, user_id, register_id, 1, 0
    FROM tbl_sense_attributes;

INSERT INTO tbl_sense_AUD (id, synset_position, variant, domain_id, lexicon_id, part_of_speech_id, status_id, synset_id, word_id, REV, REVTYPE)
    SELECT id, synset_position, variant, domain_id, lexicon_id, part_of_speech_id, status_id, synset_id, word_id, 1, 0
    FROM tbl_sense;

INSERT INTO tbl_sense_examples_AUD (id, example, type, sense_attribute_id, REV, REVTYPE)
    SELECT id, example, type, sense_attribute_id, 1, 0
    FROM tbl_sense_examples;

INSERT INTO tbl_sense_relation_AUD (sense_relation_type_id, parent_sense_id, child_sense_id, REV, REVTYPE)
    SELECT sense_relation_type_id, parent_sense_id, child_sense_id, 1, 0
    FROM tbl_sense_relation;

INSERT INTO tbl_synset_attributes_AUD (synset_id, comment, definition, error_comment, ili_id, princeton_id, owner_id, REV, REVTYPE)
    SELECT synset_id, comment, definition, error_comment, ili_id, princeton_id, owner_id, 1, 0
    FROM tbl_synset_attributes;

INSERT INTO tbl_synset_AUD (id, abstract, lexicon_id, status_id, REV, REVTYPE)
    SELECT id, abstract, lexicon_id, status_id, 1, 0
    FROM tbl_synset;

INSERT INTO tbl_synset_examples_AUD (id, example, type, synset_attribute_id, REV, REVTYPE)
    SELECT id, example, type, synset_attribute_id, 1, 0
    FROM tbl_synset_examples;

INSERT INTO tbl_synset_relation_AUD (synset_relation_type_id, parent_synset_id, child_synset_id, REV, REVTYPE)
    SELECT synset_relation_type_id, parent_synset_id, child_synset_id, 1, 0
    FROM tbl_synset_relation;

INSERT INTO tbl_users_AUD (id, email, firstname, lastname, password, role, REV, REVTYPE)
    SELECT id, email, firstname, lastname, password, role, 1, 0
    FROM tbl_users;

INSERT INTO tbl_users_settings_AUD (user_id, chosen_lexicons, lexicon_marker, show_tool_tips, REV, REVTYPE)
    SELECT user_id, chosen_lexicons, lexicon_marker, show_tool_tips, 1, 0
    FROM tbl_users_settings;

INSERT INTO tbl_word_AUD (id, word, REV, REVTYPE)
    SELECT id, word, 1, 0
    FROM tbl_word;

INSERT INTO tbl_word_form_AUD (id, form, tag, word, REV, REVTYPE)
    SELECT id, form, tag, word, 1, 0
    FROM tbl_word_form;
