/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Text;
using fit;
using System.Data;
using dbfit.util;

namespace dbfit.fixture
{
    /// <summary>
    /// This fixture compares two DataTable objects and prints out any unmatched rows.
    /// DataTables are read from Fixture symbols.
    /// </summary>
    public class CompareStoredQueries:fit.Fixture
    {
        private IDbEnvironment dbEnvironment;
        private String symbol1;
        private String symbol2;
        private DataTable dt1;
        private DataTable dt2;
        private String[] columnNames;
        private bool[] keyProperties;
        /// <summary>
        /// Constructor that will use the default environment (from DbEnvironmentFactory) and
        /// read the DataTable objects to compare from symbols contained in the first 
        /// two fixture arguments. Intended for use in standalone mode.
        /// </summary>
        public CompareStoredQueries()
        {
            dbEnvironment = DbEnvironmentFactory.DefaultEnvironment;
        }
        /// <summary>
        /// Constructor that allows the caller to pass an IDbEnvironment instance and two symbol names
        /// that will be used to retrieve DataTable objects for comparison. Intended for use in flow mode.
        /// </summary>
        /// <param name="environment"></param>
        /// <param name="symbol1">Symbol name for the fixture symbol containing the first DataTable</param>
        /// <param name="symbol2">Symbol name for the fixture symbol containing the second DataTable</param>
        public CompareStoredQueries(IDbEnvironment environment, String symbol1, String symbol2)
        {
            this.dbEnvironment = environment;
            this.symbol1 = symbol1;
            this.symbol2 = symbol2;
        }
        private DataTable GetDataTable(String symbolName){
		    Object o=Fixture.Recall(symbolName);
		    if (o==null) throw new ApplicationException("Cannot load a stored query from "+symbolName+  " - is is empty");
		    if (o.GetType().Equals(typeof(DataTable))) return (DataTable) o;
		    throw new ApplicationException("Cannot load stored query from "+symbolName 
                + " - object type is "+o.GetType().Name);
	    }
        private void InitialiseDataTables()
        {
            if (symbol1 == null || symbol2 == null)
            {
                if (Args.Length < 2) 
                    throw new ApplicationException("No symbols specified to CompareStoreQueries constructor or argument list");
                symbol1 = Args[0];
                symbol2 = Args[1];
            }
            if (symbol1.StartsWith("<<")) symbol1 = symbol1.Substring(2);
            if (symbol2.StartsWith("<<")) symbol2 = symbol2.Substring(2);
            // tables will be modified during comparison, so better to clone
            dt1 = GetDataTable(symbol1).Copy();
            dt2 = GetDataTable(symbol2).Copy();
        }
        private void LoadRowStructure(Parse headerRow)
        {
            Parse headerCell = headerRow.Parts;
            int colNum = headerRow.Parts.Size;
            columnNames = new String[colNum];
            keyProperties = new bool[colNum];
            for (int i = 0; i < colNum; i++)
            {
                String currentName = headerCell.Text;
                if (currentName == null) throw new ApplicationException("Column " + i + " does not have a name");
                currentName = currentName.Trim();
                if (currentName.Length == 0) throw new ApplicationException("Column " + i + " does not have a name");
                columnNames[i] = NameNormaliser.NormaliseName(currentName);
                keyProperties[i] = !currentName.EndsWith("?");
                headerCell = headerCell.More;
            }
        }
        public override void DoTable(Parse table)
        {
		    InitialiseDataTables();		
		    Parse lastRow=table.Parts.More;
		    if (lastRow==null){
			    throw new ApplicationException("Query structure missing from second row");
		    }
		    LoadRowStructure(lastRow);
		    lastRow=ProcessDataTable(dt1,dt2, lastRow, symbol2);
    		
		    foreach (DataRow dr in dt2.Rows){
			    lastRow=AddRow(lastRow, dr, true, " missing from "+symbol1);
		    }
	    }

        private String GetStringValue(DataRow dr, String colName)
        {
            Object o = dr[colName];
            if (o == null) return "null";
            return o.ToString();
        }

        private Parse AddRow(Parse lastRow, DataRow dr, bool markAsError, String desc)
        {
            Parse newRow = new Parse("tr", null, null, null);
            lastRow.More = newRow;
            lastRow = newRow;
            try
            {
                Parse firstCell = new Parse("td",
                        GetStringValue(dr, columnNames[0]), null, null);
                newRow.Parts = firstCell;
                if (markAsError)
                {
                    firstCell.AddToBody(Fixture.Gray(desc));
                    this.Wrong(firstCell);
                }
                for (int i = 1; i < columnNames.Length; i++)
                {
                    Parse nextCell = new Parse("td",
                            GetStringValue(dr, columnNames[i]), null, null);
                    firstCell.More = nextCell;
                    firstCell = nextCell;
                }
            }
            catch (Exception e)
            {
                this.Exception(newRow, e);
            }
            return lastRow;
        }
        private Parse addRow(Parse lastRow, DataRow dr, DataRow dr2)
        {
            Parse newRow = new Parse("tr", null, null, null);
            lastRow.More = newRow;
            lastRow = newRow;
            try
            {
                String lval = GetStringValue(dr, columnNames[0]);
                String rval = GetStringValue(dr2, columnNames[0]);
                Parse firstCell = new Parse("td", lval, null, null);
                newRow.Parts = firstCell;
                if (!lval.Equals(rval))
                {
                    Wrong(firstCell, rval);
                }
                else
                {
                    Right(firstCell);
                }
                for (int i = 1; i < columnNames.Length; i++)
                {
                    lval = GetStringValue(dr, columnNames[i]);
                    rval = GetStringValue(dr2, columnNames[i]);
                    Parse nextCell = new Parse("td",
                            lval, null, null);
                    firstCell.More = nextCell;
                    firstCell = nextCell;
                    if (!lval.Equals(rval))
                    {
                        Wrong(firstCell, rval);
                    }
                    else
                    {
                        Right(firstCell);
                    }
                }
            }
            catch (Exception e)
            {
                Exception(newRow, e);
            }
            return lastRow;
        }
        private bool IsMatch(DataRow dr, IDictionary<String, Object> matchingMask)
        {
           foreach (String key in matchingMask.Keys){
               object val = matchingMask[key];
               object drval = dr[key];
               if (val == null){
                    if (drval!=null) return false;   
               }
               else {
                   if (!val.Equals(drval)) return false;
               }                   
           }
           return true;
        }
        private DataRow FindMatching(DataTable t, IDictionary<String, Object> matchingMask)
        {
            foreach (DataRow dr in t.Rows){
                   if (IsMatch(dr,matchingMask)) return dr;
            }
            return null;
        }
        private Parse ProcessDataTable(DataTable t1, DataTable t2, Parse lastScreenRow, String queryName)
        {

            foreach (DataRow dr in t1.Rows)
            {
                IDictionary<String, Object> matchingMask = new Dictionary<String, Object>();
                for (int i = 0; i < keyProperties.Length; i++)
                {
                    if (keyProperties[i])
                        matchingMask[columnNames[i]] = dr[columnNames[i]];
                }
                DataRow dr2 = FindMatching(t2, matchingMask);
                if (dr2 != null)
                {
                    lastScreenRow = addRow(lastScreenRow, dr, dr2);
                    t2.Rows.Remove(dr2);
                }
                else
                {
                    lastScreenRow = AddRow(lastScreenRow, dr, true, " missing from " + queryName);
                }
            }
            return lastScreenRow;
        }
    }
}
