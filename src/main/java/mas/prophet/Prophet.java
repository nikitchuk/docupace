package mas.prophet;

import javassist.*;
import mas.utils.runTime.Step;
import org.junit.*;

import java.util.*;
import java.util.stream.Collectors;

import static mas.prophet.ProphetLogger.append;
import static mas.prophet.ProphetLogger.debug;


public class Prophet {

    private final MethodExtractor methodExtractor = new MethodExtractor();
    private final Class[] reportable = {Step.class, Test.class, After.class, AfterClass.class, Before.class, BeforeClass.class};
    private ClassPool classPool;

    public Prophet() {
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(getClass()));
    }

    public CallNode predictSteps(StackTraceElement[] stackTrace) {
        CallNode stackRoot = getRootMethod(stackTrace);
        if (stackRoot == null)
            return null;
        CallNode callTree = new CallNode(stackRoot);
        callTree = buildTree(callTree, null, 0);
        debug(callTree, stackRoot, "whole tree");
        pruneNonSteps(callTree, null, new ArrayList<>());
        pruneNonSteps(stackRoot, null, new ArrayList<>());
        debug(callTree, stackRoot, "pruned not steps");
        prunePassed(callTree, stackRoot);
        debug(callTree, stackRoot, "pruned passed");
        return callTree;
    }

    private void pruneNonSteps(CallNode node, CallNode parent, List<CallNode> addHere) {
        if (isReportable(node)) {
            addHere.add(node);
            node.setParent(parent);
            List<CallNode> children = new ArrayList<>();
            for (CallNode child : node.getChildren())
                pruneNonSteps(child, node, children);
            node.setChildren(children);
        } else {
            for (CallNode child : node.getChildren())
                pruneNonSteps(child, parent, addHere);
        }
    }

    private boolean isReportable(CallNode node) {
        for (Class c : reportable)
            if (node.getMethod().hasAnnotation(c))
                return true;

        return false;
    }

    private void prunePassed(final CallNode callRoot, final CallNode traceRoot) {
        CallNode stackNode = traceRoot;
        List<CallNode> calls = callRoot.getChildren();
        while (stackNode.getChildren().size() > 0) {
            stackNode = stackNode.getChildren().get(0);
            pruneLevel(calls, stackNode);
            if (calls.size() > 0)
                calls = calls.get(0).getChildren();
            else {
                System.err.println("[ERROR] CANNOT PREDICT ALL STEPS - see prediction.txt");
                append("ERROR : CALL TREE DOES NOT CORRESPOND TO STACK TRACE");
                append("ERROR : " + stackNode);
            }
        }
        pruneFinalLevel(calls, stackNode);
    }

    private void pruneFinalLevel(List<CallNode> calls, CallNode stackNode) {
        for (Iterator<CallNode> iterator = calls.iterator(); iterator.hasNext(); ) {
            CallNode call = iterator.next();
            if (call.getCalledByLine() >= stackNode.getInnerLine())
                break;
            iterator.remove();
        }
    }

    private void pruneLevel(final List<CallNode> calls, final CallNode level) {
        for (Iterator<CallNode> iterator = calls.iterator(); iterator.hasNext(); ) {
            CallNode call = iterator.next();
            if (call.equals(level))
                break;
            iterator.remove();
        }
    }

    private boolean identifyRecursion(CallNode node) {
        CallNode ancestor = node.getParent();
        while (ancestor != null) {
            if (ancestor.getMethod().equals(node.getMethod())) {
                node.setRecursive(true);
                return true;
            }
            ancestor = ancestor.getParent();
        }
        return false;
    }

    private CallNode buildTree(CallNode node, CallNode parent, int depth) {
        node.setParent(parent);
        if (parent == null || !identifyRecursion(node.getParent())) {
            CtMethod method = node.getMethod();
            node.setReportable(isReportable(node));
            if (depth < 80 && method.getLongName().startsWith(" mas")) {

                List<CallNode> children = new ArrayList<>();
                for (CallNode innerCall : methodExtractor.getInternalCalls(method)) {
                    CallNode child = buildTree(innerCall, node, depth + 1);
                    identifyRecursion(child);
                    if (child.isReportable())
                        children.add(child);
                }
                node.setChildren(children);

                if (node.isReportable() && parent != null)
                    parent.setReportable(true);
            }
        }
        return node;
    }

    private CallNode getRootMethod(StackTraceElement[] stackTrace) {
        List<CallNode> stack = Arrays.stream(stackTrace)
                .map(this::findMethod)
                .filter(Objects::nonNull)
                .filter(call -> call.getMethod() != null)
                .collect(Collectors.toList());

        List<CallNode> toRemove = new ArrayList<>();
        for (CallNode node : stack) {
            if (!isReportable(node))
                toRemove.add(node);
            else
                break;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            CallNode node = stack.get(i);
            if (!isReportable(node))
                toRemove.add(node);
            else
                break;
        }

        stack.removeAll(toRemove);
        if (stack.size() == 0)
            return null;

        CallNode child = stack.get(0);
        CallNode parent = child;
        for (int i = 1; i < stack.size(); i++) {
            parent = stack.get(i);
            child.setParent(parent);
            parent.setChildren(Collections.singletonList(child));
            child.setCalledByLine(parent.getInnerLine());
            child = parent;
        }

        return parent;
    }

    private CallNode findMethod(StackTraceElement element) {
        List<CtMethod> methods;
        try {
            CtClass clazz = classPool.getCtClass(element.getClassName());
            methods = Arrays.asList(clazz.getDeclaredMethods(element.getMethodName()));
            methods.sort(Comparator.comparingInt(o -> o.getMethodInfo().getLineNumber(0)));
            if (methods.size() == 0)
                return null;
        } catch (NotFoundException e) {
            return null;
        }

        CtMethod method;
        int i = methods.size();
        do {
            i--;
            method = methods.get(i);
        } while (i > 0 && method.getMethodInfo().getLineNumber(0) > element.getLineNumber());

        return new CallNode(method).setInnerLine(element.getLineNumber());
    }

}
