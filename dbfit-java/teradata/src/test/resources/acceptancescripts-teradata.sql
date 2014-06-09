create user dftest from sysadmin as password=dftest spool=3000000 temporary=1000000 permanent=1000000;

grant create function on dftest to dbc;
grant create procedure on dftest to dbc; 

database dftest;

create table users(name varchar(50), username varchar(50), userid numeric);

grant create function on dftest to dftest;
grant execute function on dftest to dftest;
grant create procedure on dftest to dftest;
grant execute procedure on dftest to dftest;

replace procedure nulls_back(out out1 varchar, out out2 numeric, out out3 date)
begin
   set out1 = null;
   set out2 = null;
   set out3 = null;
end;

replace procedure ConcatenateStrings(in firstString varchar, in secondString varchar, out concatenated varchar)
begin
	set concatenated=firstString || ' ' || secondString;
end;

replace function ConcatenateF(firstString varchar, secondString varchar) returns varchar
language sql
contains sql
deterministic
collation invoker
inline type 1
	return firstString || ' ' || secondString;

replace procedure CalcLength(in name varchar(100), out strlength numeric)
begin
	set strlength=characters(name);
end;

replace procedure MultiplyIO(IN factor number, INOUT val number)
begin
	set val = val*factor;
end;

replace procedure dftest.TestProc1(in name varchar(100), out strlength number)
begin
	declare my_exception_condition condition for sqlstate '22012';
	-- SQLSTATE 22012 maps to Teradata error number 2618.
	declare x integer;
	if (name = 'xx')
	then
		signal my_exception_condition;
	end if;
	set strlength = characters(name);
end;

replace procedure makeuser()
begin
	insert into users (name,username) values ('user1','fromproc');
end;