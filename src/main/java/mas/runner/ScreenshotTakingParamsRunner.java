package mas.runner;

import junitparams.internal.ParameterisedTestClassRunner;
import junitparams.internal.TestMethod;
import mas.parent.GenericTest;
import mas.utils.cleanUp.FinalCleanup;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ScreenshotTakingParamsRunner extends BlockJUnit4ClassRunner {
    private static String SCREENSHOT_FILE_FORMAT = "screenshot-%s.png";
    private static String SCREENSHOT_DIRECTORY = System.getProperty("screenshotDir", "target/");
    private static String SCREENSHOT_DIRECTORY_URL = System.getProperty("screenshotURL", getRootFullPath());
    private ParameterisedTestClassRunner parametrizedRunner;
    private Description description;
    private List<Method> finalCleanupMethods = new ArrayList<>();
    private GenericTest testInstance;

    public ScreenshotTakingParamsRunner(Class<? extends GenericTest> klass) throws InitializationError {
        super(klass);
        parametrizedRunner = new ParameterisedTestClassRunner(getTestClass());
        findFinalCleanupMethods(klass);
    }

    /**
     * Shortcut for returning an array of objects. All parameters passed to this
     * method are returned in an <code>Object[]</code> array.
     *
     * @param params Values to be returned in an <code>Object[]</code> array.
     * @return Values passed to this method.
     */
    public static Object[] $(Object... params) {
        return params;
    }

    private static String getRootFullPath() {

        return new File(SCREENSHOT_DIRECTORY)
                .getAbsolutePath() + File.separator;
    }

    @Override
    public Description getDescription() {
        if (description == null) {
            description = Description.createSuiteDescription(getName(), getTestClass().getAnnotations());
            List<FrameworkMethod> resultMethods = parametrizedRunner.returnListOfMethods();

            for (FrameworkMethod method : resultMethods)
                description.addChild(describeMethod(method));
        }
        return description;
    }

    protected Description describeMethod(FrameworkMethod method) {
        Description child = parametrizedRunner.describeParameterisedMethod(method);

        if (child == null)
            child = describeChild(method);

        return child;
    }

    /**
     * Find all methods with annotation FinalCleanup
     *
     * @param klass Start searching here and continue with superclasses
     */
    public void findFinalCleanupMethods(final Class<?> klass) {
        if (klass == null || klass.getSuperclass() == null)
            return;

        for (Method method : klass.getSuperclass().getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.getAnnotation(FinalCleanup.class) != null) {
                finalCleanupMethods.add(method);
            }
        }

        findFinalCleanupMethods(klass.getSuperclass());
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        notifier.addFirstListener(new RunListener() {
            @Override
            public void testFinished(Description description) throws Exception {
                invokeFinalCleanupMethods();
            }

            @Override
            public void testFailure(Failure failure) throws Exception {
                captureScreenshot(failure.getDescription().getMethodName());
                failure.getException().printStackTrace();
            }
        });

        if (handleIgnored(method, notifier))
            return;

        TestMethod testMethod = parametrizedRunner.testMethodFor(method);
        if (parametrizedRunner.shouldRun(testMethod))
            parametrizedRunner.runParameterisedTest(testMethod, methodBlock(method), notifier);
        else
            super.runChild(method, notifier);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return parametrizedRunner.computeFrameworkMethods();
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        errors.forEach(Throwable::printStackTrace);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        if (GenericTest.class.isAssignableFrom(test.getClass())) {
            testInstance = (GenericTest) test;
        } else {
            throw new IllegalArgumentException(getClass().getName() + " could be used only with " + GenericTest.class);
        }

        Statement methodInvoker = parametrizedRunner.parameterisedMethodInvoker(method, test);

        if (methodInvoker == null)
            methodInvoker = super.methodInvoker(method, test);

        return methodInvoker;
    }

    /**
     * Invoke all clean up  methods
     */
    private void invokeFinalCleanupMethods() {
        for (Method method : finalCleanupMethods) {
            try {
                method.invoke(testInstance);
            } catch (Exception ex) {
                throw new RuntimeException("Can't invoke final cleanup method: " + method.getName());
            }
        }
    }

    /**
     * Capture screenshot
     *
     * @param name label to be included in the file name
     */
    private void captureScreenshot(final String name) {
        try {
            File screenshot = ((TakesScreenshot) testInstance.getWebDriver()).getScreenshotAs(OutputType.FILE);
            Date now = new Date();
            final File destFile = new File(SCREENSHOT_DIRECTORY + getFileName(name));
            prepareScreenshotUrl(now, name);
            // the screenshots can be moved to a folder for sorting
            FileUtils.copyFile(screenshot, destFile);
        } catch (Exception e) {
            // no need to crash the tests if the screenshot fails - just log it
            e.printStackTrace();
        }
    }

    private boolean handleIgnored(FrameworkMethod method, RunNotifier notifier) {
        TestMethod testMethod = parametrizedRunner.testMethodFor(method);

        if (testMethod.isIgnored())
            notifier.fireTestIgnored(describeMethod(method));

        return testMethod.isIgnored();
    }

    private String getFileName(String fileName) {
        return String.format(SCREENSHOT_FILE_FORMAT, fileName);
    }

    private void prepareScreenshotUrl(final Date now, final String fileName) {
        System.err.print(now);
        System.err.print(" ");
        System.err.print(SCREENSHOT_DIRECTORY_URL);
        System.err.print(getFileName(fileName));
        System.err.println(" ");
    }
}
