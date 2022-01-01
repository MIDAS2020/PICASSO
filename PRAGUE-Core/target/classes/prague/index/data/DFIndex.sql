create table fragment(
  id int primary key,
  delids clob
);

create table cluster(
  id int primary key,
  fragments clob,
  edges long varchar
);