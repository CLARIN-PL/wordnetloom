CREATE TABLE tbl_morphology (
                                id                  BIGINT NOT NULL AUTO_INCREMENT,
                                sense_id            BINARY(16),
                                word_form           VARCHAR(255),
                                morphological_tag   VARCHAR(255),
                                PRIMARY KEY (id)
);
