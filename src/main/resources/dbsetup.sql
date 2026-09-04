create table if not exists data (
    PLAYER varchar(36),
    ENERGY int default 0 not null,
    COINS decimal(10,2) default 0.0 not null,
--    ITEMS text,
    GAMEINV text,
    STORAGEINV text,
    PRODUCERS text,
    ORDERS text,
    unique (PLAYER)
);