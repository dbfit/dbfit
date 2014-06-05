## Execute Ddl

 `Execute Ddl` is intended for executing DDL SQL statements (like `create`, `alter`, `drop`). It's similar to `Execute` with the main difference that it doesn't support bind variables, and also automatically handles required post-execute activities, if any (e.g. Teradata requires transaction to be closed after each DDL statement).

 The statement is specified as the first fixture parameter. There are no additional rows required for this command.

    !|Execute Ddl|create table tab_with_trigger(x int)|

    !|Execute Ddl|create or replace trigger trg_double_x before insert on tab_with_trigger for each row begin :new.x := :new.x * 2; end;|

    !|Insert|tab_with_trigger|
    |x                       |
    |13                      |

    !|Query|select x from tab_with_trigger|
    |x                                    |
    |26                                   |

    !|Execute Ddl|drop table tab_with_trigger|

