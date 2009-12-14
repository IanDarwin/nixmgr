-- create role usermgr login password 'plugh32';
-- create database usermgmt owner usermgr;
-- \connect usermgmt usermgr

drop table if exists account;
create table account (
	id serial primary key,
	firstname varchar(15) not null,
	lastname varchar(15) not null,
	unixcreated boolean default false,
	username varchar(8) not null,
	password varchar(32) not null,
	email varchar(100) not null,
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

drop table if exists account_userrole;
create table account_userrole (
	account_id integer not null references account(id),
	roles_id integer not null references userrole(id)
);

create table whitelist (
	id serial primary key,
	name varchar(255)
);

