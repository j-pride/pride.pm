package basic;

import org.junit.Test;

import pm.pride.SQL;
import pm.pride.SQLExpressionBuilder;
import static pm.pride.SQLExpressionBuilder.Validation.*;

public class PrideSQLExpressionBuilderTest extends AbstractPrideTest {

	private static final Object TABLE_DELTA = "DELTAS";
	private static final Object COLUMN_HEADID = "HEAD_ID";
	private static final Object COLUMN_PARTNERID = "PARTNER_ID";
	private static final Object COLUMN_CAMPAIGN_ID = "CAMPAIGN_ID";
	private static final Object COLUMN_LAST_TRX_IMPORT_RELEVANCE = "LAST_TRX_IMPORT_RELEVANCE";
	private static final Object TABLE_PROMOTION = "PROMOTION";
	private static final Object COLUMN_CURRENTFLAG = "CURRENT_FLAG";
	private static final Object COLUMN_PUBLICPROMOTIONID = "PUBLIC_PROMOTION_ID";
	private static final Object COLUMN_ACTIVATIONCHANNEL = "ACTIVATION_CHANNEL";
	private static final Object COLUMN_PROMOTION_HEAD_ID = "PROMOTION_HEAD_ID";

	
	@Override
	public void setUp() throws Exception {
		// Avoid database initialization. We don't need anything like that here
		// This speeds up the test dramatically
	}

	@Test
	public void testSimpleExpression() {
        String result = SQL.build("@ONE, @TWO, @THREE, @ONE", "one", 2, 3);
		assertEquals("one, 2, 3, one", result);
	}
	
	@Test
	public void testMixed() {
        String result = SQL.build("@ONE, @TWO, @THREE, %s, @ONE", "one", 2, 3, "string");
		assertEquals("one, 2, 3, string, one", result);
	}

	@Test
	public void testMixedWithDecimal() {
        String result = SQL.build("@ONE, @TWO, @THREE, %d, @ONE", "one", 2, "string", 3);
		assertEquals("one, 2, string, 3, one", result);
	}

	@Test
	public void testMixedWithPositionSpecs() {
        String result = SQL.build("@ONE, %3$s, @TWO, %3$s, @ONE", "one", 2, 3);
		assertEquals("one, 3, 2, 3, one", result);
	}
	
	@Test
	public void testWithPositionSpec() {
        String result = SQL.build("@2$ONE, @1$TWO", "one", 2);
		assertEquals("2, one", result);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSameNameDifferentPositions() {
        SQL.build("@1$ONE @2$ONE", "irrelevant");
	}
	
	@Test
	public void testNameWithAndWithoutPosition() {
		assertEquals("1 1", SQL.build("@1$ONE @ONE", 1));
	}
	
	@Test
	public void testNameWithPositionOverridesAllNamesWithout() {
		assertEquals("1 2 2", SQL.build("@TWO @ONE @ONE", 1, 2));
		assertEquals("1 1 1", SQL.build("@TWO @ONE @1$ONE", 1, 2));
		assertEquals("1 1 1 1", SQL.build("@TWO @ONE @1$ONE @ONE", 1, 2));
	}
	
	@Test
	public void testDifferentNamesSamePosition() {
		assertEquals("1 1", SQL.build("@1$ONE @1$TWO", 1));
	}
	
	@Test
	public void testNoExceptionOnMatchingNamesCaseInsensitive() {
		SQL.buildx("@ONE", "One");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExceptionOnWrongName() {
		SQL.buildx("@ONE", "ON");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExceptionOnWrongNameByChangedDefault() {
		try {
			SQLExpressionBuilder.validationDefault = ExceptionCaseSensitive;
			SQL.build("@ONE", "one");
		}
		finally {
			SQLExpressionBuilder.validationDefault = None;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotEnoughArguments() {
		SQL.buildx("@ONE");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExceptionOnWrongNameCaseInsensitive() {
		new SQLExpressionBuilder(ExceptionCaseSensitive).format("@ONE", "One");
	}
	
	@Test
	public void testWarningOnWrongNameCaseInsensitive() {
		new SQLExpressionBuilder(WarningCaseSensitive).format("@ONE", "One");
	}
	
	@Test
	public void testRealisticExample() {
		String fromClause = SQL.build(
				"from @DELTA_TABLE delta " +
	            "  JOIN (SELECT /*+ NO_USE_NL (p) NO_USE_NL (pp))*/ " +
	            "    p.@HEAD_ID, pp.@PARTNER_ID, pp.@HEAD_ID, pp.@CAMPAIGN_ID, pp.@LAST_TRX_IMPORT_RELEVANCE " +
	            "    FROM @DMD_PRO_PROMOTION p JOIN @DMD_PRO_PROMOTION pp ON " +
	            "      pp.@CURRENT_FLAG = 1 AND pp.@PUBLIC_PROMOTION_ID = p.@PUBLIC_PROMOTION_ID AND pp.@ACTIVATION_CHANNEL = p.@ACTIVATION_CHANNEL " +
	            "    WHERE p.@CURRENT_FLAG = 1) pro " +
	            "  ON (pro.@HEAD_ID = delta.@PROMOTION_HEAD_ID AND delta.@PARTNER_ID = pro.@PARTNER_ID) OR " +
	            "     (pro.@HEAD_ID = delta.@PROMOTION_HEAD_ID AND delta.@PARTNER_ID IS NULL) ",
	            TABLE_DELTA,
	            COLUMN_HEADID, COLUMN_PARTNERID, COLUMN_CAMPAIGN_ID, COLUMN_LAST_TRX_IMPORT_RELEVANCE, 
	            TABLE_PROMOTION, 
	            COLUMN_CURRENTFLAG, COLUMN_PUBLICPROMOTIONID, COLUMN_ACTIVATIONCHANNEL,
	            COLUMN_PROMOTION_HEAD_ID);
		assertEquals("from DELTAS delta   JOIN (SELECT /*+ NO_USE_NL (p) NO_USE_NL (pp))*/     p.HEAD_ID, pp.PARTNER_ID, pp.HEAD_ID, pp.CAMPAIGN_ID, pp.LAST_TRX_IMPORT_RELEVANCE     FROM PROMOTION p JOIN PROMOTION pp ON       pp.CURRENT_FLAG = 1 AND pp.PUBLIC_PROMOTION_ID = p.PUBLIC_PROMOTION_ID AND pp.ACTIVATION_CHANNEL = p.ACTIVATION_CHANNEL     WHERE p.CURRENT_FLAG = 1) pro   ON (pro.HEAD_ID = delta.PROMOTION_HEAD_ID AND delta.PARTNER_ID = pro.PARTNER_ID) OR      (pro.HEAD_ID = delta.PROMOTION_HEAD_ID AND delta.PARTNER_ID IS NULL) ",
				fromClause);
	}
}
