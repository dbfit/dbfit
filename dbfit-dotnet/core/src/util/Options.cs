using System;
using System.Collections.Generic;
using System.Text;

namespace dbfit.util
{
    public class Options
    {
        public static void reset()
        {
            fixedLengthStringParsing = false;
            bindSymbols = true;
            RemoveHandler("FailKeywordHandler");
            LoadHandler("dbfit.util.RedirectingFailHandler");
        }
        private static bool fixedLengthStringParsing = false;
        public static bool IsFixedLengthStringParsing()
        {
            return fixedLengthStringParsing;
        }
        private static bool bindSymbols = true;
        public static bool ShouldBindSymbols()
        {
            return bindSymbols;
        }

        public static void SetOption(String name, String value)
        {
            String normalname = NameNormaliser.NormaliseName(name);
            if ("fixedlengthstringparsing".Equals(normalname))
            {
                fixedLengthStringParsing = Boolean.Parse(value);
                if (fixedLengthStringParsing)
                    LoadHandler("dbfit.util.FixedLengthStringHandler");
                else
                    RemoveHandler("dbfit.util.FixedLengthStringHandler");
            }
            else if ("bindsymbols".Equals(normalname))
            {
                bindSymbols = Boolean.Parse(value);
            }
            else throw new ApplicationException("Unsupported option" + name);
        }
        private static void LoadHandler(String handler)
        {
            ((fit.CellHandlerList)fit.Configuration.Instance[fit.Configuration.FitCellHandlersKey]).
                Add(handler);
        }
        private static void RemoveHandler(String handler)
        {
            ((fit.CellHandlerList)fit.Configuration.Instance[fit.Configuration.FitCellHandlersKey]).
                Remove(handler);
        }
    }
}
