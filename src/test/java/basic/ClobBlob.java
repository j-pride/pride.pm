package basic;

import java.sql.Timestamp;

import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;

public class ClobBlob extends MappedObject {
    public static final String TABLE = "CLOB_BLOB_PRIDE_TEST";
    public static final String COL_RECORD_NAME = "record_name";
    public static final String COL_MY_CLOB = "my_clob";
    public static final String COL_MY_BLOB = "my_blob";

    private String recordName;
    private String myClob;
    private byte[] myBlob;

    public ClobBlob(String recordName) { this.recordName = recordName; }
    public ClobBlob() { }

	public String getRecordName() { return recordName; }
	public void setRecordName(String recordName) { this.recordName = recordName; }
	public String getMyClob() { return myClob; }
	public void setMyClob(String myClob) { this.myClob = myClob; }
	public byte[] getMyBlob() { return myBlob; }
	public void setMyBlob(byte[] myBlob) { this.myBlob = myBlob; }

	protected static final RecordDescriptor red =
        new RecordDescriptor(ClobBlob.class, TABLE, null)
            .rowPK( COL_RECORD_NAME, "getRecordName", "setRecordName" )
            .row( COL_MY_BLOB, "getMyBlob", "setMyBlob" )
            .row( COL_MY_CLOB, "getMyClob", "setMyClob" );

    public RecordDescriptor getDescriptor() { return red; }
   
}
