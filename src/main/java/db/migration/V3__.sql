-- the second script that will be run by Ninja's migration engine script
alter table Adapter add
errorLogFile varchar(255);

--insert into UserAuth (fullname, isAdmin, "password", username)
--values('Admin', true, 'q1', 'admin');