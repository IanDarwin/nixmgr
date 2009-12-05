-- create role usermgr login password 'plugh32';
-- create database usermgmt owner usermgr;
-- \connect usermgmt usermgr

drop table if exists account;
create table account (
	id serial primary key,
	firstname varchar(15),
	lastname varchar(15),
	unixcreated boolean default false,
	username varchar(8),
	password varchar(8),
	lastlogin date,
	logincredits float(7),
	loginused float(7),
	printcredits integer,
	printpagesused integer
);

drop table if exists userrole;
create table userrole (
	id serial primary key,
	name varchar(8)
);

drop table if exists user_in_role;
create table user_in_role (
	id serial primary key,
	account integer references account(id),
	role integer references userrole(id)
);
