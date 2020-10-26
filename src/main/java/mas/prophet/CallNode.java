package mas.prophet;

import javassist.CtMethod;

import java.util.ArrayList;
import java.util.List;


public class CallNode {
    private CtMethod method;
    private List<CallNode> children;
    private CallNode parent;
    private boolean reportable;
    private boolean isRecursive;
    private int calledByLine;
    private int innerLine;

    public CallNode(CtMethod method) {
        this.method = method;
    }

    public CallNode(CtMethod method, int line) {
        this.method = method;
        this.calledByLine = line;
    }

    public CallNode(CallNode node) {
        this.method = node.method;
        this.children = node.children;
        this.parent = node.parent;
        this.reportable = node.reportable;
        this.isRecursive = node.isRecursive;
        this.calledByLine = node.calledByLine;
    }

    public int getInnerLine() {
        return innerLine;
    }

    public CallNode setInnerLine(int innerLine) {
        this.innerLine = innerLine;
        return this;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public CallNode setRecursive(boolean recursive) {
        isRecursive = recursive;
        return this;
    }

    public CallNode getParent() {
        return parent;
    }

    public CallNode setParent(CallNode parent) {
        this.parent = parent;
        return this;
    }

    public int getCalledByLine() {
        return calledByLine;
    }

    public CallNode setCalledByLine(int line) {
        this.calledByLine = line;
        return this;
    }

    public boolean isReportable() {
        return reportable;
    }

    public CallNode setReportable(boolean hasStep) {
        this.reportable = hasStep;
        return this;
    }

    public List<CallNode> getChildren() {
        return children != null ? children : new ArrayList<>(0);
    }

    public CallNode setChildren(List<CallNode> children) {
        this.children = children;
        return this;
    }

    public CtMethod getMethod() {
        return method;
    }

    public CallNode setMethod(CtMethod method) {
        this.method = method;
        return this;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + calledByLine;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallNode node = (CallNode) o;

        if (calledByLine != node.calledByLine) return false;
        return method != null ? method.equals(node.method) : node.method == null;

    }

    @Override
    public String toString() {
        String s;
        if (method == null) {
            s = "unknown call";
        } else {
            s = method.getLongName();
            if (calledByLine != 0) {
                s += " @" + calledByLine;
                if (innerLine != 0)
                    s += " >" + innerLine;
            }
        }
        return s;
    }
}
