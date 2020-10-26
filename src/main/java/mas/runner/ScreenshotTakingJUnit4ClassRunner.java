package mas.runner;

import mas.parent.GenericTest;
import mas.utils.cleanUp.FinalCleanup;
import mas.utils.runTime.EndToEndProperties;
import mas.watchers.CollectorGuard;
import mas.watchers.JUnitListener;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotTakingJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private static final IgnoreParentClassFilter FILTER = new IgnoreParentClassFilter();
    private static CollectorGuard collectorSetupListener;
    private static JUnitListener jUnitListener;
    /**
     * Default test instance
     */
    private GenericTest testInstance;
    /**
     * Collection for method clean up
     */
    private List<Method> finalCleanupMethods = new ArrayList<>();

    public ScreenshotTakingJUnit4ClassRunner(Class<? extends GenericTest> klass) throws InitializationError, NoTestsRemainException {
        super(klass);
        if (EndToEndProperties.getInstance().IGNORE_PARENT_TESTS) {
            filter(FILTER);
        }
        findFinalCleanupMethods(klass);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        if (GenericTest.class.isAssignableFrom(test.getClass())) {
            testInstance = (GenericTest) test;
            collectorSetupListener.setTestInstance(testInstance);
            jUnitListener.setTestInstance(testInstance);
        } else {
            throw new IllegalArgumentException(getClass().getName() + " could be used only with " + GenericTest.class);
        }
        return super.methodInvoker(method, test);
    }

    @Override
    public void run(RunNotifier notifier) {
        if (collectorSetupListener != null) {
            notifier.removeListener(collectorSetupListener);
        }
        collectorSetupListener = new CollectorGuard();
        notifier.addFirstListener(collectorSetupListener);

        if (jUnitListener != null) {
            notifier.removeListener(jUnitListener);
        }
        jUnitListener = new JUnitListener(this);
        notifier.addFirstListener(jUnitListener);

        super.run(notifier);
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

    /**
     * Invoke all found clean up  methods
     * @throws InvocationTargetException see {@link Method#invoke(Object, Object...)}
     * @throws IllegalAccessException see {@link Method#invoke(Object, Object...)}
     */
    public void invokeFinalCleanupMethods() throws InvocationTargetException, IllegalAccessException {
        for (Method method : finalCleanupMethods) {
            method.invoke(testInstance);
        }
    }
}




