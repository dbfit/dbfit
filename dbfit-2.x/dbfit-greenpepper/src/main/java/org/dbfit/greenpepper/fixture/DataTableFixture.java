package org.dbfit.greenpepper.fixture;
import com.greenpepper.reflect.Fixture;
import com.greenpepper.reflect.Message;
import com.greenpepper.reflect.NoSuchMessageException;

import dbfit.util.DataRow;
import dbfit.util.DataTable;
import dbfit.util.NameNormaliser;

public class DataTableFixture implements Fixture{
	class DataColumnReader extends Message {
		private DataRow dataRow;
		public DataColumnReader(DataRow dataRow, String key) {
			super();
			this.dataRow = dataRow;
			this.key = key;
		}
		private String key;		
	    @Override
	    public Object send( String... args ) throws Exception
	    {
	        assertArgumentsCount( args );
	        return dataRow.getStringValue(NameNormaliser.normaliseName(key));
	    }
	    @Override
	    public int getArity()
	    {
	        return 0;
	    }	    	   
	}
	class DataRowFixture implements Fixture{
		private DataRow dataRow;
		DataRowFixture (DataRow dataRow){
			this.dataRow=dataRow;
		}
		public Fixture fixtureFor(Object arg0) {
			return this;
		}
		public boolean canCheck(String arg0) {
			return true;
		}
		public boolean canSend(String arg0) {
			return false;
		}
		public Message check(String header) throws NoSuchMessageException {
			return new DataColumnReader(dataRow,header);
		}
		public Object getTarget() {
			return this;
		}
		public Message send(String arg0) throws NoSuchMessageException {
			throw new UnsupportedOperationException("send is not supported by data table fixture");
		}
		
	}
	
	protected DataTable dataTable;
	protected DataTableFixture(){
	}
	public DataTableFixture(DataTable dt){
		this.dataTable=dt;
	}
	@Override
	public boolean canCheck(String arg0) {
		return false;
	}
	@Override
	public boolean canSend(String arg0) {
		return false;
	}
	@Override
	public Message check(String arg0) throws NoSuchMessageException {
		throw new UnsupportedOperationException("send is not supported by data table fixture");
	}
	@Override
	public Fixture fixtureFor(Object dataRow) {
		if (!(dataRow instanceof DataRow)) 
			throw new UnsupportedOperationException("DataTableFixture can only wrap DataRow objects");
		return new DataRowFixture((DataRow)dataRow);
	}
	@Override
	public Object getTarget() {
		return dataTable.getUnprocessedRows();
	}
	@Override
	public Message send(String arg0) throws NoSuchMessageException {
		throw new UnsupportedOperationException("send is not supported by data table fixture");
	}
	
}
