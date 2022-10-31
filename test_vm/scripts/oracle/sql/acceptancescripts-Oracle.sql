whenever sqlerror exit sql.sqlcode
whenever oserror exit 9

define tbs_data = USERS

create user dftest identified by dftest default tablespace &&tbs_data quota unlimited on &&tbs_data.;
grant connect to dftest;
grant create session to dftest;
grant create procedure to dftest;
grant create table to dftest;
grant resource to dftest;
grant create procedure to dftest;

alter session set current_schema = dftest;

create or replace procedure ConcatenateStrings(firstString varchar2, secondString varchar2, concatenated out varchar2) as
begin
	concatenated:=firstString || ' ' || secondString;
end;
/

create or replace function ConcatenateF(firstString varchar2, secondString varchar2) return varchar as
begin
	return firstString || ' ' || secondString;
end;
/

create procedure CalcLength(name varchar2, strlength out number) as
begin
	strlength:=length(name);
end;
/

create sequence s1 start with 1;


create table users(name varchar2(50), username varchar2(50), userid number primary key);

create or replace procedure makeuser as
begin
	insert into users (name,username) values ('user1','fromproc');
end;
/

CREATE OR REPLACE TRIGGER USERS_BIE
BEFORE INSERT ON USERS
FOR EACH ROW
BEGIN
	SELECT s1.NEXTVAL INTO :new.userid FROM dual;
END;
/

create or replace package RCTest as
	type URefCursor IS REF CURSOR RETURN USERS%ROWTYPE;
	procedure TestRefCursor (howmuch number,lvlcursor out URefCursor);
end;
/

create or replace package body RCTest as
	procedure TestRefCursor (
		howmuch number,
		lvlcursor out URefCursor
	)
	as
	begin
		for i in 1..howmuch loop
			insert into users(name, username) values ('User '||i, 'Username'||i);
		end loop;
		open lvlcursor for
			select * from users;
	end TestRefCursor;
end;
/

create or replace function Multiply(n1 number, n2 number) return number as
begin
	return n1*n2;
end;
/

Create table clobtypetest (s1 number(5), c2 CLOB);

create or replace package RCLOBTest as
	type URefCursor IS REF CURSOR RETURN clobtypetest%ROWTYPE;
	procedure TestRefCursor (howmany number,outcursor out URefCursor);
end;
/

create or replace package body RCLOBTest as
	procedure TestRefCursor (
		howmany number,
		outcursor out URefCursor
	)
	as
	begin
		OPEN outcursor FOR
			SELECT * FROM clobtypetest
			WHERE s1<=howmany;
	end;
end;
/

create or replace procedure raise_error_with_params(name in varchar2, strlength out number) as
begin
    if (name = 'xx') then
        raise_application_error(-20001, 'test exception');
    end if;
    strlength := LENGTH(name);
end;
/

create or replace procedure raise_error_no_params as
begin
    raise_application_error(-20001, 'test exception');
end;
/


create user dfsyntest identified by dfsyntest default tablespace &&tbs_data;

alter user dfsyntest quota unlimited on &&tbs_data;

alter session set current_schema = dfsyntest;

create or replace procedure dfsyntest.standaloneproc(num1 number, num2 out number) as
begin
	num2:=2*num1;
end;
/

create or replace public synonym synstandaloneproc for dfsyntest.standaloneproc;

create or replace package dfsyntest.pkg as
	procedure pkgproc(num1 number, num2 out number);
end;
/

create or replace package body dfsyntest.pkg as
	procedure pkgproc(num1 number, num2 out number) as
	begin
		num2:=2*num1;
	end;
end;
/

create or replace public synonym synpkg for dfsyntest.pkg;

grant execute on synstandaloneproc to dftest;

grant execute on dfsyntest.pkg to dftest;

create table dfsyntest.animals(id number, name varchar2(100 char), arrival_tstamp timestamp);
grant select,insert,update on dfsyntest.animals to dftest;
create or replace synonym dftest.prv_syn_animals for dfsyntest.animals;
create or replace public synonym pub_syn_animals for dfsyntest.animals;

spool /tmp/dbfit-ora-setup-done.flag
prompt DBIFT ORACLE SETUP DONE
spool off

exit

