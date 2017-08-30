package de.mathema.pride;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse ist ein kleiner Helfer für einen Kompromiss zwischen Anschaulichkeit
 * und Verwendung von Konstanten beim Aufbau komplizierter SQL-Ausdrücke. Die
 * Funktion {@link #format(String, Object...)} funktioniert im Prinzip wie String.format(),
 * allerdings kann man die Parameter auch zusätzlich über einen <i>Namen</i> statt nur über
 * ihren Index ansprechen. Die Namen werden über § eingeleitet, d.h. man kann auch
 * %-Ausdrücke von String.format und §-Ausdrücke von SQLExpressionFormatter
 * kombiniert verwenden. Die Namen werden aus dem Format-String gelesen und in der
 * Reihenfolge ihrer Ausftretens durch Indizes ersetzt. Z.B. wird
 * "where $PROMOTION_TABELLE.$ID = $KAMPAGNEN_TABELLE.$ID" übersetzt zu
 * "where %s.%s = %s.%2$s".
 * <p>
 * An dem übersetzten Formatstring sieht man, was der Vorteil ist: der String mit Variablennamen
 * ist noch ziemlich gut als SQL lesbar und auf Sinnhaftigkeit prüfbar. Wenn sich immer nur
 * ein %s an das nächste reiht, sieht man das nicht mehr.
 * <p>
 * Eine beispiel füe die Anwendung sieht man in {@link KampagneRepositoryImpl#kampagneKannFreigegebenWerden}
 * 
 * @author less02
 */
public class SQLExpressionFormatter {
    public static char VARIABLE_HEAD = '§';
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
