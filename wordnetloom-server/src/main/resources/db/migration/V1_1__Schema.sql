--
-- Table structure for table `tbl_application_localised_string`
--
DROP TABLE IF EXISTS `tbl_application_localised_string`;
CREATE TABLE `tbl_application_localised_string` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` text,
  `language` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`language`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_corpus_example`
--
DROP TABLE IF EXISTS `tbl_corpus_example`;
CREATE TABLE `tbl_corpus_example` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` text,
  `word` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `word_index` (`word`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_dictionaries`
--
DROP TABLE IF EXISTS `tbl_dictionaries`;
CREATE TABLE `tbl_dictionaries` (
  `dtype` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description_id` bigint(20) DEFAULT NULL COMMENT 'Dictionary description',
  `name_id` bigint(20) DEFAULT NULL COMMENT 'Dictionary name',
  `tag` varchar(20) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  `color` varchar(7) DEFAULT NULL,
  `is_default` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FKflyxm5y0r293f9s1sv4q7weix` (`description_id`),
  KEY `FK11lr8u8vfj0m3dv9hmxpj5653` (`name_id`),
  CONSTRAINT `FK11lr8u8vfj0m3dv9hmxpj5653` FOREIGN KEY (`name_id`) REFERENCES `tbl_application_localised_string` (`id`),
  CONSTRAINT `FKflyxm5y0r293f9s1sv4q7weix` FOREIGN KEY (`description_id`) REFERENCES `tbl_application_localised_string` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_domain`
--
DROP TABLE IF EXISTS `tbl_domain`;

CREATE TABLE `tbl_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhgtdmfui3wtjng46asuqfa79b` (`description_id`),
  KEY `FKilj10y6a5e5wvfxr4otivxy8f` (`name_id`),
  CONSTRAINT `FKhgtdmfui3wtjng46asuqfa79b` FOREIGN KEY (`description_id`) REFERENCES `tbl_application_localised_string` (`id`),
  CONSTRAINT `FKilj10y6a5e5wvfxr4otivxy8f` FOREIGN KEY (`name_id`) REFERENCES `tbl_application_localised_string` (`id`)
) ENGINE=InnoDB COMMENT='Table describes domain';

--
-- Table structure for table `tbl_part_of_speech`
--
DROP TABLE IF EXISTS `tbl_part_of_speech`;

CREATE TABLE `tbl_part_of_speech` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name_id` bigint(20) DEFAULT NULL COMMENT 'Name of part of speech',
  `color` varchar(255) DEFAULT NULL COMMENT 'Color displayed on visualisation',
  PRIMARY KEY (`id`),
  KEY `FKqgj4aq3ne5hjb61eo7gagdngw` (`name_id`),
  CONSTRAINT `FKqgj4aq3ne5hjb61eo7gagdngw` FOREIGN KEY (`name_id`) REFERENCES `tbl_application_localised_string` (`id`)
) ENGINE=InnoDB COMMENT='Table describes parts of speech';

--
-- Table structure for table `tbl_lexicon`
--
DROP TABLE IF EXISTS `tbl_lexicon`;

CREATE TABLE `tbl_lexicon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` varchar(255) NOT NULL COMMENT 'Short identification string representing lexicon',
  `language_name` varchar(255) NOT NULL COMMENT 'Language of lexicon',
  `language_shortcut` varchar(5) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `lexicon_version` varchar(255) NOT NULL COMMENT 'Lexicon version',
  `license` varchar(255) DEFAULT NULL COMMENT 'Lexicon license',
  `email` varchar(255) DEFAULT NULL COMMENT 'Contact person email',
  `reference_url` varchar(255) DEFAULT NULL,
  `citation` text,
  `confidence_score` varchar(255) DEFAULT NULL,
  `description` text,
  `read_only` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_word`
--
DROP TABLE IF EXISTS `tbl_word`;

CREATE TABLE `tbl_word` (
  `id` binary(16) NOT NULL,
  `word` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `word_index` (`word`),
  KEY `word_uuid_index` (`id`),
  CONSTRAINT UC_word UNIQUE (word)
) ENGINE=InnoDB;


--
-- Table structure for table `tbl_synset`
--
DROP TABLE IF EXISTS `tbl_synset`;

CREATE TABLE `tbl_synset` (
  `id` binary(16) NOT NULL,
  `lexicon_id` bigint(20) NOT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `abstract` tinyint(1) DEFAULT NULL COMMENT 'is synset abstract',
  PRIMARY KEY (`id`),
  KEY `FKfxflmrbnq64hax2r7gs1gbeuj` (`lexicon_id`),
  KEY `synset_uuid_index` (`id`),
  CONSTRAINT `FKfxflmrbnq64hax2r7gs1gbeuj` FOREIGN KEY (`lexicon_id`) REFERENCES `tbl_lexicon` (`id`)
) ENGINE=InnoDB;


--
-- Table structure for table `tbl_sense`
--
DROP TABLE IF EXISTS `tbl_sense`;

CREATE TABLE `tbl_sense` (
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
  KEY `FKeuhrtymboieklw932horawdvk` (`domain_id`),
  KEY `FKa45bf1te6qdk0wu7441xrdhvv` (`lexicon_id`),
  KEY `FKjvdptha3oq3lsr3kt3f04lo5u` (`part_of_speech_id`),
  KEY `sense_uuid_index` (`id`),
  KEY `FK_sense_synset` (`synset_id`),
  KEY `FK_sense_word` (`word_id`),
  CONSTRAINT `FK_sense_synset` FOREIGN KEY (`synset_id`) REFERENCES `tbl_synset` (`id`),
  CONSTRAINT `FK_sense_word` FOREIGN KEY (`word_id`) REFERENCES `tbl_word` (`id`),
  CONSTRAINT `FKa45bf1te6qdk0wu7441xrdhvv` FOREIGN KEY (`lexicon_id`) REFERENCES `tbl_lexicon` (`id`),
  CONSTRAINT `FKeuhrtymboieklw932horawdvk` FOREIGN KEY (`domain_id`) REFERENCES `tbl_domain` (`id`),
  CONSTRAINT `FKjvdptha3oq3lsr3kt3f04lo5u` FOREIGN KEY (`part_of_speech_id`) REFERENCES `tbl_part_of_speech` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_relation_tests`
--
DROP TABLE IF EXISTS `tbl_relation_tests`;

CREATE TABLE `tbl_relation_tests` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `relation_type_id` binary(16) DEFAULT NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  `test` text,
  `element_A_part_of_speech_id` bigint(20) DEFAULT NULL,
  `element_B_part_of_speech_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9q4toynhnsw62qcw0rws2n6ym` (`element_A_part_of_speech_id`),
  KEY `FKfx1y4ddftr5baay3du383k9kh` (`element_B_part_of_speech_id`),
  KEY `FK_relation_test_relation_type` (`relation_type_id`),
  CONSTRAINT `FK9q4toynhnsw62qcw0rws2n6ym` FOREIGN KEY (`element_A_part_of_speech_id`) REFERENCES `tbl_part_of_speech` (`id`),
  CONSTRAINT `FKfx1y4ddftr5baay3du383k9kh` FOREIGN KEY (`element_B_part_of_speech_id`) REFERENCES `tbl_part_of_speech` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_relation_type`
--
DROP TABLE IF EXISTS `tbl_relation_type`;

CREATE TABLE `tbl_relation_type` (
  `id` binary(16) NOT NULL,
  `auto_reverse` bit(1) NOT NULL DEFAULT b'0' COMMENT 'On true application will create automatically reversed relation',
  `multilingual` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Relation between two lexicons',
  `description_id` bigint(20) DEFAULT NULL,
  `display_text_id` bigint(20) DEFAULT NULL,
  `name_id` bigint(20) DEFAULT NULL,
  `relation_argument` varchar(255) DEFAULT NULL COMMENT 'Describes type of relation',
  `global_wordnet_relation_type` varchar(255) DEFAULT NULL,
  `short_display_text_id` bigint(20) DEFAULT NULL COMMENT 'Text displayed on visualisation',
  `color` varchar(255) DEFAULT NULL COMMENT 'Color of displayed relation',
  `node_position` varchar(255) DEFAULT NULL COMMENT 'Position in node LEFT,TOP,RIGHT,BOTTOM',
  `priority` int(11) DEFAULT NULL,
  `parent_relation_type_id` binary(16) DEFAULT NULL,
  `reverse_relation_type_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3qs6td1pvv97n4834gc95s1w` (`description_id`),
  KEY `FK7nfuf14f6hfcb6goi3bqbqgms` (`display_text_id`),
  KEY `FKk1msw7t7lxfr5ciyfqpvdncip` (`name_id`),
  KEY `FKkd3s4gwfo72pasivl4jvtqnr9` (`short_display_text_id`),
  KEY `relation_type_uuid_index` (`id`),
  KEY `FK_relation_type_parent` (`parent_relation_type_id`),
  KEY `FK_relation_type_reverse` (`reverse_relation_type_id`),
  CONSTRAINT `FK3qs6td1pvv97n4834gc95s1w` FOREIGN KEY (`description_id`) REFERENCES `tbl_application_localised_string` (`id`),
  CONSTRAINT `FK7nfuf14f6hfcb6goi3bqbqgms` FOREIGN KEY (`display_text_id`) REFERENCES `tbl_application_localised_string` (`id`),
  CONSTRAINT `FKk1msw7t7lxfr5ciyfqpvdncip` FOREIGN KEY (`name_id`) REFERENCES `tbl_application_localised_string` (`id`),
  CONSTRAINT `FKkd3s4gwfo72pasivl4jvtqnr9` FOREIGN KEY (`short_display_text_id`) REFERENCES `tbl_application_localised_string` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_relation_type_allowed_lexicons`
--
DROP TABLE IF EXISTS `tbl_relation_type_allowed_lexicons`;

CREATE TABLE `tbl_relation_type_allowed_lexicons` (
  `lexicon_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`lexicon_id`,`relation_type_id`),
  KEY `FK_relation_type_allowed_lexicons_relation` (`relation_type_id`),
  CONSTRAINT `FK5ynuaw5d0qyhywfxj0u8vxuyl` FOREIGN KEY (`lexicon_id`) REFERENCES `tbl_lexicon` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_relation_type_allowed_parts_of_speech`
--
DROP TABLE IF EXISTS `tbl_relation_type_allowed_parts_of_speech`;

CREATE TABLE `tbl_relation_type_allowed_parts_of_speech` (
  `part_of_speech_id` bigint(20) NOT NULL,
  `relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`part_of_speech_id`,`relation_type_id`),
  KEY `FK_relation_type_allowed_pos_relation` (`relation_type_id`),
  CONSTRAINT `FK5ynuaw5d0qyhywfxj0u8vxuylzxc` FOREIGN KEY (`part_of_speech_id`) REFERENCES `tbl_part_of_speech` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_sense_attributes`
--
DROP TABLE IF EXISTS `tbl_sense_attributes`;

CREATE TABLE `tbl_sense_attributes` (
  `sense_id` binary(16) NOT NULL,
  `comment` text,
  `definition` text,
  `link` varchar(255) DEFAULT NULL,
  `register_id` bigint(20) DEFAULT NULL,
  `error_comment` text,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sense_id`),
  CONSTRAINT `FK_sense_attributes_sense` FOREIGN KEY (`sense_id`) REFERENCES `tbl_sense` (`id`)
) ENGINE=InnoDB;


--
-- Table structure for table `tbl_sense_examples`
--
DROP TABLE IF EXISTS `tbl_sense_examples`;

CREATE TABLE `tbl_sense_examples` (
  `id` binary(16) NOT NULL,
  `example` text,
  `type` varchar(30) NOT NULL,
  `sense_attribute_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sense_examples_uuid_index` (`id`),
  KEY `FK_sense_examples_attribute` (`sense_attribute_id`),
  CONSTRAINT `FK_sense_examples_attribute` FOREIGN KEY (`sense_attribute_id`) REFERENCES `tbl_sense` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_sense_relation`
--
DROP TABLE IF EXISTS `tbl_sense_relation`;

CREATE TABLE `tbl_sense_relation` (
  `parent_sense_id` binary(16) NOT NULL,
  `child_sense_id` binary(16) NOT NULL,
  `sense_relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`child_sense_id`,`parent_sense_id`,`sense_relation_type_id`),
  KEY `FK_sense_relation_child` (`child_sense_id`),
  KEY `FK_sense_relation_parent` (`parent_sense_id`),
  KEY `FK_sense_relation_relation` (`sense_relation_type_id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_synset_attributes`
--
DROP TABLE IF EXISTS `tbl_synset_attributes`;

CREATE TABLE `tbl_synset_attributes` (
  `synset_id` binary(16) NOT NULL,
  `comment` text,
  `definition` text,
  `error_comment` text,
  `user_name` varchar(255) DEFAULT NULL,
  `princeton_id` varchar(255) DEFAULT NULL COMMENT 'External original Princeton Id',
  `ili_id` varchar(255) DEFAULT NULL COMMENT 'OMW id',
  PRIMARY KEY (`synset_id`),
  CONSTRAINT `FK_synset_attributes_synset` FOREIGN KEY (`synset_id`) REFERENCES `tbl_synset` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_synset_examples`
--
DROP TABLE IF EXISTS `tbl_synset_examples`;

CREATE TABLE `tbl_synset_examples` (
  `id` binary(16) NOT NULL,
  `example` text,
  `type` varchar(30) DEFAULT NULL,
  `synset_attribute_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `synset_examples_uuid_index` (`id`),
  KEY `FK_synset_examples_attribute` (`synset_attribute_id`),
  CONSTRAINT `FK_synset_examples_attribute` FOREIGN KEY (`synset_attribute_id`) REFERENCES `tbl_synset` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_synset_relation`
--
DROP TABLE IF EXISTS `tbl_synset_relation`;

CREATE TABLE `tbl_synset_relation` (
  `parent_synset_id` binary(16) NOT NULL,
  `child_synset_id` binary(16) NOT NULL,
  `synset_relation_type_id` binary(16) NOT NULL,
  PRIMARY KEY (`child_synset_id`,`parent_synset_id`,`synset_relation_type_id`),
  KEY `FK_synset_relation_child` (`child_synset_id`),
  KEY `FK_synset_relation_parent` (`parent_synset_id`),
  KEY `FK_synset_relation_relation` (`synset_relation_type_id`)
) ENGINE=InnoDB;

--
-- Table structure for table `tbl_word_form`
--
DROP TABLE IF EXISTS `tbl_word_form`;

CREATE TABLE `tbl_word_form` (
  `id` binary(16) NOT NULL,
  `form` varchar(255) DEFAULT NULL,
  `tag` varchar(255)  DEFAULT NULL,
  `word` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;