package basic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface SkipForDBType {
    String[] value();

    String message() default "These test should be skipped for this kind of database";
}
