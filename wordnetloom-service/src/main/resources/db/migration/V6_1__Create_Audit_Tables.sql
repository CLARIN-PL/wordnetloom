create table hibernate_sequence (
                                    next_val bigint
) engine=InnoDB;

create table REVINFO (
                         REV integer not null,
                         REVTSTMP bigint,
                         user_email varchar(255),
                         primary key (REV)
) engine=InnoDB;

create table tbl_application_localised_string_AUD (
                                                      id bigint not null,
                                                      language varchar(255) not null,
                                                      REV integer not null,
                                                      REVTYPE tinyint,
                                                      value text,
                                                      primary key (id, language, REV)
) engine=InnoDB;

create table tbl_corpus_example_AUD (
                                        id bigint not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        text longtext,
                                        word varchar(255),
                                        primary key (id, REV)
) engine=InnoDB;

create table tbl_dictionaries_AUD (
                                      id bigint not null,
                                      REV integer not null,
                                      dtype varchar(31) not null,
                                      REVTYPE tinyint,
                                      description_id bigint,
                                      is_default bit,
                                      name_id bigint,
                                      color varchar(255),
                                      primary key (id, REV)
) engine=InnoDB;

create table tbl_domain_AUD (
                                id bigint not null,
                                REV integer not null,
                                REVTYPE tinyint,
                                description_id bigint,
                                name_id bigint,
                                primary key (id, REV)
) engine=InnoDB;

create table tbl_lexicon_AUD (
                                 id bigint not null,
                                 REV integer not null,
                                 REVTYPE tinyint,
                                 citation longtext,
                                 confidence_score varchar(255),
                                 email varchar(255),
                                 identifier varchar(255),
                                 language_name varchar(255),
                                 language_shortcut varchar(255),
                                 lexicon_version varchar(255),
                                 license varchar(255),
                                 name varchar(255),
                                 reference_url varchar(255),
                                 primary key (id, REV)
) engine=InnoDB;

create table tbl_part_of_speech_AUD (
                                        id bigint not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        color varchar(255),
                                        name_id bigint,
                                        primary key (id, REV)
) engine=InnoDB;

create table tbl_relation_tests_AUD (
                                        id bigint not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        position int default 0,
                                        test longtext,
                                        relation_type_id BINARY(16),
                                        element_A_part_of_speech_id bigint,
                                        element_B_part_of_speech_id bigint,
                                        primary key (id, REV)
) engine=InnoDB;

create table tbl_relation_type_allowed_lexicons_AUD (
                                                        REV integer not null,
                                                        relation_type_id BINARY(16) not null,
                                                        lexicon_id bigint not null,
                                                        REVTYPE tinyint,
                                                        primary key (REV, relation_type_id, lexicon_id)
) engine=InnoDB;

create table tbl_relation_type_allowed_parts_of_speech_AUD (
                                                               REV integer not null,
                                                               relation_type_id BINARY(16) not null,
                                                               part_of_speech_id bigint not null,
                                                               REVTYPE tinyint,
                                                               primary key (REV, relation_type_id, part_of_speech_id)
) engine=InnoDB;

create table tbl_relation_type_AUD (
                                       id BINARY(16) not null,
                                       REV integer not null,
                                       REVTYPE tinyint,
                                       auto_reverse bit default 0,
                                       color varchar(255),
                                       description_id bigint,
                                       display_text_id bigint,
                                       global_wordnet_relation_type varchar(255),
                                       multilingual bit default 0,
                                       name_id bigint,
                                       node_position varchar(255),
                                       relation_argument varchar(255),
                                       short_display_text_id bigint,
                                       parent_relation_type_id BINARY(16),
                                       reverse_relation_type_id BINARY(16),
                                       primary key (id, REV)
) engine=InnoDB;

create table tbl_sense_attributes_AUD (
                                          sense_id BINARY(16) not null,
                                          REV integer not null,
                                          REVTYPE tinyint,
                                          comment longtext,
                                          definition longtext,
                                          error_comment text,
                                          link varchar(255),
                                          user_name varchar(255),
                                          register_id bigint,
                                          primary key (sense_id, REV)
) engine=InnoDB;

create table tbl_sense_AUD (
                               id BINARY(16) not null,
                               REV integer not null,
                               REVTYPE tinyint,
                               synset_position int default 0,
                               variant int default 1,
                               domain_id bigint,
                               lexicon_id bigint,
                               part_of_speech_id bigint,
                               status_id bigint,
                               synset_id BINARY(16),
                               word_id BINARY(16),
                               primary key (id, REV)
) engine=InnoDB;

create table tbl_sense_examples_AUD (
                                        id BINARY(16) not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        example text,
                                        type varchar(255),
                                        sense_attribute_id binary(255),
                                        primary key (id, REV)
) engine=InnoDB;

create table tbl_sense_relation_AUD (
                                        sense_relation_type_id BINARY(16) not null,
                                        parent_sense_id BINARY(16) not null,
                                        child_sense_id BINARY(16) not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        primary key (sense_relation_type_id, parent_sense_id, child_sense_id, REV)
) engine=InnoDB;

create table tbl_synset_attributes_AUD (
                                           synset_id binary(16) not null,
                                           REV integer not null,
                                           REVTYPE tinyint,
                                           comment longtext,
                                           definition longtext,
                                           error_comment text,
                                           user_name varchar(255),
                                           ili_id varchar(255),
                                           princeton_id varchar(255),
                                           primary key (synset_id, REV)
) engine=InnoDB;

create table tbl_synset_AUD (
                                id BINARY(16) not null,
                                REV integer not null,
                                REVTYPE tinyint,
                                abstract bit,
                                lexicon_id bigint,
                                status_id bigint,
                                primary key (id, REV)
) engine=InnoDB;

create table tbl_synset_examples_AUD (
                                         id BINARY(16) not null,
                                         REV integer not null,
                                         REVTYPE tinyint,
                                         example text,
                                         type varchar(255),
                                         synset_attribute_id binary(255),
                                         primary key (id, REV)
) engine=InnoDB;

create table tbl_synset_relation_AUD (
                                         synset_relation_type_id BINARY(16) not null,
                                         parent_synset_id BINARY(16) not null,
                                         child_synset_id BINARY(16) not null,
                                         REV integer not null,
                                         REVTYPE tinyint,
                                         primary key (synset_relation_type_id, parent_synset_id, child_synset_id, REV)
) engine=InnoDB;

create table tbl_word_AUD (
                              id BINARY(16) not null,
                              REV integer not null,
                              REVTYPE tinyint,
                              word varchar(255),
                              primary key (id, REV)
) engine=InnoDB;

create table tbl_word_form_AUD (
                                   id BINARY(16) not null,
                                   REV integer not null,
                                   REVTYPE tinyint,
                                   form varchar(255),
                                   tag varchar(255),
                                   word varchar(255),
                                   primary key (id, REV)
) engine=InnoDB;

CREATE TABLE tbl_emotional_annotations_AUD (
                                       id BINARY(16) not null,
                                       sense_id BINARY(16) not null,
                                       REV integer not null,
                                       REVTYPE tinyint,
                                       has_emotional_characteristic BIT DEFAULT 0 NOT NULL,
                                       super_anotation       BIT DEFAULT 0 NOT NULL,
                                       markedness_id         BIGINT,
                                       legacy_id         BIGINT,
                                       example1              VARCHAR(512),
                                       example2              VARCHAR(512),
                                       user_name             VARCHAR(255),
                                       PRIMARY KEY (id, REV)
) engine=InnoDB;

CREATE TABLE tbl_sense_emotions_AUD (
                                   annotation_id BINARY(16) not null,
                                   emotion bigint not null,
                                   REV integer not null,
                                   REVTYPE tinyint,
                                   PRIMARY KEY (annotation_id, emotion, REV)
) engine=InnoDB;

CREATE TABLE tbl_sense_valuations_AUD (
                                        annotation_id BINARY(16) not null,
                                        valuation bigint not null,
                                        REV integer not null,
                                        REVTYPE tinyint,
                                        PRIMARY KEY (annotation_id, valuation, REV)
) engine=InnoDB;

alter table tbl_emotional_annotations_AUD
    add constraint FKq3m6a3milrp2vio4ehdjc86ob
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_emotions_AUD
    add constraint FKq3m6a3kilrp2vio7ehdjc86ob
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_valuations_AUD
    add constraint FKq3m6t3kilrp2vio7ehdjc46ob
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_application_localised_string_AUD
    add constraint FKexdjhefv63pv6vckb0cxggpck
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_corpus_example_AUD
    add constraint FKq3s7u0s6nnedwjf6bp3y0ycv1
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_dictionaries_AUD
    add constraint FK3fojakfe5nlcoionvhk27fcq
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_domain_AUD
    add constraint FKc9wv7yu8l4r8sl5hefjil50dr
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_lexicon_AUD
    add constraint FKqa17rb27yir50oy51vl7qij8u
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_part_of_speech_AUD
    add constraint FKaebflfgud63haqeds36nycqs6
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_relation_tests_AUD
    add constraint FK4ai75jsv8hdc5b42b6fjr0i0s
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_relation_type_allowed_lexicons_AUD
    add constraint FK92mkn6wt4yddmnum2m61867k9
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_relation_type_allowed_parts_of_speech_AUD
    add constraint FKjp7rwk10y23yw0pirxd7nmv63
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_relation_type_AUD
    add constraint FKqtefjiv4xwysnwlt6m96q8bfp
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_attributes_AUD
    add constraint FKcw3if20nywbwnebwje111br83
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_AUD
    add constraint FKdd05gykqw6j2the46pcu8okpx
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_examples_AUD
    add constraint FKgbcpp0c4tpfuujfhfrwmu11yt
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_sense_relation_AUD
    add constraint FK50pr8impdgwcij026qwg8jjg1
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_synset_attributes_AUD
    add constraint FK373unwlylae223axqbqmlfw42
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_synset_AUD
    add constraint FK9ax99u3bcxu27yo86mhkrbid3
        foreign key (REV)
            references REVINFO (REV);

alter table tbl_synset_examples_AUD
    add constraint FKb6npm70ueyhvpjhh4jdvb6q4w
        foreign key (REV)
            references REVINFO (REV);
