using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text;
using System.Text.RegularExpressions;

using fit;
using dbfit.util;
namespace dbfit.fixture
{
    public class ExecuteProcedure : fit.Fixture
    {
        IDbEnvironment dbEnvironment;
        DbCommand command;
        String procedureName;
        bool expectException = false;
        DbParameterAccessor[] accessors;
        int? errorCode = null;
        public ExecuteProcedure()
        {
            dbEnvironment = DbEnvironmentFactory.DefaultEnvironment;
        }
        public ExecuteProcedure(IDbEnvironment dbEnvironment, String procedureName, bool expectException)
        {
            this.procedureName = procedureName;
            this.dbEnvironment = dbEnvironment;
            this.expectException = expectException;
            this.errorCode = null;
        }
        public ExecuteProcedure(IDbEnvironment dbEnvironment, String procedureName, int errorCode)
        {
            this.procedureName = procedureName;
            this.dbEnvironment = dbEnvironment;
            this.expectException = true;
            this.errorCode = errorCode;
        }
        public ExecuteProcedure(IDbEnvironment dbEnvironment, String procedureName)
            : this(dbEnvironment, procedureName, false)
        {
        }
        public override void DoRows(Parse rows)
        {
            if (String.IsNullOrEmpty(procedureName)) procedureName = Args[0];
            if (rows != null)
            {
                InitParameters(rows.Parts);
                InitCommand();
                Parse row = rows;
                while ((row = row.More) != null)
                {
                    CheckRow(row);
                }
            }
            else
            {
                accessors = new DbParameterAccessor[0];
                InitCommand();
                command.ExecuteNonQuery();
            }
        }
        internal static DbParameterAccessor[] SortAccessors(DbParameterAccessor[] accessors)
        {
            DbParameterAccessor[] sortedAccessors = (DbParameterAccessor[])accessors.Clone();
            for (int i = 0; i < sortedAccessors.Length - 1; i++)
                for (int j = i + 1; j < sortedAccessors.Length; j++)
                {
                    if (sortedAccessors[i].Position > sortedAccessors[j].Position)
                    {
                        DbParameterAccessor x = sortedAccessors[i];
                        sortedAccessors[i] = sortedAccessors[j];
                        sortedAccessors[j] = x;
                    }
                }
            return sortedAccessors;
        }
        private void InitCommand()
        {
            command = dbEnvironment.CreateCommand(procedureName, CommandType.StoredProcedure);
            DbParameterAccessor[] sortedAccessors = SortAccessors(accessors);

            foreach (DbParameterAccessor accessor in sortedAccessors)
            {

                // in/out params can cause the same parameter to be added twice,
                // check to avoid that
                if (!command.Parameters.Contains(accessor.DbParameter))
                    command.Parameters.Add(accessor.DbParameter);
            }
        }
        private void CheckRow(Parse row)
        {
            Parse cell = row.Parts;
            //first set input params
            foreach (DbParameterAccessor accessor in accessors)
            {
                ICellHandler cellHandler = CellOperation.GetHandler(this, cell, accessor.ParameterType);

                if (!accessor.IsBoundToCheckOperation)
                {
                    cellHandler.HandleInput(this, cell, accessor);
                }
                cell = cell.More;
            }
            if (expectException)
            {
                try
                {
                    command.ExecuteNonQuery();
                    Wrong(row);
                }
                catch (Exception e)
                {
                    row.Parts.Last.More = new Parse("td",
                    Gray(e.ToString()), null, null);
                    if (errorCode.HasValue)
                    {
                        if (this.dbEnvironment.GetExceptionCode(e) == errorCode)
                            Right(row);
                        else
                            Wrong(row);
                    }
                    else
                        Right(row);
                }
            }
            else
            {
                command.ExecuteNonQuery();
                //evaluate output params
                cell = row.Parts;
                foreach (DbParameterAccessor accessor in accessors)
                {
                    ICellHandler cellHandler = CellOperation.GetHandler(this, cell, accessor.ParameterType);
                    if (accessor.IsBoundToCheckOperation)
                    {
                        cellHandler.HandleCheck(this, cell, accessor);
                    }
                    cell = cell.More;
                }
            }
        }
        private static Regex checkIsImpliedByRegex = new Regex("(\\?|!|\\(\\))$");

        private void InitParameters(Parse headerCells)
        {
            Dictionary<String, DbParameterAccessor> allParams =
                dbEnvironment.GetAllProcedureParameters(procedureName);
            accessors = new DbParameterAccessor[headerCells.Size];
            for (int i = 0; headerCells != null; i++, headerCells = headerCells.More)
            {
                String paramName = NameNormaliser.NormaliseName(headerCells.Text);
                try
                {
                    accessors[i] = DbParameterAccessor.CloneWithSameParameter(allParams[paramName]);
                }
                catch (System.Collections.Generic.KeyNotFoundException)
                {
                    Wrong(headerCells);
                    throw new ApplicationException("Cannot find parameter " + paramName);
                }
                accessors[i].IsBoundToCheckOperation = checkIsImpliedByRegex.IsMatch(headerCells.Text);
                // sql server quirk. if output parameter is used in an input column, then 
                // the param should be remapped to IN/OUT
                if ((!accessors[i].IsBoundToCheckOperation) &&
                    accessors[i].DbParameter.Direction == ParameterDirection.Output)
                    accessors[i].DbParameter.Direction = ParameterDirection.InputOutput;
            }
        }
    }
}
