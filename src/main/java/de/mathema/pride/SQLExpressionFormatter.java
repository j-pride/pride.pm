package de.mathema.pride;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
/**
 * This class helps to assemble SQL expressions in a way that is a compromise
 * between SQL redability on the one hand and the usage of constants an the other hand
 * to follow the DRY principle. For example database column and table names are often
 * available as (generated) constants and should of course be used even when the SQL
 * becomes rough. The function {@link #format(String, Object...)} in principle works like
 * String.format(), but additionally allows to address the Parameters <i>by name</i> rather
 * than by index. Names are indicated by a leading § character, i.e. you may as well combine
 * % expressions vom String.format with § expressions from SQLExpressionFormatter. The names
 * are read from the format string and are replaced by their indices according to there
 * accurence. E.g. the string
 * <pre>
 * "where $PROMOTION_TABLE.$ID = $DCAMPAIGN_TABLE.$ID"
 * </pre>
 * will be translated to
 * <pre>
 * "where %s.%s = %s.%2$s"
 * </pre>
 * 
 * The translated format string shows the intention of the formatter: the string with
 * variable names is pretty well-readable SQL which can easily be verified for syntactical
 * and semantical correctness. The translated string definitely not!
 * 
 * @author less02
 */
public class SQLExpressionFormatter {
    public static String VARIABLE_HEAD = "§";
    public static String VARIABLE_REFERENCE_REGEXP = VARIABLE_HEAD + "[A-Za-z_]+";

    public static String format(String formatString, Object... args) {
        List<VariableReference> variables = extractVariables(formatString);
        formatString = replaceVariables(formatString, variables);
        return String.format(formatString, args);
    }

    private static String replaceVariables(String formatString, List<VariableReference> variables) {
        //Liste nach Länge der Variablennamen absteigend sortieren, damit z.B. §PROMOTIONS_PARTNER
        //wegen einer Variable §PROMOTION nicht durch dmd_promotionS_PARTNER ersetzt wird
        Collections.sort(variables);
        for (VariableReference variable: variables) {
            formatString = formatString.replaceFirst(variable.getVariableName(), "%s");
            formatString = formatString.replaceAll(variable.getVariableName(), "%" + variable.getIndex() + "\\$s");
        }
        return formatString;
    }

    private static List<VariableReference> extractVariables(String formatString) {
        List<VariableReference> variables = new ArrayList<VariableReference>();
        Pattern pattern = Pattern.compile(VARIABLE_REFERENCE_REGEXP);
        Matcher matcher = pattern.matcher(formatString);
        int index = 1;
        while(matcher.find()) {
            VariableReference ref = new VariableReference(index, matcher.group());
            if (!variables.contains(ref)) {
                variables.add(ref);
                index++;
            }
        }
        return variables;
    }

    private static class VariableReference implements Comparable<VariableReference> {
        final int index;
        final String variableName;
        private VariableReference(int index, String variableName) {
            this.index = index;
            this.variableName = variableName;
        }
        public int getIndex() { return index; }
        public String getVariableName() { return variableName; }
        public int compareTo(VariableReference rhs) {
            return rhs.variableName.length() - variableName.length();
        }
        @Override
        public int hashCode() {
            return variableName.hashCode();
        }
        @Override
        public boolean equals(Object rhs) {
            return variableName.equals(((VariableReference)rhs).variableName);
        }
        
    }
    
}
