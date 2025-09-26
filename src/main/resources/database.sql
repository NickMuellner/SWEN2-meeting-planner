CREATE TABLE meeting
(
    id            SERIAL PRIMARY KEY,
    title         TEXT NOT NULL,
    from_datetime TIMESTAMP,
    to_datetime   TIMESTAMP,
    agenda        TEXT
);

CREATE TABLE meeting_note
(
    id         SERIAL PRIMARY KEY,
    meeting_id INTEGER NOT NULL REFERENCES meeting (id) ON DELETE CASCADE,
    note       TEXT
);