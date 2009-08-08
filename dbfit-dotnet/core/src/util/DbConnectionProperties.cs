using System;
using System.Collections.Generic;
using System.Text;

using MbUnit.Framework;

namespace dbfit
{
    public sealed class DbConnectionProperties{
        public string Service;
        public string Username;
        public string Password;
        public string DbName;
        public string FullConnectionString;
        private DbConnectionProperties(){}
        public static DbConnectionProperties CreateFromString(String contents)
        {   
            int currLine=0;
            String[] lines = contents.Split(new char[]{'\n'});
            DbConnectionProperties props = new DbConnectionProperties();
            foreach (String line in lines)
            {
                currLine++;
                if (line==null) continue;
                String trimline=line.Trim();
                if (trimline.Length == 0) continue;
                if (trimline.StartsWith("#")) continue;
                String[] keyval = trimline.Split(new char[] { '=' },2);
                if (keyval.Length==1) throw new 
                    ApplicationException("Connection properties format incorrect, line "+currLine+ " does not contain a key-value pair ");
                String key = keyval[0].Trim().ToLower();
                String val = keyval[1].Trim();
                if ("username".Equals(key))
                {
                    props.Username = val;
                }
                else if ("password".Equals(key))
                {
                    props.Password = val;
                }
                else if ("service".Equals(key))
                {
                    props.Service = val;
                }
                else if ("database".Equals(key))
                {
                    props.DbName = val;
                }
                else if ("connection-string".Equals(key))
                {
                    props.FullConnectionString = val;
                }
                else
                {
                    throw new ApplicationException("Unsupported key in properties file:" + key);
                }

            }
            if (props.FullConnectionString != null) return props;
            if (props.Service != null && props.Username != null && props.Password != null) return props;
            throw new ApplicationException("You have to define either the full connection string; or service, username and password in the properties file");
        }
        public static DbConnectionProperties CreateFromFile(String path){
            if(!System.IO.File.Exists(path)) throw new ApplicationException("File "+path +" does not exist");
            String s;
            try{
                   s=System.IO.File.ReadAllText(path);
            }
            catch (Exception e){
                   throw new ApplicationException("Error reading file "+path,e);
            }
            return CreateFromString(s);
        }
    }
}