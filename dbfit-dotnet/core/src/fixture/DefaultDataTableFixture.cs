/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0

using System;
using System.Collections.Generic;
using System.Text;
using fit;
using System.Data;
using System.Data.Common;

namespace dbfit.fixture
{
    /// <summary>
    /// An in-memory implementation of AbstractDataTableFixture. Allows callers
    /// to pass an already prepared DataTable
    /// </summary>
    public class DefaultDataTableFixture : AbstractDataTableFixture
    {
        private DataTable dataTable;
        private bool isOrdered;
        public DefaultDataTableFixture(DataTable table, bool isOrdered)
        {
            this.dataTable = table;
            this.isOrdered = isOrdered;
        }
        protected override DataTable GetDataTable()
        {
            return dataTable;
        }
        protected override bool IsOrdered { get { return isOrdered; } }
    }
}