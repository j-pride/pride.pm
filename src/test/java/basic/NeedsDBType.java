package basic;

import java.lang.annotation.*;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface NeedsDBType {
    String[] value();

    String message() default "These tests cannot be run with the given database type";
}
