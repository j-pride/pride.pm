package pm.pride.xml;

import pm.pride.basic.AbstractPrideTest;
import pm.pride.basic.NeedsDBType;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor;

import java.sql.SQLException;

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
	protected void checkIfTestShouldBeSkipped() {
		super.checkIfTestShouldBeSkipped();
		// If we are running on Oracle (currently the only database which supports XML type at all)
		// we check if Oracle's XML type is on the classpath. E.g. on Travis CI the obscure
		// libraries mentioned above are not available and the appropriate Maven dependencies
		// are ignored (see profile "travis" in pom.xml). In this case, the tests should be
		// skipped as well rather than fail.
		if (DatabaseFactory.getDatabase().getDBType().equals(ResourceAccessor.DBType.ORACLE)) {
			try {
				Class.forName("oracle.xdb.XMLType");
			}
			catch(ClassNotFoundException cnfx) {
				String message = "Skipping XML type tests on Oracle due to missing libraries";
				System.err.println(message);
				Assumptions.assumeFalse(true, message);
			}
		}
	}
	
    protected void createXMLTable() throws SQLException {
        String columns = ""
        		+ "RECORD_NAME varchar(50), "
                + "DATA XMLType";
        dropAndCreateTable(XMLTestEntity.TABLE, columns);
    }

	@Override
	@BeforeEach
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
