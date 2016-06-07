CREATE TABLE AdapterEvent (
  id            SERIAL,
  Adapter_id    BIGINT NOT NULL,
  "timestamp"   TIMESTAMP,
  message       VARCHAR(5000),
  repeatCounter INT4,
  PRIMARY KEY (id)

);

ALTER TABLE AdapterEvent
ADD CONSTRAINT "fk_event_adapter_1"
FOREIGN KEY ("adapter_id")
REFERENCES adapter ("id")
ON DELETE CASCADE;

