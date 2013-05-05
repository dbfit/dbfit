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

    procedure t_wrapper( t_p1 OUT VARCHAR2, t_p2 IN BOOLEAN )
    is
        t_v_p1_out BOOLEAN;
    begin
        proc_4_bool_out_bool_in( t_v_p1_out, t_p2 );
        t_p1 := t_bool2chr( t_v_p1_out );
    end t_wrapper;

begin
    t_wrapper( ?, t_chr2bool( ? ) );
end;

