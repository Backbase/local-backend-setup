alter session set container=BACKBASE;

CREATE USER access_control identified by b4ckb4s3 default tablespace users quota unlimited on users;
GRANT connect, create session, create table, create sequence to access_control;

CREATE USER arrangement_manager identified by b4ckb4s3 default tablespace users quota unlimited on users;
GRANT connect, create session, create table, create sequence to arrangement_manager;

CREATE USER user_manager identified by b4ckb4s3 default tablespace users quota unlimited on users;
GRANT connect, create session, create table, create sequence to user_manager;

CREATE USER backbase_identity identified by b4ckb4s3 default tablespace users quota unlimited on users;
GRANT connect, create session, create table, create sequence to backbase_identity;
GRANT SELECT ON SYS.DBA_RECYCLEBIN TO BACKBASE_IDENTITY;