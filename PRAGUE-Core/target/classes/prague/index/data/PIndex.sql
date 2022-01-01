create table p_index(
  id int generated always as identity primary key,
  cam varchar(500) unique,
  type char(1) not null,
  edge_num int not null,
  info long varchar,
  parent varchar(200) not null default '',
  del_ids clob
);

create index i_size on p_index (edge_num);

insert into p_index (cam, type, edge_num)
values (?, ?, ?);
select IDENTITY_VAL_LOCAL() from p_index;

select id, cam, type, edge_num, info from p_index

create table properties(
  id varchar(50) primary key,
  info varchar(50) not null
)