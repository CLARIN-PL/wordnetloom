START TRANSACTION;

INSERT INTO tbl_morphology (word_form, morphological_tag, sense_id)
SELECT
    m.word_form,
    m.morphological_tag,
    s.id
FROM wordnet_work.morphology m INNER JOIN wordnet.tbl_sense s
    ON m.lexicalunit_id = s.legacy_id;

COMMIT;
