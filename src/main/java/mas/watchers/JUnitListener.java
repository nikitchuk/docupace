package mas.watchers;

import com.codepine.api.testrail.model.Run;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import mas.parent.GenericTest;
import mas.runner.ScreenshotTakingJUnit4ClassRunner;
import mas.utils.cleanUp.Evidence;
import mas.utils.runTime.EndToEndProperties;
import mas.utils.runTime.EventAppender;
import mas.utils.runTime.Scenario;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default listener class for custom runner
 */
public class JUnitListener extends RunListener {

    private static Run run;

    private final ScreenshotTakingJUnit4ClassRunner runner;
    /**
     * starting times of individual tests
     */
    private Map<String, Long> startTimes = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private GenericTest testInstance;

    public JUnitListener(ScreenshotTakingJUnit4ClassRunner runner) {
        this.runner = runner;
    }

    public void setTestInstance(GenericTest testInstance) {
        this.testInstance = testInstance;
    }

    @Override
    public void testRunFinished(Result result) {
        EventAppender.closeLogFile();
    }

    @Override
    public void testStarted(Description description) {
        if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS && isThisInstance(description)) {
            startTimes.put(description.getDisplayName(), System.currentTimeMillis());
        }
    }

    @Override
    public void testFinished(Description description) {

        if (isThisInstance(description)) {
            try {
                runner.invokeFinalCleanupMethods();
            } catch (Exception e) {
                testFailure(new Failure(description, e));
            } finally {
                EventAppender.endBlock();
            }
            if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS) {
                String id = description.getDisplayName();
                if (startTimes.containsKey(id)) {
                    System.out.println("\nTEST RESULT: *SUCCESS* " + description.getMethodName() + " in " + getDuration(id) + "\n");
                    startTimes.remove(id);
                }
            }
        }
    }

    @Override
    public void testFailure(Failure failure) {

        if (isThisInstance(failure)) {
            String name = failure.getDescription().getMethodName();
            String id = failure.getDescription().getDisplayName();

            if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS) {
                if (startTimes.containsKey(id)) {
                    System.out.println("\nTEST RESULT: *FAIL*   " + name + " in " + getDuration(id));
                    startTimes.remove(id);
                }
            }

            if (notYetHandled(failure)) {
                Evidence evidence = null;
                try {
                    evidence = testInstance.getCleaner().handleError(name);
                } finally {
                    EventAppender.logError(failure.getException(), evidence, true);
                }
            }

            if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS) {
                System.out.println(processErrorMessage(failure));
            }
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        if (isThisInstance(failure)) {
            EventAppender.logSkip(failure.getException());

            if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS) {
                String name = failure.getDescription().getMethodName();
                String id = failure.getDescription().getDisplayName();

                StringBuilder message = new StringBuilder("\nTEST RESULT: *SKIP*   ");
                message.append(name)
                        .append(" in ")
                        .append(getDuration(id))
                        .append("\n");

                if (failure.getException().getMessage() != null) {
                    String[] lines = failure.getException().getMessage().split("\n");
                    message.append("  CAUSE : ").append(lines[0]).append("\n");
                    if (lines.length > 1) {
                        for (int i = 1; i < lines.length; i++) {
                            message.append("        : ")
                                    .append(lines[i])
                                    .append("\n");
                        }
                    }
                }
                System.out.println(message);
                startTimes.remove(id);
            }
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        System.out.println("________________________________________________________________________________\n" +
                "TEST IGNORED : " + description.getMethodName() + "\n");

        List<Scenario> scenarios = TestStartupWatcher.filterScenarios(description.getAnnotations());
        List<String> names = TestStartupWatcher.getNames(scenarios);
        EventAppender.startTest(description.getMethodName(), names);

        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(getClass()));
        CtClass cc = classPool.get(description.getClassName());
        CtMethod method = cc.getDeclaredMethod(description.getMethodName());
        int lineNumber = method.getMethodInfo().getLineNumber(0);

        Throwable throwable = new Throwable("Test ignored");
        StackTraceElement[] stackTrace = {new StackTraceElement(description.getClassName(), description.getMethodName(), "", lineNumber)};
        throwable.setStackTrace(stackTrace);
        EventAppender.logSkip(throwable);
        EventAppender.endBlock();
    }

    /**
     * @param displayName unique test identification
     * @return formatted time as HH:mm::ss
     */
    protected String getDuration(String displayName) {
        long millis = System.currentTimeMillis() - startTimes.get(displayName);
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private boolean isThisInstance(Failure failure) {
        Description description = failure.getDescription();
        return isThisInstance(description);
    }

    private boolean notYetHandled(Failure failure) {
        StackTraceElement[] stackTrace = failure.getException().getStackTrace();
        boolean needScreenshot = true;
        for (StackTraceElement st : stackTrace) {
            if (st.getClassName().contains(".ScreenshotTakingErrorCollector")
                    && !st.getMethodName().contains("evaluate")) {
                needScreenshot = false;
                break;
            }
        }
        return needScreenshot;
    }

    private String processErrorMessage(Failure failure) {
        StringBuilder newMessage = new StringBuilder();
        if (failure.getException().getMessage() != null) {
            String[] split = failure.getException().getMessage().split("\n");
            newMessage.append("  ERROR : ").append(split[0]).append("\n");
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    if (split[i].matches("\\W+Screenshot.+png")) {
                        break;
                    }
                    newMessage.append("        : ").append(split[i]).append("\n");
                }
            }
        }
        return newMessage.toString();

    }

    private boolean isThisInstance(Description description) {
        Class finishedTestClass = description.getTestClass();
        if (testInstance == null || finishedTestClass == null) { // REMOVE THIS WORKAROUND AFTER FIXING ISSUE WITH SKIPPED TESTS
            logger.warn("Cannot verify test instance identity. This instance: {}, Description: {}", testInstance, description);
            return false;
        }
        Class thisTestClass = testInstance.getClass();
        return finishedTestClass.equals(thisTestClass);
    }

}