/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package de.mathema.pride;

import java.lang.reflect.Array;

/**
 * Convenience class for simplified assembly of SQL expressions
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class SQLExpression
{
    public static interface Operator {
        public String EQUAL = "=";
        public String UNEQUAL = "<>";
        public String LESS = "<";
        public String GREATER = ">";
        public String LESSEQUAL = "<=";
        public String GREATEREQUAL = ">=";
        public String LIKE = "LIKE";
        public String BETWEEN = "BETWEEN";
        public String IN = "IN";
        public String NOTIN = "NOT IN";
    }
    
    public static interface Direction {
    	public String ASC = "ASC";
    	public String DESC = "DESC";
    }
    private String expression;
    private String orderBy;
    private SQLFormatter formatter;

    /** Create a new empty SQL expression
     * @param formatter A formatter object used to format SQL values.
     * This may e.g. be a {@link Database} object.
     */
    public SQLExpression(SQLFormatter formatter) { this(formatter, null); }

    /** Create a new SQL expression
     * @param formatter A formatter object úsed to format SQL values
     *   This may e.g. be a {@link Database} object.
     * @param expression An initial expression
     */
    public SQLExpression(SQLFormatter formatter, String expression) {
    	this(formatter, expression, null);
    }

    /** Create a new SQL expression
     * @param formatter A formatter object úsed to format SQL values
     *   This may e.g. be a {@link Database} object.
     * @param expression An initial expression
     * @param orderByExpression An initial order by expression
     */
    public SQLExpression(SQLFormatter formatter, String expression, String orderByExpression) {
        this.formatter = formatter;
        this.expression = (expression == null) ? null : expression.trim();
        this.orderBy = (orderByExpression == null) ? null : orderByExpression.trim();
    }

    /** Returns true if the expression, represented by this object is empty,
     * i.e. it is null or an empty string
     */
    protected boolean isEmpty() {
        return (expression == null || expression.length() == 0);
    }

    /** Format the passed value if there has a formatter been passed
     * in the constructor and if the value is not of type {@link SQLRaw}
     */
    protected String formatValue(Object value) {
        return (formatter == null || (value instanceof SQLRaw)) ?
            value.toString() : formatter.formatValue(value);
    }

    protected String formatOperator(String operator, Object value) {
        if (formatter != null)
            return formatter.formatOperator(operator, value);
        return AbstractResourceAccessor.standardOperator(operator, value);
    }

    protected String operation(String field, String operator, Object value) {
        String result = field + " " + formatOperator(operator, value) + " ";
        if (operator.equals(Operator.BETWEEN)) {
            return result + formatValue(Array.get(value, 0)) + " AND " + formatValue(Array.get(value, 1));
        }
        else if (operator.equals(Operator.IN) || operator.equals(Operator.NOTIN)) {
            result += " ( ";
            for (int i = 0; i < Array.getLength(value); i++) {
                result += formatValue(Array.get(value, i));
                if (i < Array.getLength(value)-1)
                    result += ", ";
            }
            return result + " )";
        }
        else
            return result + formatValue(value);
    }
    
    /** Returns an SQL assignment expression for the passed field and value */
    protected String assign(String field, Object value) {
        return operation(field, Operator.EQUAL, value);
    }

    /** Appends a sub-expression to this object's expression
     * @param operation The appendence operator to use, e.g. AND, OR, ...
     * @param subExpression The sub expression to append
     * @return a new SQLExpression representing the assembled expression
     */
    protected SQLExpression append(String operation, String subExpression) {
    	if (subExpression == null || subExpression.length() == 0)
    		return this;
    	else
        	return new SQLExpression(formatter, isEmpty() ? subExpression :
                                 	 expression + " " + operation + " " + subExpression, orderBy);
    }

    /** Returns a new SQLExpression representing the AND concatenation of this
     * object's expression with a field assignment
     * @param field The field to assign a value to
     * @param value The value to assign to the specified field. The value will
     *   be formatted by a call to {@link #formatValue}.
     * @return A new SQLExpression
     */
    public SQLExpression and(String field, Object value) {
        return append("AND", assign(field, value));
    }

    /** Returns a new SQLExpression representing the AND concatenation of this
     * object's expression and a field operation defined by the passed parameters.
     * @param field The field to operate on
     * @param operator The operator to apply. Supported values are listed in
     *   sub-interface {@link Operator}.
     * @param value The value to apply. In case of operator {@link Operator#BETWEEN},
     *   the value is supposed to be an array of two values. In case of operator
     *   {@link Operator#IN}, the value is supposed to be an array of arbitrary
     *   length holding the values to compare the denoted field to.
     * @return A new SQLExpression
     */
    public SQLExpression and(String field, String operator, Object value) {
        return append("AND", operation(field, operator, value));
    }

	public SQLExpression and(String field, boolean value) {
		return and(field, new Boolean(value));
	}

	public SQLExpression and(String field, String operator, boolean value) {
		return and(field, operator, new Boolean(value));
	}

	public SQLExpression and(String field, char value) {
		return and(field, new Character(value));
	}

	public SQLExpression and(String field, String operator, char value) {
		return and(field, operator, new Character(value));
	}

	public SQLExpression and(String field, short value) {
		return and(field, new Short(value));
	}

	public SQLExpression and(String field, String operator, short value) {
		return and(field, operator, new Short(value));
	}

    public SQLExpression and(String field, int value) {
        return and(field, new Integer(value));
    }

    public SQLExpression and(String field, String operator, int value) {
        return and(field, operator, new Integer(value));
    }

    public SQLExpression and(String field, long value) {
        return and(field, new Long(value));
    }

    public SQLExpression and(String field, String operator, long value) {
        return and(field, operator, new Long(value));
    }

	public SQLExpression and(String field, float value) {
		return and(field, new Float(value));
	}

	public SQLExpression and(String field, String operator, float value) {
		return and(field, operator, new Float(value));
	}

	public SQLExpression and(String field, double value) {
		return and(field, new Double(value));
	}

	public SQLExpression and(String field, String operator, double value) {
		return and(field, operator, new Double(value));
	}

    public SQLExpression and(SQLExpression rhsExpression) {
        return append("AND", rhsExpression.brace().toString());
    }

    public SQLExpression and(String rhsExpression) {
        return append("AND", rhsExpression);
    }

    /** Returns a new SQLExpression representing the OR concatenation of this
     * object's expression with a field assignment
     * @param field The fiels to assign a value to
     * @param value The value to assign to the specified field. The value will
     *   be formatted by a call to {@link #formatValue}.
     * @return A new SQLExpression
     */
    public SQLExpression or(String field, Object value) {
        return append("OR", assign(field, value));
    }

    /** Returns a new SQLExpression representing the OR concatenation of this
     * object's expression and a field operation defined by the passed parameters.
     * @param field The field to operate on
     * @param operator The operator to apply. Supported values are listed in
     *   sub-interface {@link Operator}.
     * @param value The value to apply. In case of operator {@link Operator#BETWEEN},
     *   the value is supposed to be an array of two values. In case of operator
     *   {@link Operator#IN}, the value is supposed to be an array of arbitrary
     *   length holding the values to compare the denoted field to.
     * @return A new SQLExpression
     */
    public SQLExpression or(String field, String operator, Object value) {
        return append("OR", operation(field, operator, value));
    }

	public SQLExpression or(String field, boolean value) {
		return or(field, new Boolean(value));
	}

	public SQLExpression or(String field, String operator, boolean value) {
		return or(field, operator, new Boolean(value));
	}

	public SQLExpression or(String field, char value) {
		return or(field, new Character(value));
	}

	public SQLExpression or(String field, String operator, char value) {
		return or(field, operator, new Character(value));
	}

	public SQLExpression or(String field, short value) {
		return or(field, new Short(value));
	}

	public SQLExpression or(String field, String operator, short value) {
		return or(field, operator, new Short(value));
	}

    public SQLExpression or(String field, int value) {
        return or(field, new Integer(value));
    }

    public SQLExpression or(String field, String operator, int value) {
        return or(field, operator, new Integer(value));
    }

    public SQLExpression or(String field, long value) {
        return or(field, new Long(value));
    }

    public SQLExpression or(String field, String operator, long value) {
        return or(field, operator, new Long(value));
    }

	public SQLExpression or(String field, float value) {
		return or(field, new Float(value));
	}

	public SQLExpression or(String field, String operator, float value) {
		return or(field, operator, new Float(value));
	}

	public SQLExpression or(String field, double value) {
		return or(field, new Double(value));
	}

	public SQLExpression or(String field, String operator, double value) {
		return or(field, operator, new Double(value));
	}

    public SQLExpression or(SQLExpression rhsExpression) {
        return append("OR", rhsExpression.brace().toString());
    }

    public SQLExpression or(String rhsExpression) {
        return append("OR", rhsExpression);
    }

    /** Puts this object's expression in braces */
    public SQLExpression brace() {
        return (isEmpty()) ? this : new SQLExpression(formatter, "( " + expression + " )", orderBy);
    }

	public String toString() {
		boolean noExpression = isEmpty(expression);
		boolean noOrderBy = isEmpty(orderBy);
		// TODO meist02: Dies ist nur ein mieser Workaround, weil das Wörtchen WHERE nicht Teil der SQLExpression ist und somit von aussen dem SQL-Befehl hinzugefügt wird. Ist die where clause leer und order by gefüllt, würde "where order by" Ins SQL geschrieben werden. Gar nicht gut!  
		String dummyWhereClause = noExpression && !noOrderBy ? "1 = 1 " : ""; 
		return (noExpression ? dummyWhereClause : expression) + (noOrderBy ? "" : " ORDER BY " + orderBy); 
	}
    
	public SQLExpression orderBy(String orderExpression) {
		return orderBy(orderExpression, null);
    }

   	public SQLExpression orderBy(String orderExpression, String direction) {
    	if(isEmpty(orderExpression)) { 
    		return this;
    	}
    	
    	if (!orderExpression.endsWith(" ")) {
    		orderExpression = orderExpression + " ";
    	} 
    	
    	orderExpression = orderExpression + (isEmpty(direction) ? "" : direction.trim() + " ");
    	
        return new SQLExpression(formatter, expression, isEmpty(orderBy) ? orderExpression :
                                 	 orderBy + ", " + orderExpression);
    }

    protected boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0 );
    }

    public final static String REVISION_ID = "$Header: /framework/pride/src/de/mathema/pride/SQLExpression.java 4     3.02.06 13:41 Less02 $";
}

/* $Log: /framework/pride/src/de/mathema/pride/SQLExpression.java $
 * 
 * 4     3.02.06 13:41 Less02
 * Upgrade to PriDE 2.3.2
 * 
 *    Rev 1.1   11 Sep 2002 11:25:06   math19
 * Some convenience functions added.
 * 
 *    Rev 1.0   06 Sep 2002 14:52:52   math19
 * Initial revision.
 * 
 *    Rev 1.1   Jul 25 2002 16:49:16   math19
 * Function formatOperator() added.
 */
