ALTER TABLE CUPPYMATCH
    ADD COLUMN p_stadium BIGINT;

CREATE CACHED TABLE cuppytrailstadium
(
    hjmpTS BIGINT,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    modifiedTS TIMESTAMP,
    createdTS TIMESTAMP,
    PK BIGINT,
    p_capacity INTEGER,
    p_code NVARCHAR(255),
    p_stadiumtype BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX stadiumRelIDX_161 ON CUPPYMATCH (p_stadium);

