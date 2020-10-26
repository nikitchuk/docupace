package mas.runner;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class IgnoreParentClassFilter extends Filter {
    @Override
    public boolean shouldRun(Description d) {
        Class<?> clazz = d.getTestClass();
        String methodName = d.getMethodName();
        try {
            // ignore tests from parent class
            return clazz.getDeclaredMethod(methodName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String describe() {
        return "Ignore tests from parent class filter.";
    }
}