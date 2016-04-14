-- the first script for migration
create table Adapter (
    id serial,
    content varchar(5000),
    postedAt timestamp,
    jarFileName varchar(255),
    jarFilePath varchar(255),
    checkStatusCommands varchar(5000),
    stopCommands varchar(5000),
    startCommands varchar(5000),
    title varchar(255),
    status varchar(255),
    primary key (id)
);

create table Adapter_authorIds (
    Adapter_id bigint not null,
    authorIds bigint
);

create table UserAuth (
    id serial,
    fullname varchar(255),
    isAdmin boolean not null,
    password varchar(255),
    username varchar(255),
    primary key (id)
);

alter table Adapter_authorIds
add constraint FK_f9ivk719aqb0rqd8my08loev7
foreign key (Adapter_id)
references Adapter;