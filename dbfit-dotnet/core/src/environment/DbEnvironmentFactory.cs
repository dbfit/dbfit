/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Text;

namespace dbfit {
    /// <summary>
    /// Static singleton holder for an IDbEnvironment instance that is used
    /// as the "current" environment in standalone mode. This class can also
    /// be used by 3rd party fixtures to access DbFit features or execute
    /// statements in the same transaction.
    /// </summary>
    public class DbEnvironmentFactory {
        private static IDbEnvironment instance;
        public static IDbEnvironment DefaultEnvironment
        {
            get { return instance; }
            set { instance=value; }
        }
    }
}
