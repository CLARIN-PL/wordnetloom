INSERT INTO emotional_annotations (id, sense_id, has_emotional_characteristic,super_anotation,emotions,valuations, markedness, example1, example2, user_name)
  SELECT id, lexicalunit_id, unitStatus, super_anotation, emotions, valuations, markedness, example1, example2, owner
  FROM wordnet_work.emotion;