ALTER TABLE sms ADD stilling_id TEXT;
ALTER TABLE sms ADD dirty BOOLEAN;

CREATE INDEX sms_stilling_id_index ON sms (stilling_id);
CREATE INDEX sms_dirty_index ON sms (dirty);
CREATE INDEX sms_kandidatliste_id_index ON sms (kandidatliste_id);
CREATE INDEX sms_fnr_index ON sms (fnr);
