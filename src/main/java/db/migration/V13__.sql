CREATE TABLE Adapter_templateIds (
  Adapter_id  BIGINT NOT NULL,
  templateIds BIGINT
);

ALTER TABLE Adapter_templateIds
ADD CONSTRAINT adapter_to_template
FOREIGN KEY (Adapter_id)
REFERENCES Adapter;