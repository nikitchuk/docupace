package mas.utils;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import sun.awt.EventListenerAggregate;

import java.util.EventListener;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;


public class Collector implements TestRule {

    @Rule
    public ErrorCollector collector =  new ErrorCollector();
    private EventListenerAggregate errors;


    @Override
    public Statement apply(Statement base, Description description) {
        return null;
    }


    @Test
    public void example() {
        collector.addError(new Throwable("first thing went wrong"));
        collector.addError(new Throwable("second thing went wrong"));

        // all lines will run, and then a combined failure logged at the end.
    }

//    public void check(){
//        collector.checkThat();
//    }



    public <T> boolean checkThat(final String reason, final T value, final Matcher<T> matcher) {
        return null != checkSucceeds(() -> {
            assertThat(reason, value, matcher);
            return value;
        });
    }

    /**
     * Adds to the table the exception, if any, thrown from {@code callable}.
     * Execution continues, but the test will fail at the end if
     * {@code callable} threw an exception.
     *
     * @param callable  method should be executed
     * @return executed method
     */
    public Object checkSucceeds(Callable<Object> callable) {
        try {
            return callable.call();
        } catch (Throwable e) {
            prepareError(e);
            return null;
        }
    }

    private void prepareError(Throwable e) {
        String newMessage = "Error";

        AssertionError modifiedError = new AssertionError(newMessage.trim());
        modifiedError.setStackTrace(e.getStackTrace());
        errors.add((EventListener) modifiedError);
    }
}
