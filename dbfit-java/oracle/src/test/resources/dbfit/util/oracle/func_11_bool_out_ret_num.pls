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

    function t_wrapper( t_p1 OUT VARCHAR2 ) RETURN NUMBER
    is
        t_v_p1_out BOOLEAN;
        t_v_ret NUMBER;
    begin
        t_v_ret := f_bool_out_ret_num( t_v_p1_out );
        t_p1 := t_bool2chr( t_v_p1_out );
        return t_v_ret;
    end t_wrapper;

begin
    ? := t_wrapper( ? );
end;

