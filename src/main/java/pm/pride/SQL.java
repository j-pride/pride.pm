/*******************************************************************************
 * Copyright (c) 2001-2018 The PriDE team
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.pm/LGPL.html
 *******************************************************************************/
package pm.pride;

import java.util.Date;

public class SQL {

	/**
	 * This is just a helper class, preventing a string value from getting formatted when
	 * passed to any expression builder function of {@link WhereCondition}. E.g. use this
	 * type to express an equality check between two database fields like this:<p>
	 * <tt>exp.and("field1", new SQL.Pre("field2"))</tt>
	 * or
	 * <tt>exp.and("field1", SQL.pre("field2"))</tt>
	 */
	public static class Pre {
	    protected String fieldname;
	    public Pre(String fieldname) { this.fieldname = fieldname; }
	    public String toString() { return fieldname; }
	}

	public static Pre pre(String fieldname) {
		return new Pre(fieldname);
	}

	public static Date systime() {
		return DatabaseFactory.getDatabase().getSystime();
	}
	
	/** Assembly of complex SQL expressions. Details see {@link SQLExpressionBuilder#format(String, Object...)} */
	public static String build(String formatString, Object... args) {
		return new SQLExpressionBuilder().format(formatString, args);
	}
	
	/** Assembly of complex SQL expressions. Details see {@link SQLExpressionBuilder#format(String, Object...)} */
	public static String buildx(String formatString, Object... args) {
		return new SQLExpressionBuilder(SQLExpressionBuilder.Validation.ExceptionCaseInsensitive).format(formatString, args);
	}
	
	public interface Formatter {
	    String formatValue(Object rawValue);
	    String formatOperator(String operator, Object rawValue);
	    Object formatPreparedValue(Object rawValue);
	    boolean bindvarsByDefault();
	}
	
}
