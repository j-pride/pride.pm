package pm.pride.guardrail;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pm.pride.basic.AbstractPrideTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "pm.pride")
public class EnsureCorrectTestDefinitionGuardrailTest {

    @ArchTest
    public static final ArchRule ensure_setUp_is_annotated_with_BeforeEach =
            methods().that()
                     .areDeclaredInClassesThat()
                     .areAssignableTo(AbstractPrideTest.class)
                     .and()
                     .haveName("setUp")
                     .should()
                     .beAnnotatedWith(BeforeEach.class);

    @ArchTest
    public static final ArchRule ensure_tearDown_is_annotated_with_AfterEach =
            methods().that()
                     .areDeclaredInClassesThat()
                     .areAssignableTo(AbstractPrideTest.class)
                     .and()
                     .haveName("tearDown")
                     .should()
                     .beAnnotatedWith(AfterEach.class);
}
