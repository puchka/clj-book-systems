drop table if exists requests;
create table requests (
    id            serial primary key,
    path          text not null,
    ip            inet not null,
    is_processed  boolean not null default false,
    zip           text,
    country       text,
    city          text,
    lat           float,
    lon           float
);

