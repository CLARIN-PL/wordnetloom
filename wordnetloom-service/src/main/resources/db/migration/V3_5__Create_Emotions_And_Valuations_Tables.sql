START TRANSACTION;

CREATE TABLE unit_emotions (
	annotation_id BIGINT,
    emotion BIGINT,
    PRIMARY KEY(annotation_id, emotion),
    FOREIGN KEY (annotation_id) REFERENCES emotional_annotations(id),
    FOREIGN KEY (emotion) REFERENCES dictionaries(id)
);

CREATE TABLE unit_valuations (
	annotation_id BIGINT,
    valuation BIGINT,
    PRIMARY KEY (annotation_id, valuation),
    FOREIGN KEY (annotation_id) REFERENCES emotional_annotations(id),
    FOREIGN KEY (valuation) REFERENCES dictionaries(id)
);

DELIMITER $$

ALTER TABLE emotional_annotations
ADD markedness_id BIGINT;

ALTER TABLE emotional_annotations
ADD CONSTRAINT FK_annotations_markedness FOREIGN KEY (markedness_id) REFERENCES dictionaries(id);

DELIMITER $$

COMMIT;

