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

    function t_chr2bool( p_arg VARCHAR2 ) return BOOLEAN
    is
    begin
        if ( p_arg = 'true' )
        then
            return true;
        elsif ( p_arg = 'false' )
        then
            return false;
        elsif ( p_arg is null )
        then
            return null;
        else
            raise_application_error( -20013, 'Error. Expected true or false, got: ' || p_arg );
        end if;
    end t_chr2bool;

    function t_wrapper( t_p1 IN BOOLEAN, t_p2 OUT VARCHAR2, t_p3 IN OUT VARCHAR2, t_p4 IN VARCHAR2, t_p5 IN OUT NUMBER ) RETURN BOOLEAN
    is
        t_v_p2_out BOOLEAN;
        t_v_p3_inout BOOLEAN := t_chr2bool( t_p3 );
        t_v_ret BOOLEAN;
    begin
        t_v_ret := f_bool_all_mix_ret_bool( t_p1, t_v_p2_out, t_v_p3_inout, t_p4, t_p5 );
        t_p2 := t_bool2chr( t_v_p2_out );
        t_p3 := t_bool2chr( t_v_p3_inout );
        return t_v_ret;
    end t_wrapper;

begin
    ? := t_bool2chr( t_wrapper( t_chr2bool( ? ), ?, ?, ?, ? ) );
end;

