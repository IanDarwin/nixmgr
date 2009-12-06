-- userrole
insert into userrole(id,name) values(0, 'admin');
insert into userrole(id,name) values(5, 'student');

-- account
insert into account(id,firstname,lastname,email,unixcreated,username,password) values(0,'Ian','Darwin','ian@darwinsys.com','true','ian','fakePW');
insert into account(id,firstname,lastname,email,unixcreated,username,password) values(1,'Chuma','Chukwulozie','chuma@ernescliff.ca','true','chuma','fakePW');

-- user in roles
insert into user_in_role(id,account,role) values(0,0,0);
insert into user_in_role(id,account,role) values(1,0,5);

