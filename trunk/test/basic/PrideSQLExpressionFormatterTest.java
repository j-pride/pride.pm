package basic;

import org.junit.Test;

import de.mathema.pride.SQLExpressionFormatter;

public class PrideSQLExpressionFormatterTest extends AbstractPrideTest {

	@Test
	public void testSimpleExpression() {
        String result = SQLExpressionFormatter.format("�ONE, �TWO, �THREE, �ONE", "one", 2, 3);
		assertEquals("one, 2, 3, one", result);
	}
	
	@Test
	public void testMixedExpression() {
        String result = SQLExpressionFormatter.format("�ONE, �TWO, �THREE, %s, �ONE", "one", 2, 3, "string");
		assertEquals("one, 2, 3, string, one", result);
	}
	
}
