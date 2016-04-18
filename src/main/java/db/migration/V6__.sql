CREATE TABLE GlobalConfig (
  id                  SERIAL,
  propertyname        VARCHAR(255),
  propertyvalue       VARCHAR(500),
  propertyDescription VARCHAR(5000),
  PRIMARY KEY (id)
);

CREATE TABLE AdapterTemplate (
  id              SERIAL,
  templateName    VARCHAR(255),
  templateXmlPath VARCHAR(255),
  PRIMARY KEY (id)
);