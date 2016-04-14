-- the second script that will be run by Ninja's migration engine script
CREATE TABLE AdapterConfigFile (
  id         SERIAL,
  Adapter_id BIGINT NOT NULL,
  configFile VARCHAR(255),
  PRIMARY KEY (id)

);

CREATE TABLE AdapterConfigFile_keys (
  AdapterConfigFile_id BIGINT NOT NULL,
  propertyname         VARCHAR(255),
  propertyvalue        VARCHAR(500)
);

ALTER TABLE AdapterConfigFile
ADD CONSTRAINT "fk_adapter_1"
FOREIGN KEY ("adapter_id")
REFERENCES adapter ("id")
ON DELETE CASCADE;

ALTER TABLE AdapterConfigFile_keys
ADD CONSTRAINT "fk_adapterconfigfile_1"
FOREIGN KEY (AdapterConfigFile_id)
REFERENCES AdapterConfigFile ("id")
ON DELETE CASCADE;
--insert into UserAuth (fullname, isAdmin, "password", username)
--values('Admin', true, 'q1', 'admin');