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

    procedure t_wrapper( t_p1 OUT VARCHAR2 )
    is
        t_v_p1_out BOOLEAN;
    begin
        proc_3_bool_out( t_v_p1_out );
        t_p1 := t_bool2chr( t_v_p1_out );
    end t_wrapper;

begin
    t_wrapper( ? );
end;

