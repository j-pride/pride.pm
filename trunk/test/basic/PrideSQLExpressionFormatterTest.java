package basic;

import java.util.Date;

import org.junit.Test;

import de.mathema.pride.SQLExpressionFormatter;

public class PrideSQLExpressionFormatterTest extends AbstractPrideTest {

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

	@Test
	public void testSimpleExpression() {
        String result = SQLExpressionFormatter.format("§ONE, §TWO, §THREE, §ONE", "one", 2, 3);
		assertEquals("one, 2, 3, one", result);
	}
	
	@Test
	public void testMixedExpression() {
        String result = SQLExpressionFormatter.format("§ONE, §TWO, §THREE, %s, §ONE", "one", 2, 3, "string");
		assertEquals("one, 2, 3, string, one", result);
	}

	@Test
	public void testRealisticExample() {
		String fromClause = SQLExpressionFormatter.format(
				"from §DELTA_TABLE delta " +
	            "  JOIN (SELECT /*+ NO_USE_NL (p) NO_USE_NL (pp))*/ " +
	            "    p.§HEAD_ID, pp.§PARTNER_ID, pp.§HEAD_ID, pp.§CAMPAIGN_ID, pp.§LAST_TRX_IMPORT_RELEVANCE " +
	            "    FROM §DMD_PRO_PROMOTION p JOIN §DMD_PRO_PROMOTION pp ON " +
	            "      pp.§CURRENT_FLAG = 1 AND pp.§PUBLIC_PROMOTION_ID = p.§PUBLIC_PROMOTION_ID AND pp.§ACTIVATION_CHANNEL = p.§ACTIVATION_CHANNEL " +
	            "    WHERE p.§CURRENT_FLAG = 1) pro " +
	            "  ON (pro.§HEAD_ID = delta.§PROMOTION_HEAD_ID AND delta.§PARTNER_ID = pro.§PARTNER_ID) OR " +
	            "     (pro.§HEAD_ID = delta.§PROMOTION_HEAD_ID AND delta.§PARTNER_ID IS NULL) ",
	            TABLE_DELTA,
	            COLUMN_HEADID, COLUMN_PARTNERID, COLUMN_CAMPAIGN_ID, COLUMN_LAST_TRX_IMPORT_RELEVANCE, 
	            TABLE_PROMOTION, 
	            COLUMN_CURRENTFLAG, COLUMN_PUBLICPROMOTIONID, COLUMN_ACTIVATIONCHANNEL,
	            COLUMN_PROMOTION_HEAD_ID);
		assertEquals("from DELTAS delta   JOIN (SELECT /*+ NO_USE_NL (p) NO_USE_NL (pp))*/     p.HEAD_ID, pp.PARTNER_ID, pp.HEAD_ID, pp.CAMPAIGN_ID, pp.LAST_TRX_IMPORT_RELEVANCE     FROM PROMOTION p JOIN PROMOTION pp ON       pp.CURRENT_FLAG = 1 AND pp.PUBLIC_PROMOTION_ID = p.PUBLIC_PROMOTION_ID AND pp.ACTIVATION_CHANNEL = p.ACTIVATION_CHANNEL     WHERE p.CURRENT_FLAG = 1) pro   ON (pro.HEAD_ID = delta.PROMOTION_HEAD_ID AND delta.PARTNER_ID = pro.PARTNER_ID) OR      (pro.HEAD_ID = delta.PROMOTION_HEAD_ID AND delta.PARTNER_ID IS NULL) ",
				fromClause);
	}
}
