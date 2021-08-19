
# Change user id to user full name in sense attributes
ALTER TABLE sense_attributes
DROP FOREIGN KEY FKhnsf3ffqr27ceqnrji9g69hxp;

ALTER TABLE sense_attributes
DROP COLUMN user_id;

ALTER TABLE sense_attributes
ADD COLUMN user_name varchar(255) DEFAULT NULL;


# Change user id to user full name in synset attributes
ALTER TABLE synset_attributes
DROP FOREIGN KEY FKd4daq7s6mjs49n2flpjndk0ob;

ALTER TABLE synset_attributes
DROP COLUMN owner_id;

ALTER TABLE synset_attributes
ADD COLUMN user_name varchar(255) DEFAULT NULL;


# Drop user and user settings tables
DROP TABLE users_settings;
DROP TABLE users;
