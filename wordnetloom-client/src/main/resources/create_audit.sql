CREATE TABLE wordnet_audit.REVINFO (
	REV int(11) PRIMARY KEY AUTO_INCREMENT,
    REVTSTMP bigint(20)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_dictionaries` (
  `dtype` varchar(31) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `id` bigint(20) NOT NULL,
  `description_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  `tag` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  `color` varchar(7) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `is_default` bit(1) DEFAULT b'0',
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_domain` (
  `id` bigint(20) NOT NULL,
  `description_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_lexicon` (
  `id` bigint(20) NOT NULL ,
  `identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `language_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `language_shortcut` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `lexicon_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `license` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `reference_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `citation` text CHARACTER SET utf8 COLLATE utf8_bin,
  `confidence_score` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `onlyToRead` bit(1) DEFAULT b'0',
  	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_part_of_speech` (
  `id` bigint(20) NOT NULL,
  `name_id` bigint(20) DEFAULT NULL ,
  `color` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_relation_tests` (
  `id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) DEFAULT NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  `test` text CHARACTER SET utf8 COLLATE utf8_bin,
  `element_A_part_of_speech_id` bigint(20) DEFAULT NULL,
  `element_B_part_of_speech_id` bigint(20) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_relation_type` (
  `id` binary(16) NOT NULL,
  `auto_reverse` bit(1) NOT NULL DEFAULT b'0',
  `multilingual` bit(1) NOT NULL DEFAULT b'0',
  `description_id` bigint(20) DEFAULT NULL,
  `display_text_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  `relation_argument` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `global_wordnet_relation_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `short_display_text_id` bigint(20) DEFAULT NULL,
  `color` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `node_position` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `parent_relation_type_id` binary(16) DEFAULT NULL,
  `reverse_relation_type_id` binary(16) DEFAULT NULL,
    `REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_relation_type_allowed_lexicons` (
  `lexicon_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  `REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_relation_type_allowed_parts_of_speech` (
  `part_of_speech_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  `REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_sense` (
  `id` binary(16) NOT NULL,
  `synset_position` int(11) DEFAULT NULL,
  `variant` int(11) NOT NULL DEFAULT '1',
  `domain_id` bigint(20) NOT NULL,
  `lexicon_id` bigint(20) NOT NULL,
  `part_of_speech_id` bigint(20) NOT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `synset_id` binary(16) DEFAULT NULL,
  `word_id` binary(16) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_sense_attributes` (
  `sense_id` binary(16) NOT NULL,
  `comment` text CHARACTER SET utf8 COLLATE utf8_bin,
  `definition` text CHARACTER SET utf8 COLLATE utf8_bin,
  `link` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `register_id` bigint(20) DEFAULT NULL,
  `aspect_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `error_comment` text CHARACTER SET utf8 COLLATE utf8_bin,
  `proper_name` bit(1) NOT NULL DEFAULT b'0',
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_sense_examples` (
  `id` binary(16) NOT NULL,
  `example` text CHARACTER SET utf8 COLLATE utf8_bin,
  `type` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `sense_attribute_id` binary(16) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_sense_relation` (
  `parent_sense_id` binary(16) NOT NULL,
  `child_sense_id` binary(16) NOT NULL,
  `sense_relation_type_id` binary(16) NOT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_synset` (
  `id` binary(16) NOT NULL,
  `lexicon_id` bigint(20) NOT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `abstract` tinyint(1) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_synset_attributes` (
  `synset_id` binary(16) NOT NULL,
  `comment` text CHARACTER SET utf8 COLLATE utf8_bin,
  `definition` text CHARACTER SET utf8 COLLATE utf8_bin,
  `princeton_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `error_comment` text CHARACTER SET utf8 COLLATE utf8_bin,
  `ili_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_synset_examples` (
  `id` binary(16) NOT NULL,
  `example` text CHARACTER SET utf8 COLLATE utf8_bin,
  `type` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `synset_attribute_id` binary(16) DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_synset_relation` (
  `parent_synset_id` binary(16) NOT NULL,
  `child_synset_id` binary(16) NOT NULL,
  `synset_relation_type_id` binary(16) NOT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `firstname` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `lastname` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_users_settings` (
  `user_id` bigint(20) NOT NULL,
  `lexicon_marker` tinyint(1) DEFAULT NULL,
  `chosen_lexicons` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `show_tool_tips` tinyint(1) DEFAULT NULL,
  `REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

CREATE TABLE `wordnet_audit`.`tracker_tbl_word` (
  `id` binary(16) NOT NULL,
  `word` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
	`REV` int(11) ,
    `REVEND` int(11),
    `REVTYPE` tinyint(4),
    FOREIGN KEY (REV) REFERENCES wordnet_audit.REVINFO(REV)
);

INSERT INTO wordnet_audit.REVINFO (REV, REVTSTMP)
VALUES (1, 1552388152843);

INSERT INTO wordnet_audit.tracker_tbl_dictionaries (`dtype`, `id`, `description_id`, `name_id`, `tag`, `value`, `color`, `is_default`, `REV`, `REVTYPE`)
SELECT `dtype`, `id`, `description_id`, `name_id`, `tag`, `value`, `color`, `is_default`, 1, 0
FROM tbl_dictionaries;

INSERT INTO wordnet_audit.tracker_tbl_domain(`id`, `description_id`, `name_id`, `REV`, `REVTYPE`)
SELECT `id`, `description_id`, `name_id`, 1, 0
FROM tbl_domain;

INSERT INTO wordnet_audit.tracker_tbl_lexicon(`id`, `identifier`, `language_name`, `language_shortcut`, `name`, `lexicon_version`, `license`, `email`, `reference_url`, `citation`, `confidence_score`, `description`, `onlyToRead`, `REV`, `REVTYPE`)
SELECT `id`, `identifier`, `language_name`, `language_shortcut`, `name`, `lexicon_version`, `license`, `email`, `reference_url`, `citation`, `confidence_score`, `description`, `onlyToRead`, 1, 0
FROM tbl_lexicon;

INSERT INTO wordnet_audit.tracker_tbl_part_of_speech(`id`, `name_id`, `color`, `REV`, `REVTYPE`)
SELECT `id`, `name_id`, `color`, 1,  0
FROM tbl_part_of_speech;

INSERT INTO wordnet_audit.tracker_tbl_relation_tests (`id`, `relation_type_id`, `position`, `test`, `element_A_part_of_speech_id`, `element_B_part_of_speech_id`, `REV`, `REVTYPE`)
SELECT `id`, `relation_type_id`, `position`, `test`, `element_A_part_of_speech_id`, `element_B_part_of_speech_id`, 1, 0
FROM tbl_relation_tests;

INSERT INTO wordnet_audit.tracker_tbl_relation_type (`id`, `auto_reverse`, `multilingual`, `description_id`, `display_text_id`, `name_id`, `relation_argument`, `global_wordnet_relation_type`, `short_display_text_id`, `color`, `node_position`, `priority`, `parent_relation_type_id`, `reverse_relation_type_id`, `REV`,  `REVTYPE`)
SELECT `id`, `auto_reverse`, `multilingual`, `description_id`, `display_text_id`, `name_id`, `relation_argument`, `global_wordnet_relation_type`, `short_display_text_id`, `color`, `node_position`, `priority`, `parent_relation_type_id`, `reverse_relation_type_id`, 1, 0
FROM tbl_relation_type;

INSERT INTO wordnet_audit.tracker_tbl_relation_type_allowed_lexicons(`lexicon_id`, `relation_type_id`, `REV`,  `REVTYPE`)
SELECT `lexicon_id`, `relation_type_id`, 1, 0
FROM tbl_relation_type_allowed_lexicons;

INSERT INTO wordnet_audit.tracker_tbl_relation_type_allowed_parts_of_speech(`part_of_speech_id`, `relation_type_id`, `REV`,  `REVTYPE`)
SELECT `part_of_speech_id`, `relation_type_id`, 1, 0
FROM tbl_relation_type_allowed_parts_of_speech;

INSERT INTO wordnet_audit.tracker_tbl_sense(`id`, `synset_position`, `variant`, `domain_id`, `lexicon_id`, `part_of_speech_id`, `status_id`, `synset_id`, `word_id`, `REV`, `REVTYPE`)
SELECT `id`, `synset_position`, `variant`, `domain_id`, `lexicon_id`, `part_of_speech_id`, `status_id`, `synset_id`, `word_id`, 1,  0
FROM tbl_sense;

INSERT INTO wordnet_audit.tracker_tbl_sense_attributes(`sense_id`, `comment`, `definition`, `link`, `register_id`, `aspect_id`, `user_id`, `error_comment`, `proper_name`, `REV`,  `REVTYPE`)
SELECT `sense_id`, `comment`, `definition`, `link`, `register_id`, `aspect_id`, `user_id`, `error_comment`, `proper_name`, 1, 0
FROM tbl_sense_attributes;

INSERT INTO wordnet_audit.tracker_tbl_sense_examples(`id`, `example`, `type`, `sense_attribute_id`, `REV`,  `REVTYPE`)
SELECT `id`, `example`, `type`, `sense_attribute_id`, 1,  0
FROM tbl_sense_examples;

INSERT INTO wordnet_audit.tracker_tbl_sense_relation (`parent_sense_id`, `child_sense_id`, `sense_relation_type_id`, `REV`,  `REVTYPE`)
SELECT `parent_sense_id`, `child_sense_id`, `sense_relation_type_id`, 1,  0
FROM tbl_sense_relation;

INSERT INTO wordnet_audit.tracker_tbl_synset(`id`, `lexicon_id`, `status_id`, `abstract`, `REV`,  `REVTYPE`)
SELECT `id`, `lexicon_id`, `status_id`, `abstract`, 1,  0
FROM tbl_synset;

INSERT INTO wordnet_audit.tracker_tbl_synset_attributes(`synset_id`, `comment`, `definition`, `princeton_id`, `owner_id`, `error_comment`, `ili_id`, `REV`,  `REVTYPE`)
SELECT `synset_id`, `comment`, `definition`, `princeton_id`, `owner_id`, `error_comment`, `ili_id`,1,  0
FROM tbl_synset_attributes;

INSERT INTO wordnet_audit.tracker_tbl_synset_examples(`id`, `example`, `type`, `synset_attribute_id`, `REV`,  `REVTYPE`)
SELECT `id`, `example`, `type`, `synset_attribute_id`, 1, 0
FROM tbl_synset_examples;

INSERT INTO wordnet_audit.tracker_tbl_synset_relation (`parent_synset_id`, `child_synset_id`, `synset_relation_type_id`, `REV`,  `REVTYPE`)
SELECT `parent_synset_id`, `child_synset_id`, `synset_relation_type_id`, 1,  0
FROM tbl_synset_relation;

INSERT INTO wordnet_audit.tracker_tbl_users (`id`, `email`, `firstname`, `lastname`, `password`, `role`, `REV`,  `REVTYPE`)
SELECT `id`, `email`, `firstname`, `lastname`, `password`, `role`, 1,  0
FROM tbl_users;

INSERT INTO wordnet_audit.tracker_tbl_users_settings(`user_id`, `lexicon_marker`, `chosen_lexicons`, `show_tool_tips`, `REV`,  `REVTYPE`)
SELECT `user_id`, `lexicon_marker`, `chosen_lexicons`, `show_tool_tips`, 1,  0
FROM tbl_users_settings;

INSERT INTO wordnet_audit.tracker_tbl_word (`id`, `word`, `REV`, `REVTYPE`)
SELECT `id`, `word`, 1, 0
FROM tbl_word;