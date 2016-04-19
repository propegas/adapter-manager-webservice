CREATE TABLE TemplateProperty (
  id               SERIAL,
  propertyName     VARCHAR(255),
  propertyValue    VARCHAR(500),
  createdXmlFileId VARCHAR(128),
  PRIMARY KEY (id)
);
