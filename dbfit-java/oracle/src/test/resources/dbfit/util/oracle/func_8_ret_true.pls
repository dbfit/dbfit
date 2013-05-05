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

begin
    ? := t_bool2chr( f_ret_true() );
end;

