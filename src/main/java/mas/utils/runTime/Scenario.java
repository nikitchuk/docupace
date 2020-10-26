package mas.utils.runTime;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Scenarios.class)

public @interface Scenario {
    /**
     * Returns scenario in annotation.
     *
     * @return scenario
     */
    String value();

    String[] url() default {};

    String[] note() default {};

    Country[] countries() default {};
}
