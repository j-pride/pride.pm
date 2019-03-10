package xml;

import java.sql.SQLException;

import org.junit.Test;

import basic.AbstractPrideTest;
import basic.NeedsDBType;
import pm.pride.ResourceAccessor;

/**
 * To make SQLXML work on Oracle, you have to collect some libraries in a fairly obscure way.
 * The key advice came from this site:
 * <p>
 *   https://stackoverflow.com/questions/28085992/which-settings-do-i-have-to-configure-to-use-xml-as-input-parameter-with-oracle
 * <p>
 * xdb6.jar can be loaded from the Oracle Maven repository using the same version as for the
 * JDBC driver. The xmlparserv2.jar library is also available there but it doesn't work! You
 * have to follow the instructions from the site above intead, i.e. downloading JDeveloper 12
 * Java Edition and copy the JAR file to your local Maven repository.
 */
@NeedsDBType(ResourceAccessor.DBType.ORACLE)
public class PrideXMLTest extends AbstractPrideTest {
    protected void createXMLTable() throws SQLException {
        String columns = ""
        		+ "RECORD_NAME varchar(50), "
                + "DATA XMLType";
        dropAndCreateTable(XMLTestEntity.TABLE, columns);
    }

	@Override
	public void setUp() throws Exception {
		super.setUp();
    	createXMLTable();
	}

	@Test
	public void testWrite() throws Exception {
		XMLTestEntity xte = new XMLTestEntity();
		xte.setRecordName("minimal");
		xte.setData("<pride/>");
		xte.create();
		xte.commit();
	}

	@Test
	public void testRead() throws Exception {
		testWrite();
		XMLTestEntity xte = new XMLTestEntity();
		xte.setRecordName("minimal");
		xte.findByExample(XMLTestEntity.COL_RECORD_NAME);
		System.out.println(xte.getData());
	}

}
