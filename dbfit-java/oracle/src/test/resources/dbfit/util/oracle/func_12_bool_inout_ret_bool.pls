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

    function t_wrapper( t_p1 IN OUT VARCHAR2 ) RETURN BOOLEAN
    is
        t_v_p1_inout BOOLEAN := t_chr2bool( t_p1 );
        t_v_ret BOOLEAN;
    begin
        t_v_ret := f_bool_inout_ret_bool( t_v_p1_inout );
        t_p1 := t_bool2chr( t_v_p1_inout );
        return t_v_ret;
    end t_wrapper;

begin
    ? := t_bool2chr( t_wrapper( ? ) );
end;

