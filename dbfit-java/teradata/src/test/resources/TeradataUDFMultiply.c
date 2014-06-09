/**********************************
**REPLACE FUNCTION Multiply (
**  n1 NUMERIC,
**  n2 NUMERIC)
**RETURNS NUMERIC
**SPECIFIC Multiply 
**LANGUAGE C
**NO SQL
**PARAMETER STYLE SQL
**DETERMINISTIC
**CALLED ON NULL INPUT
**EXTERNAL NAME 'CS!Multiply!Multiply.c'
**********************************/

#define SQL_TEXT Latin_Text  

#include "sqltypes_td.h"
#include <string.h>
#include <stdio.h>

#define IS_NULL -1
#define IS_NOT_NULL 0
#define MAXIMUM_LENGTH 16000
#define EOS '\0'
#define UDF_OK "00000"

void Multiply(NUMERIC4 *n1,
               NUMERIC4 *n2,
               NUMERIC4 *result,
               int *n1IsNull,
               int *n2IsNull,
               int *resultIsNull,
               char sqlstate[6],
               SQL_TEXT extname[129],
               SQL_TEXT specific_name[129],
               SQL_TEXT error_message[257])
{
    if ((*n1IsNull == IS_NULL) || (*n2IsNull == IS_NULL))
        {
        *resultIsNull = IS_NULL;

        (void) strcpy(sqlstate, UDF_OK);
        error_message[0] = EOS;

        return;
        }

    *result = (*n1) * (*n2);
    *resultIsNull = IS_NOT_NULL;

    (void) strcpy(sqlstate, UDF_OK);
    error_message[0] = EOS;
}
