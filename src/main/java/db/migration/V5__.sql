-- the second script that will be run by Ninja's migration engine script
alter table AdapterConfigFile add
configDescription varchar(5000);

alter table AdapterConfigFile_keys add
propertyDescription varchar(5000);