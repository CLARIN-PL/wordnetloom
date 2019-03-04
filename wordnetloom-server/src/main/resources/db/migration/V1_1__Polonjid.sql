--
-- Table structure for table `yiddish_sense_extension`
--
DROP TABLE IF EXISTS `yiddish_sense_extension`;

CREATE TABLE `yiddish_sense_extension` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sense_id` binary(16) DEFAULT NULL,
  `comment` text,
  `context` text,
  `etymology` text,
  `spelling_latin` varchar(255) DEFAULT NULL,
  `meaning` text,
  `variant` varchar(255) DEFAULT NULL,
  `etymological_root` varchar(255) DEFAULT NULL,
  `spelling_yiddish` varchar(255) DEFAULT NULL,
  `spelling_yivo` varchar(255) DEFAULT NULL,
  `attested_id` bigint(20) DEFAULT NULL,
  `grammatical_gender_id` bigint(20) DEFAULT NULL,
  `lexical_characteristic_id` bigint(20) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `style_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sense_fk` (`sense_id`),
  KEY `attested_id` (`attested_id`),
  KEY `grammatical_gender_id` (`grammatical_gender_id`),
  KEY `lexical_characteristic_id` (`lexical_characteristic_id`),
  KEY `source_id` (`source_id`),
  KEY `status_id` (`status_id`),
  KEY `style_id` (`style_id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_1` FOREIGN KEY (`sense_id`) REFERENCES `tbl_sense` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_2` FOREIGN KEY (`attested_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_3` FOREIGN KEY (`grammatical_gender_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_4` FOREIGN KEY (`lexical_characteristic_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_5` FOREIGN KEY (`source_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_6` FOREIGN KEY (`status_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_sense_extension_ibfk_7` FOREIGN KEY (`style_id`) REFERENCES `tbl_dictionaries` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `yiddish_domain`
--
DROP TABLE IF EXISTS `yiddish_domain`;

CREATE TABLE `yiddish_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domain_id` bigint(20) NOT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `extension_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `domain_id` (`domain_id`),
  KEY `modifier_id` (`modifier_id`),
  KEY `extension_id` (`extension_id`),
  CONSTRAINT `yiddish_domain_ibfk_1` FOREIGN KEY (`domain_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_domain_ibfk_2` FOREIGN KEY (`modifier_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_domain_ibfk_3` FOREIGN KEY (`extension_id`) REFERENCES `yiddish_sense_extension` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `yiddish_extension_source`
--
DROP TABLE IF EXISTS `yiddish_extension_source`;

CREATE TABLE `yiddish_extension_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sense_extension_id` bigint(20) NOT NULL,
  `source_dictionary_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sense_extension_id` (`sense_extension_id`),
  KEY `source_dictionary_id` (`source_dictionary_id`),
  CONSTRAINT `yiddish_extension_source_ibfk_1` FOREIGN KEY (`sense_extension_id`) REFERENCES `yiddish_sense_extension` (`id`),
  CONSTRAINT `yiddish_extension_source_ibfk_2` FOREIGN KEY (`source_dictionary_id`) REFERENCES `tbl_dictionaries` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `yiddish_inflection`
--
DROP TABLE IF EXISTS `yiddish_inflection`;

CREATE TABLE `yiddish_inflection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(255) NOT NULL,
  `prefix_id` bigint(20) NOT NULL,
  `extension_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `prefix_id` (`prefix_id`),
  KEY `extension_id` (`extension_id`),
  CONSTRAINT `yiddish_inflection_ibfk_1` FOREIGN KEY (`prefix_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_inflection_ibfk_2` FOREIGN KEY (`extension_id`) REFERENCES `yiddish_sense_extension` (`id`)
) ENGINE=InnoDB;

--
-- Table structure for table `yiddish_particles`
--
DROP TABLE IF EXISTS `yiddish_particles`;

CREATE TABLE `yiddish_particles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtype` varchar(31) NOT NULL,
  `position` int(11) DEFAULT '0',
  `root` varchar(255) DEFAULT NULL,
  `constituent` varchar(255) DEFAULT NULL,
  `extension_id` bigint(20) DEFAULT NULL,
  `particle_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `extension_id` (`extension_id`),
  KEY `particle_id` (`particle_id`),
  CONSTRAINT `yiddish_particles_ibfk_1` FOREIGN KEY (`extension_id`) REFERENCES `yiddish_sense_extension` (`id`),
  CONSTRAINT `yiddish_particles_ibfk_2` FOREIGN KEY (`particle_id`) REFERENCES `tbl_dictionaries` (`id`)
) ENGINE=InnoDB;


--
-- Table structure for table `yiddish_transcriptions`
--
DROP TABLE IF EXISTS `yiddish_transcriptions`;

CREATE TABLE `yiddish_transcriptions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phonography` varchar(255)  NOT NULL,
  `transcription_id` bigint(20) NOT NULL,
  `extension_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `transcription_id` (`transcription_id`),
  KEY `extension_id` (`extension_id`),
  CONSTRAINT `yiddish_transcriptions_ibfk_1` FOREIGN KEY (`transcription_id`) REFERENCES `tbl_dictionaries` (`id`),
  CONSTRAINT `yiddish_transcriptions_ibfk_2` FOREIGN KEY (`extension_id`) REFERENCES `yiddish_sense_extension` (`id`)
) ENGINE=InnoDB;
