ALTER TABLE templateproperty ADD
Adapter_id BIGINT NOT NULL;

ALTER TABLE templateproperty
ADD CONSTRAINT property_to_adapter
FOREIGN KEY (Adapter_id)
REFERENCES Adapter ON DELETE CASCADE;