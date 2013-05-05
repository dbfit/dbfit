declare
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

begin
    proc_1( t_chr2bool( ? ) );
end;

