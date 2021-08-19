ALTER TABLE dictionaries
ADD is_default BIT DEFAULT 0;

update dictionaries set is_default = 1 WHERE id = 12;