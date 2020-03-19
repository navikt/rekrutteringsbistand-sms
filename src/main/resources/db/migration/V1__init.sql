CREATE TABLE sms (
    id SERIAL PRIMARY KEY,
    opprettet TIMESTAMP NOT NULL,
    sendt TIMESTAMP,
    melding TEXT NOT NULL,
    fnr VARCHAR(11) NOT NULL,
    kandidatliste_id TEXT NOT NULL,
    navident VARCHAR(7) NOT NULL,
    status TEXT NOT NULL
);
