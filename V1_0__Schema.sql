--
-- Table structure for table `application_labels`
--
DROP TABLE IF EXISTS `application_labels`;
CREATE TABLE `application_labels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_key` varchar(70) NOT NULL,
  `value` varchar(255) NOT NULL,
  `language` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `firstname` varchar(255)  NOT NULL,
  `lastname` varchar(255)  NOT NULL,
  `password` varchar(64)  NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `users_settings`
--
DROP TABLE IF EXISTS `users_settings`;

CREATE TABLE `users_settings` (
  `user_id` bigint(20) NOT NULL,
  `lexicon_marker` tinyint(1) DEFAULT NULL,
  `chosen_lexicons` varchar(255)  NOT NULL,
  `show_tool_tips` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `users_settings_to_user_constraint` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `application_localised_string`
--
DROP TABLE IF EXISTS `application_localised_string`;
CREATE TABLE `application_localised_string` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` text,
  `language` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`language`)
) ENGINE=InnoDB;

--
-- Table structure for table `relation_type`
--
DROP TABLE IF EXISTS `relation_type`;

CREATE TABLE `relation_type` (
  `id` binary(16) NOT NULL,
  `auto_reverse` bit(1) NOT NULL DEFAULT b'0' COMMENT 'On true application will create automatically reversed relation',
  `multilingual` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Relation between two lexicons',
  `description_id` bigint(20) DEFAULT NULL,
  `display_text_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  `relation_argument` varchar(255) DEFAULT NULL COMMENT 'Describes type of relation',
  `short_display_text_id` bigint(20) DEFAULT NULL COMMENT 'Text displayed on visualisation',
  `color` varchar(255) DEFAULT NULL COMMENT 'Color of displayed relation',
  `node_position` varchar(255) DEFAULT NULL COMMENT 'Position in node LEFT,TOP,RIGHT,BOTTOM',
  `priority` int(11) DEFAULT NULL,
  `parent_relation_type_id` binary(16) DEFAULT NULL,
  `reverse_relation_type_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`description_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`display_text_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`name_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`short_display_text_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`parent_relation_type_id`) REFERENCES `relation_type` (`id`),
  FOREIGN KEY (`reverse_relation_type_id`) REFERENCES `relation_type` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `part_of_speech`
--
DROP TABLE IF EXISTS `part_of_speech`;

CREATE TABLE `part_of_speech` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name_id` bigint(20) DEFAULT NULL COMMENT 'Name of part of speech',
  `color` varchar(255) DEFAULT NULL COMMENT 'Color displayed on visualisation',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`name_id`) REFERENCES `application_localised_string` (`id`)
) ENGINE=InnoDB COMMENT='Table describes parts of speech';

--
-- Table structure for table `relation_type_allowed_parts_of_speech`
--
DROP TABLE IF EXISTS `relation_type_allowed_parts_of_speech`;

CREATE TABLE `relation_type_allowed_parts_of_speech` (
  `part_of_speech_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`part_of_speech_id`,`relation_type_id`),
  FOREIGN KEY (`part_of_speech_id`) REFERENCES `part_of_speech` (`id`),
  FOREIGN KEY (`relation_type_id`) REFERENCES `relation_type` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `lexicon`
--
DROP TABLE IF EXISTS `lexicon`;

CREATE TABLE `lexicon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` varchar(255) NOT NULL COMMENT 'Short identification string representing lexicon',
  `language_name` varchar(255) NOT NULL COMMENT 'Language of lexicon',
  `name` varchar(255) NOT NULL,
  `lexicon_version` varchar(255) NOT NULL COMMENT 'Lexicon version',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `synset`
--
DROP TABLE IF EXISTS `synset`;

CREATE TABLE `synset` (
  `id` binary(16) NOT NULL,
  `split` int(11) DEFAULT NULL,
  `lexicon_id` bigint(20) NOT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `abstract` tinyint(1) DEFAULT NULL COMMENT 'is synset abstract',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`lexicon_id`) REFERENCES `lexicon` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `relation_type_allowed_lexicons`
--
DROP TABLE IF EXISTS `relation_type_allowed_lexicons`;

CREATE TABLE `relation_type_allowed_lexicons` (
  `lexicon_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`lexicon_id`,`relation_type_id`),
  FOREIGN KEY (`lexicon_id`) REFERENCES `lexicon` (`id`),
  FOREIGN KEY (`relation_type_id`) REFERENCES `relation_type` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `corpus_example`
--
DROP TABLE IF EXISTS `corpus_example`;
CREATE TABLE `corpus_example` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` text,
  `word` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `synset_attributes`
--
DROP TABLE IF EXISTS `synset_attributes`;

CREATE TABLE `synset_attributes` (
  `synset_id` binary(16) NOT NULL,
  `comment` text,
  `definition` text,
  `owner_id` bigint(20) DEFAULT NULL COMMENT 'Synset owner',
  `error_comment` text,
  `princeton_id` varchar(255) DEFAULT NULL COMMENT 'External original Princeton Id',
  `ili_id` varchar(255) DEFAULT NULL COMMENT 'OMW id',
  PRIMARY KEY (`synset_id`),
  FOREIGN KEY (`synset_id`) REFERENCES `synset` (`id`),
  FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `synset_examples`
--
DROP TABLE IF EXISTS `synset_examples`;

CREATE TABLE `synset_examples` (
  `id` binary(16) NOT NULL,
  `example` text,
  `type` varchar(30) DEFAULT NULL,
  `synset_attribute_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`synset_attribute_id`) REFERENCES `synset` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `synset_relation`
--
DROP TABLE IF EXISTS `synset_relation`;

CREATE TABLE `synset_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_synset_id` binary(16) NOT NULL,
  `child_synset_id` binary(16) NOT NULL,
  `synset_relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`synset_relation_type_id`) REFERENCES `relation_type` (`id`),
  FOREIGN KEY (`parent_synset_id`) REFERENCES `synset` (`id`),
  FOREIGN KEY (`child_synset_id`) REFERENCES `synset` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `relation_tests`
--
DROP TABLE IF EXISTS `relation_tests`;

CREATE TABLE `relation_tests` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `relation_type_id` binary(16) DEFAULT NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  `test` text,
  `element_A_part_of_speech_id` bigint(20) DEFAULT NULL,
  `element_B_part_of_speech_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`element_A_part_of_speech_id`) REFERENCES `part_of_speech` (`id`),
  FOREIGN KEY (`element_B_part_of_speech_id`) REFERENCES `part_of_speech` (`id`),
  FOREIGN KEY (`relation_type_id`) REFERENCES `relation_type` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `domain`
--
DROP TABLE IF EXISTS `domain`;

CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`description_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`name_id`) REFERENCES `application_localised_string` (`id`)
) ENGINE=InnoDB COMMENT='Table describes domain';

--
-- Table structure for table `word`
--
DROP TABLE IF EXISTS `word`;

CREATE TABLE `word` (
  `id` binary(16) NOT NULL,
  `word` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `word_index` (`word`),
  KEY `word_uuid_index` (`id`),
  CONSTRAINT UC_word UNIQUE (word)
) ENGINE=InnoDB;

--
-- Table structure for table `sense`
--
DROP TABLE IF EXISTS `sense`;

CREATE TABLE `sense` (
  `id` binary(16) NOT NULL,
  `synset_position` int(11) DEFAULT NULL COMMENT 'Position order in synset',
  `variant` int(11) NOT NULL DEFAULT '1' COMMENT 'Sense variant number',
  `domain_id` bigint(20) NOT NULL COMMENT 'Domain Id',
  `lexicon_id` bigint(20) NOT NULL COMMENT 'Lexicon Id',
  `part_of_speech_id` bigint(20) NOT NULL COMMENT 'Part of speech Id',
  `status_id` bigint(20) DEFAULT NULL,
  `synset_id` binary(16) DEFAULT NULL,
  `word_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`synset_id`) REFERENCES `synset` (`id`),
  FOREIGN KEY (`word_id`) REFERENCES `word` (`id`),
  FOREIGN KEY (`lexicon_id`) REFERENCES `lexicon` (`id`),
  FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  FOREIGN KEY (`part_of_speech_id`) REFERENCES `part_of_speech` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `dictionaries`
--
DROP TABLE IF EXISTS `dictionaries`;
CREATE TABLE `dictionaries` (
  `dtype` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description_id` bigint(20) DEFAULT NULL COMMENT 'Dictionary description',
  `name_id` bigint(20) DEFAULT NULL COMMENT 'Dictionary name',
  `tag` varchar(20) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`name_id`) REFERENCES `application_localised_string` (`id`),
  FOREIGN KEY (`description_id`) REFERENCES `application_localised_string` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `sense_attributes`
--
DROP TABLE IF EXISTS `sense_attributes`;

CREATE TABLE `sense_attributes` (
  `sense_id` binary(16) NOT NULL,
  `comment` text,
  `definition` text,
  `link` varchar(255) DEFAULT NULL,
  `register_id` bigint(20) DEFAULT NULL,
  `aspect_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `error_comment` text,
  PRIMARY KEY (`sense_id`),
  FOREIGN KEY (`sense_id`) REFERENCES `sense` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `sense_examples`
--
DROP TABLE IF EXISTS `sense_examples`;

CREATE TABLE `sense_examples` (
  `id` binary(16) NOT NULL,
  `example` text,
  `type` varchar(30) NOT NULL,
  `sense_attribute_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`sense_attribute_id`) REFERENCES `sense` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `sense_relation`
--
DROP TABLE IF EXISTS `sense_relation`;

CREATE TABLE `sense_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_sense_id` binary(16) NOT NULL,
  `child_sense_id` binary(16) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`relation_type_id`) REFERENCES `relation_type` (`id`),
  FOREIGN KEY (`child_sense_id`) REFERENCES `sense` (`id`),
  FOREIGN KEY (`parent_sense_id`) REFERENCES `sense` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `word_form`
--
DROP TABLE IF EXISTS `word_form`;

CREATE TABLE `word_form` (
  `id` binary(16) NOT NULL,
  `form` varchar(255) DEFAULT NULL,
  `tag` varchar(255)  DEFAULT NULL,
  `word` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
