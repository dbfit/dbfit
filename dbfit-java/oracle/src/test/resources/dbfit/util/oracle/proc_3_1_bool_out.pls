declare
    function t_bool2chr( p_arg BOOLEAN ) return VARCHAR2
    is
    begin
        if ( p_arg is null )
        then
            return null;
        elsif ( p_arg )
        then
            return 'true';
        else
            return 'false';
        end if;
    end t_bool2chr;

    procedure t_wrapper( t_p_1 OUT VARCHAR2 )
    is
        tt_v_out_1 BOOLEAN;
    begin
        proc_3_bool_out( tt_v_out_1 );
        t_p_1 := t_bool2chr( tt_v_out_1 );
    end t_wrapper;
begin
    t_wrapper( ? );
end;

