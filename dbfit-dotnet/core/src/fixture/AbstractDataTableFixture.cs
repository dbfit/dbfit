/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Text;
using fit;
using System.Data;
using System.Data.Common;
using System.Text.RegularExpressions;
using dbfit.util;
namespace dbfit
{
        /// <summary>
        /// Generic implementation of RowFixture-like test class that works
        /// with System.Data.Common.DataTable objects instead of arrays. It supports
        /// partial row-key matching and uses the current DataRow as a target object
        /// while calling cell handlers.
        /// </summary>
        public abstract class AbstractDataTableFixture : Fixture
        {
            private DataColumnAccessor[] accessors;
            private DataTable dataTable;
            private static Regex checkIsImpliedByRegex = new Regex("(\\?|!|\\(\\))$");

            protected abstract DataTable GetDataTable();
            protected abstract bool IsOrdered { get;}
           
            private DataColumn GetColumn(String name)
            {
                foreach (DataColumn c in dataTable.Columns)
                {
                    if (name.Equals(NameNormaliser.NormaliseName(c.ColumnName)))
                        return c;
                }
                throw new ColumnMissingException(name);
            }

            private void ReadColumnNames(Parse headerCells)
            {
                accessors = new DataColumnAccessor[headerCells.Size];
                for (int i = 0; headerCells != null; i++, headerCells = headerCells.More)
                {
                    String columnName = NameNormaliser.NormaliseName(headerCells.Text);
                    accessors[i] = new DataColumnAccessor(GetColumn(columnName), !checkIsImpliedByRegex.IsMatch(headerCells.Text));
                }
            }
            public override void DoRows(Parse rows)
            {
                dataTable = GetDataTable();
                ReadColumnNames(rows.Parts);
                Parse row = rows;
                while ((row = row.More) != null)
                {
                    DataRow match = FindMatchingTableRow(row, dataTable);
                    if (match == null)
                    {
                        MarkRowAsMissing(row);
                    }
                    else
                    {
                        CheckMatchingRow(row, match);
                        dataTable.Rows.Remove(match);
                    }
                }
                AddSurplusRows(rows, dataTable);
            }
            private DataRow FindMatchingTableRow(Parse row, DataTable table)
            {
                if (IsOrdered)
                {
                    if (table.Rows.Count > 0) return table.Rows[0];
                }
                else
                {
                    foreach (DataRow dataRow in table.Rows)
                    {
                        if (IsMatch(row, dataRow)) return dataRow;
                    }
                }
                return null;
            }
            private void CheckMatchingRow(Parse row, DataRow d)
            {

                SetTargetObject(d);

                Parse cell = row.Parts;
                foreach (Accessor accessor in accessors)
                {
                    ICellHandler cellHandler = CellOperation.GetHandler(this,cell, accessor.ParameterType);

                    cellHandler.HandleCheck(this, cell, accessor);
                    cell = cell.More;
                }
            }
            private bool IsMatch(Parse row, DataRow d)
            {
                SetTargetObject(d);
                Parse cell = row.Parts;
                foreach (DataColumnAccessor accessor in accessors)
                {
                    ICellHandler cellHandler = CellOperation.GetHandler(this,cell, accessor.ParameterType);
                    if (accessor.IsUsedForMatching()
                        && (!cellHandler.HandleEvaluate(this, cell, accessor)))
                        return false;
                    cell = cell.More;
                }
                return true;
            }
            private void AddSurplusRows(Parse rows, DataTable remaining)
            {
                foreach (DataRow datarow in remaining.Rows)
                    AddSurplusRow(rows, datarow);
            }

            private void AddSurplusRow(Parse rows, DataRow dr)
            {
                Parse cell = null;
                SetTargetObject(dr);
                foreach (Accessor accessor in accessors)
                {
                    Parse newCell = new Parse("td",
                        Gray(GetStringValue(accessor.Get(this)))
                        , null, null);
                    if (cell == null)
                        cell = newCell;
                    else
                        cell.Last.More = newCell;
                }
                AddRowToTable(cell, rows);
                MarkRowAsSurplus(rows.Last);
            }

            private void AddRowToTable(Parse cells, Parse rows)
            {
                rows.Last.More = new Parse("tr", null, cells, null);
            }
            private void MarkRowAsMissing(Parse row)
            {
                Wrong(row.Parts);
                row.Parts.AddToBody(Label("missing"));
            }

            private void MarkRowAsSurplus(Parse row)
            {
                Wrong(row.Parts);
                row.Parts.AddToBody(Label("surplus"));
            }
            public static string GetStringValue(Object o)
            {
                return (o == null) ? "null" : o.ToString();
            }
            private DataRow currentRow;
            private void SetTargetObject(DataRow dr)
            {
                currentRow = dr;
            }
            public override object GetTargetObject()
            {
                return currentRow;
            }
        }
}