package xml;

import java.sql.SQLXML;

import basic.NeedsDBType;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;
import pm.pride.ResourceAccessor;

public class XMLTestEntity extends MappedObject {
    public static final String TABLE = "XML_TEST_ENTITY";
    public static final String COL_RECORD_NAME = "RECORD_NAME";
    public static final String COL_DATA = "DATA";

    String recordName;
    String data;

    public String getRecordName() { return recordName; }
	public void setRecordName(String recordName) { this.recordName = recordName; }
	public String getData() { return data; }
	public void setData(String data) { this.data = data; }

	protected static final RecordDescriptor red =
        new RecordDescriptor(XMLTestEntity.class, TABLE, null)
            .row( COL_RECORD_NAME, "getRecordName", "setRecordName" )
            .row( COL_DATA, "getData", "setData", SQLXML.class );

    public RecordDescriptor getDescriptor() { return red; }
    
}
