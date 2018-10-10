drop table if exists todo;
create table todo (
  id int primary key auto_increment not null,
  task varchar(256) not null,
  opened long not null,
  closed long not null
);