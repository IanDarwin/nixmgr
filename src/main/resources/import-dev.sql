-- userrole
insert into userrole(id,name) values(0, 'admin');
insert into userrole(id,name) values(5, 'student');

-- account
insert into account(id,firstname,lastname,unixcreated,username,password) values(0,'Ian','Darwin',f,'ian','fakePW');

-- user in roles
insert into user_in_role(id,account,userrole) values(0,0,0);
insert into user_in_role(id,account,userrole) values(0,0,5);

