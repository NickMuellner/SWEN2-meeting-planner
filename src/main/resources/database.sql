CREATE TABLE meeting
(
    id                SERIAL PRIMARY KEY,
    title             TEXT NOT NULL,
    "from"            TEXT,
    "to"              TEXT,
    agenda            TEXT
);

CREATE TABLE meeting_note
(
    id         SERIAL PRIMARY KEY,
    meeting_id INTEGER NOT NULL REFERENCES meeting (id) ON DELETE CASCADE,
    note       TEXT
);