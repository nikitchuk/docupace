package mas.utils.cleanUp;

import mas.utils.runTime.EndToEndProperties;
import mas.utils.runTime.EventAppender;
import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;


public class ScreenshotTakingErrorCollector implements TestRule {

    protected List<Throwable> errors = new ArrayList<>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private CleanUpUtil cleaner;

    public ScreenshotTakingErrorCollector(CleanUpUtil cleaner) {
        this.cleaner = cleaner;
    }

    public void setCleaner(CleanUpUtil cleaner) {
        this.cleaner = cleaner;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    /**
     * Adds a failure with the given {@code reason}
     * to the table if {@code matcher} does not match {@code value}.
     * Execution continues, but the test will fail at the end if the match fails.
     *
     * @param value -  the computed value being compared
     * @param matcher - an expression, built of Matchers, specifying allowed values
     * @param reason -  additional information about the error
     * @param <T>  the static type accepted by the matcher (this can flag obvious compile-time problems such as assertThat(1, is("a"))
     * @return value or null
     * */
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
        String newMessage = prepareEvidence(e);

        AssertionError modifiedError = new AssertionError(newMessage.trim());
        modifiedError.setStackTrace(e.getStackTrace());
        errors.add(modifiedError);
        EventAppender.logError(modifiedError, null, false);
    }

    private String prepareEvidence(Throwable e) {
        if (cleaner == null) {
            cleaner = new CleanUpUtil(null);
        }
        Evidence evidence = cleaner.handleError(getMethodName(e));

        StringBuilder newMessage = new StringBuilder();
        for (String line : e.getMessage().split("\\n")) {
            newMessage.append("\n");
            if (line.startsWith("Expected: ")) {
                if (evidence.hasScreenshot()) {
                    newMessage.append("  Screenshot: ").append(evidence.formatScreenshotUrl().trim()).append("\n");
                }
                if (evidence.hasText()) {
                    newMessage.append("        Text: ").append(evidence.formatTextUrl().trim()).append("\n");
                }
                if (evidence.hasHtml()) {
                    newMessage.append("        Page: ").append(evidence.formatHtmlUrl().trim()).append("\n");
                }
                newMessage.append("    ").append(line);
            } else if (line.trim().startsWith("but: ")) {
                newMessage.append("    ").append(line);
            } else {
                newMessage.append(line);
            }
        }
        return newMessage.toString();
    }

    protected String getMethodName(Throwable error) {
        StackTraceElement[] stackTrace = error.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.contains("ScreenshotTakingErrorCollector") && i > 2) {
                return error.getStackTrace()[i].getMethodName();
            }
        }
        return "unknown_method_name";
    }

    /**
     * Adds a Throwable to the table.  Execution continues, but the test will fail at the end.
     *
     * @param error - the error which was catch
     */
    public void addError(Throwable error) {
        if (EndToEndProperties.getInstance().TAKE_ACTIONS_WHEN_ADDING_TO_COLLECTOR) {
            cleaner.handleError(getMethodName(error));
        }
        StackTraceElement[] stack = Arrays.copyOf(error.getStackTrace(), error.getStackTrace().length + 1);
        stack[stack.length - 1] = new StackTraceElement(this.getClass().getCanonicalName(), "addError", null, 1);
        error.setStackTrace(stack);
        errors.add(error);
        EventAppender.logError(error, null, false);
    }

    public List<Throwable> getErrors() {
        return errors;
    }

}
