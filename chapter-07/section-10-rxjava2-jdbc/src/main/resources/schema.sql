create table wallet
(
  id integer auto_increment primary key,
  owner varchar(255) not null,
  balance integer not null,
  deposits integer not null,
  withdraws integer not null
);

create table book
(
  id varchar(64) primary key,
  title varchar(255) not null,
  publishing_year integer not null
);