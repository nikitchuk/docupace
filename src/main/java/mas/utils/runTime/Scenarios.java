package mas.utils.runTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)

/*
  Used to mark all scenarios which are implemented by a test method.
 */
public @interface Scenarios {
    /**
     * Returns all scenarios in annotation.
     *
     * @return scenarios
     */
    Scenario[] value() default {};
}


