package basic;

import java.sql.SQLException;

import org.junit.Test;

import pm.pride.ResourceAccessor;

@SkipForDBType(ResourceAccessor.DBType.SQLITE)
public class ClobBlobTest extends AbstractPrideTest {
    protected void createClobBlobTable() throws SQLException {
        String columns = ""
        		+ "RECORD_NAME varchar(50), "
                + "MY_CLOB CLOB, "
                + "MY_BLOB BLOB";
        dropAndCreateTable(ClobBlob.TABLE, columns);
    }

	@Override
	public void setUp() throws Exception {
		super.setUp();
    	createClobBlobTable();
	}

	@Test
	public void testInsertReadNull() throws Exception {
		String recordName = "null";
		ClobBlob cb = new ClobBlob(recordName);
		cb.create();
		cb.commit();
		cb = new ClobBlob(recordName);
		cb.findXE();
		assertNull(cb.getMyBlob());
		assertNull(cb.getMyClob());
	}
	
	@Test
	public void testInsertReadClob() throws Exception {
		String recordName = "clob";
		String clobContent = "myclob";
		ClobBlob cb = new ClobBlob(recordName);
		cb.setMyClob(clobContent);
		cb.create();
		cb.commit();
		cb = new ClobBlob(recordName);
		cb.findXE();
		assertEquals(clobContent, cb.getMyClob());
	}
	
	@Test
	public void testInsertReadBlob() throws Exception {
		String recordName = "blob";
		String blobContentString = "myblob content";
		byte[] blobContent = blobContentString.getBytes();
		ClobBlob cb = new ClobBlob(recordName);
		cb.setMyBlob(blobContent);
		cb.create();
		cb.commit();
		cb = new ClobBlob(recordName);
		cb.findXE();
		assertEquals(blobContentString, new String(cb.getMyBlob()));
		assertArrayEquals(blobContent, cb.getMyBlob());
	}
	
}

