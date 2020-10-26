package mas.watchers;

import mas.parent.GenericTest;
import mas.utils.cleanUp.ScreenshotTakingErrorCollector;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;


public class CollectorGuard extends RunListener {

    private static ScreenshotTakingErrorCollector collector;
    private GenericTest testInstance;

    public static ScreenshotTakingErrorCollector getCollector() {
        return collector;
    }

    public static void setCollector(ScreenshotTakingErrorCollector collector) {
        CollectorGuard.collector = collector;
    }

    public void setTestInstance(GenericTest testInstance) {
        this.testInstance = testInstance;
    }

    @Override
    public void testFinished(Description description) {
        if (isThisInstance(description)) {
            collector = null;
        }
    }

    private boolean isThisInstance(Description description) {
        if (testInstance == null) {
            return false;
        }
        Class finishedTestClass = description.getTestClass();
        Class thisTestClass = testInstance.getClass();
        return finishedTestClass.equals(thisTestClass);
    }
}
