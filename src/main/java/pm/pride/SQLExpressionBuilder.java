package pm.pride;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class helps to assemble SQL expressions in a way that is a compromise
 * between SQL readability on the one hand and the usage of constants an the other hand
 * to follow the DRY principle. For example database column and table names are often
 * available as (generated) constants and should of course be used even when the SQL
 * becomes rough. The function {@link #format(String, Object...)} in principle works like
 * String.format(), but additionally allows to address the Parameters <i>by name</i> rather
 * than by index. Names are indicated by a leading @ character, i.e. you may as well combine
 * % expressions from String.format with @ expressions from SQLExpressionFormatter. The names
 * are read from the format string and are replaced by their indices according to there
 * occurence. E.g. the string
 * <pre>
 * "where @PROMOTION_TABLE.@ID = @CAMPAIGN_TABLE.@ID"
 * </pre>
 * will be translated to
 * <pre>
 * "where %s.%s = %s.%2$s"
 * </pre>
 * 
 * The translated format string shows the intention of the builder: the string with
 * variable names is pretty well-readable SQL which can easily be verified for syntactical
 * and semantical correctness. The translated string definitely not!<br>
 * If the names pile up in complicated SQL, they may be accompanied by a position specifier
 * as known from String.format(). The specifier mus be placed between the leading @ character
 * and the variable name. E.g. the following expression is equivalent to the one above:
 * <pre>
 * "where @1$PROMOTION_TABLE.@2$ID = @3$CAMPAIGN_TABLE.@2$ID"
 * </pre>
 * 
 * Same names must be accompanied by the same index all over the expression, otherwise the
 * builder will throw an {@link IllegalArgumentException}. Alternatively, the index may only
 * be specified in one occurrence of the variable and omitted in all others, e.g.
 * <pre>
 * "where @PROMOTION_TABLE.@2$ID = @CAMPAIGN_TABLE.@ID"
 * </pre>
 * 
 * <p>
 * The class is supposed to be used indirectly through {@link SQL#build(String, Object...)}
 * 
 * @author less02
 */
public class SQLExpressionBuilder {
    public static String VARIABLE_HEAD = "@";
    public static String VARIABLE_REFERENCE_REGEXP = VARIABLE_HEAD + "((\\d+)\\$)?([A-Za-z_]+)";

    static String format(String formatString, Object... args) {
        List<VariableReference> variables = extractVariables(formatString);
        formatString = replaceVariables(formatString, variables);
        return String.format(formatString, args);
    }

    private static String replaceVariables(String formatString, List<VariableReference> variables) {
        // Sort list by variable name length in descending order, so that e.g.
    	// @PROMOTIONS_PARTNER will not be replaced by dmd_promotionS_PARTNER because there is a variable
    	// @PROMOTION present too
        Collections.sort(variables);
        for (VariableReference variable: variables) {
        	if (variable.indexFromName) {
        		formatString = formatString.replace(variable.fullExpression, "%" + variable.index + "$s");
        		formatString = formatString.replace(VARIABLE_HEAD + variable.variableName, "%" + variable.index + "$s");
        	}
        	else {
                formatString = formatString.replaceFirst(variable.fullExpression, "%s");
                formatString = formatString.replaceAll(variable.fullExpression, "%" + variable.index + "\\$s");
        	}
        }
        return formatString;
    }

    private static List<VariableReference> extractVariables(String formatString) {
        Map<String, VariableReference> variables = new HashMap<>();
        Pattern pattern = Pattern.compile(VARIABLE_REFERENCE_REGEXP);
        Matcher matcher = pattern.matcher(formatString);
        int index = 1;
        while(matcher.find()) {
            VariableReference ref = new VariableReference(index, matcher.group(0), matcher.group(2), matcher.group(3));
            VariableReference earlierRef = variables.get(ref.variableName);
            if (earlierRef == null) {
                variables.put(ref.variableName, ref);
                if (!ref.indexFromName)
                	index++;
            }
            else {
            	if (earlierRef.replaceableBy(ref))
            		variables.replace(earlierRef.variableName, ref);
            }
        }
        return new ArrayList<VariableReference>(variables.values());
    }

    private static class VariableReference implements Comparable<VariableReference> {
        final int index;
        final String variableName;
        final boolean indexFromName;
        final String fullExpression;
        private VariableReference(int indexFromPosition, String fullExpression, String indexFromName, String variableName) {
        	this.fullExpression = fullExpression;
        	this.indexFromName = (indexFromName != null);
        	this.index = (this.indexFromName) ? Integer.parseInt(indexFromName) : indexFromPosition;
            this.variableName = variableName;
        }
        public boolean replaceableBy(VariableReference ref) {
        	if (indexFromName && ref.indexFromName && index != ref.index) {
        		throw new IllegalArgumentException("Variable name " + variableName + " occurs with different indices: " +
        				fullExpression + " / " + ref.fullExpression);
        		
        	}
        	return !indexFromName && ref.indexFromName;
		}
        public int compareTo(VariableReference rhs) {
            return rhs.variableName.length() - variableName.length();
        }
        
        @Override
        public int hashCode() { return variableName.hashCode(); }
        
        @Override
        public boolean equals(Object rhs) {
            return variableName.equals(((VariableReference)rhs).variableName);
        }

        public String toString() { return variableName + "(" + index + ")"; }
    }
    
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile(VARIABLE_REFERENCE_REGEXP);
        Matcher matcher = pattern.matcher("@ONE @1$ONE @01$ONE @f$ONE");
		while(matcher.find()) {
			System.out.println(matcher.group(0) + " / " + matcher.group(1) +
					" / " + matcher.group(2) + " / " + matcher.group(3));
		}
	}
}
