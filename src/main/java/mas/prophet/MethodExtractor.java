package mas.prophet;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.List;

class MethodExtractor extends ExprEditor {
    private List<CallNode> children = new ArrayList<>();

    @Override
    public void edit(MethodCall m) {
        try {
            children.add(new CallNode(m.getMethod(), m.getLineNumber()));
        } catch (NotFoundException ignored) {
        }
    }

    public List<CallNode> getInternalCalls(CtMethod method) {
        children = new ArrayList<>();

        try {
            method.instrument(this);
        } catch (CannotCompileException ignored) {
        }

        return children;
    }
}