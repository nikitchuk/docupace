package mas.watchers;

import mas.utils.runTime.EventAppender;
import mas.utils.runTime.Step;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
public class LoggingAspect {

    private static final String STEP_POINTCUT = "@annotation( mas.utils.runTime.Step) && execution(* *(..))";

    @Before(STEP_POINTCUT)
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Step step = method.getAnnotation(Step.class);
        List<String> scenarios = TestStartupWatcher.getNames(Arrays.asList(method.getAnnotations()));
        String name = step == null ? method.getName() : step.value();
        EventAppender.startBlock(name, scenarios);
    }

    @AfterReturning(STEP_POINTCUT)
    public void afterReturning() {
        EventAppender.endBlock();
    }

    @AfterThrowing(STEP_POINTCUT)
    public void afterThrowing() {
        EventAppender.endBlock();
    }
}